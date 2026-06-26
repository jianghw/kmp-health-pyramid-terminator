package com.terminator.backend.routes

import com.terminator.backend.db.ConsumptionRules
import com.terminator.backend.db.ConsumptionWarnings
import com.terminator.backend.db.HealthApps
import com.terminator.backend.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Route.consumptionWarningRoutes() {
    route("/api/warnings") {
        post("/evaluate") {
            val request = call.receive<EvaluateConsumptionRequest>()
            val userId = 1L

            val result = transaction {
                val app = HealthApps.selectAll().where { HealthApps.appId eq request.appId }.firstOrNull()
                    ?: return@transaction null

                val rules = ConsumptionRules.selectAll().where {
                    (ConsumptionRules.userId eq userId) and
                    (ConsumptionRules.isEnabled eq true)
                }.toList()

                val defaultRules = listOf(
                    Triple("daily_limit", 200.0, "daily"),
                    Triple("single_limit", 100.0, "single"),
                    Triple("frequency_limit", 5.0, "hourly")
                )

                val activeRules = if (rules.isEmpty()) defaultRules else rules.map {
                    Triple(it[ConsumptionRules.ruleType], it[ConsumptionRules.thresholdAmount], it[ConsumptionRules.timeWindow])
                }

                val violatedRule = activeRules.firstOrNull { (ruleType, threshold, timeWindow) ->
                    evaluateRule(userId, request.appId, request.amount, ruleType, threshold, timeWindow)
                }

                if (violatedRule != null) {
                    val (ruleType, threshold, _) = violatedRule
                    val warningLevel = when {
                        request.amount >= threshold * 2 -> "critical"
                        request.amount >= threshold * 1.5 -> "high"
                        request.amount >= threshold -> "medium"
                        else -> "low"
                    }

                    val warningId = ConsumptionWarnings.insert {
                        it[ConsumptionWarnings.userId] = userId
                        it[ConsumptionWarnings.appId] = request.appId
                        it[ConsumptionWarnings.ruleType] = ruleType
                        it[ConsumptionWarnings.warningLevel] = warningLevel
                        it[title] = getWarningTitle(ruleType)
                        it[message] = getWarningMessage(ruleType, request.amount, threshold)
                        it[currentAmount] = request.amount
                        it[thresholdAmount] = threshold
                        it[status] = "active"
                        it[createdAt] = LocalDateTime.now()
                    } get ConsumptionWarnings.warningId

                    val warning = ConsumptionWarningResponse(
                        warningId = warningId,
                        userId = userId,
                        appId = request.appId,
                        ruleType = ruleType,
                        warningLevel = warningLevel,
                        title = getWarningTitle(ruleType),
                        message = getWarningMessage(ruleType, request.amount, threshold),
                        currentAmount = request.amount,
                        thresholdAmount = threshold,
                        status = "active",
                        createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )

                    EvaluateConsumptionResponse(
                        allowed = warningLevel == "low",
                        warning = warning,
                        message = if (warningLevel == "low") "消费金额在安全范围内" else "消费金额超过预警阈值，已触发预警"
                    )
                } else {
                    EvaluateConsumptionResponse(
                        allowed = true,
                        warning = null,
                        message = "消费金额在安全范围内"
                    )
                }
            }

            if (result != null) {
                call.respond(ApiResponse(success = true, message = "评估完成", data = result))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "应用不存在"))
            }
        }

        get {
            val userId = 1L
            val warnings = transaction {
                ConsumptionWarnings.selectAll().where {
                    ConsumptionWarnings.userId eq userId
                }.orderBy(ConsumptionWarnings.createdAt, SortOrder.DESC).map {
                    ConsumptionWarningResponse(
                        warningId = it[ConsumptionWarnings.warningId],
                        userId = it[ConsumptionWarnings.userId],
                        appId = it[ConsumptionWarnings.appId],
                        ruleType = it[ConsumptionWarnings.ruleType],
                        warningLevel = it[ConsumptionWarnings.warningLevel],
                        title = it[ConsumptionWarnings.title],
                        message = it[ConsumptionWarnings.message],
                        currentAmount = it[ConsumptionWarnings.currentAmount],
                        thresholdAmount = it[ConsumptionWarnings.thresholdAmount],
                        status = it[ConsumptionWarnings.status],
                        createdAt = it[ConsumptionWarnings.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }
            call.respond(ApiResponse(success = true, message = "查询成功", data = warnings))
        }

        get("/{warningId}") {
            val warningId = call.parameters["warningId"]?.toLongOrNull()
            if (warningId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的预警ID"))
                return@get
            }

            val warning = transaction {
                ConsumptionWarnings.selectAll().where {
                    ConsumptionWarnings.warningId eq warningId
                }.firstOrNull()?.let {
                    ConsumptionWarningResponse(
                        warningId = it[ConsumptionWarnings.warningId],
                        userId = it[ConsumptionWarnings.userId],
                        appId = it[ConsumptionWarnings.appId],
                        ruleType = it[ConsumptionWarnings.ruleType],
                        warningLevel = it[ConsumptionWarnings.warningLevel],
                        title = it[ConsumptionWarnings.title],
                        message = it[ConsumptionWarnings.message],
                        currentAmount = it[ConsumptionWarnings.currentAmount],
                        thresholdAmount = it[ConsumptionWarnings.thresholdAmount],
                        status = it[ConsumptionWarnings.status],
                        createdAt = it[ConsumptionWarnings.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }

            if (warning != null) {
                call.respond(ApiResponse(success = true, message = "查询成功", data = warning))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "预警记录不存在"))
            }
        }

        put("/{warningId}/dismiss") {
            val warningId = call.parameters["warningId"]?.toLongOrNull()
            if (warningId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的预警ID"))
                return@put
            }

            transaction {
                ConsumptionWarnings.update({ ConsumptionWarnings.warningId eq warningId }) {
                    it[status] = "dismissed"
                }
            }

            call.respond(ApiResponse<Nothing>(success = true, message = "预警已忽略"))
        }
    }

    route("/api/consumption-rules") {
        post {
            val userId = 1L
            val request = call.receive<CreateConsumptionRuleRequest>()

            val result = transaction {
                val ruleId = ConsumptionRules.insert {
                    it[ConsumptionRules.userId] = userId
                    it[ruleType] = request.ruleType
                    it[ruleName] = request.ruleName
                    it[thresholdAmount] = request.thresholdAmount
                    it[timeWindow] = request.timeWindow
                    it[isEnabled] = true
                    it[createdAt] = LocalDateTime.now()
                } get ConsumptionRules.ruleId

                ConsumptionRuleResponse(
                    ruleId = ruleId,
                    userId = userId,
                    ruleType = request.ruleType,
                    ruleName = request.ruleName,
                    thresholdAmount = request.thresholdAmount,
                    timeWindow = request.timeWindow,
                    isEnabled = true,
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }

            call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "规则创建成功", data = result))
        }

        get {
            val userId = 1L
            val rules = transaction {
                ConsumptionRules.selectAll().where {
                    ConsumptionRules.userId eq userId
                }.map {
                    ConsumptionRuleResponse(
                        ruleId = it[ConsumptionRules.ruleId],
                        userId = it[ConsumptionRules.userId],
                        ruleType = it[ConsumptionRules.ruleType],
                        ruleName = it[ConsumptionRules.ruleName],
                        thresholdAmount = it[ConsumptionRules.thresholdAmount],
                        timeWindow = it[ConsumptionRules.timeWindow],
                        isEnabled = it[ConsumptionRules.isEnabled],
                        createdAt = it[ConsumptionRules.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }
            call.respond(ApiResponse(success = true, message = "查询成功", data = rules))
        }

        put("/{ruleId}") {
            val ruleId = call.parameters["ruleId"]?.toLongOrNull()
            if (ruleId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的规则ID"))
                return@put
            }

            val request = call.receive<CreateConsumptionRuleRequest>()

            transaction {
                ConsumptionRules.update({ ConsumptionRules.ruleId eq ruleId }) {
                    it[ruleType] = request.ruleType
                    it[ruleName] = request.ruleName
                    it[thresholdAmount] = request.thresholdAmount
                    it[timeWindow] = request.timeWindow
                }
            }

            call.respond(ApiResponse<Nothing>(success = true, message = "规则更新成功"))
        }

        delete("/{ruleId}") {
            val ruleId = call.parameters["ruleId"]?.toLongOrNull()
            if (ruleId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的规则ID"))
                return@delete
            }

            transaction {
                ConsumptionRules.deleteWhere { ConsumptionRules.ruleId eq ruleId }
            }

            call.respond(ApiResponse<Nothing>(success = true, message = "规则删除成功"))
        }

        put("/{ruleId}/toggle") {
            val ruleId = call.parameters["ruleId"]?.toLongOrNull()
            if (ruleId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的规则ID"))
                return@put
            }

            val rule = transaction {
                ConsumptionRules.selectAll().where { ConsumptionRules.ruleId eq ruleId }.firstOrNull()
            }

            if (rule == null) {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "规则不存在"))
                return@put
            }

            transaction {
                ConsumptionRules.update({ ConsumptionRules.ruleId eq ruleId }) {
                    it[isEnabled] = !rule[ConsumptionRules.isEnabled]
                }
            }

            call.respond(ApiResponse<Nothing>(success = true, message = "规则状态已更新"))
        }
    }
}

private fun evaluateRule(
    userId: Long,
    appId: Long,
    amount: Double,
    ruleType: String,
    threshold: Double,
    timeWindow: String
): Boolean {
    return when (ruleType) {
        "daily_limit" -> {
            val todayStart = LocalDateTime.now().toLocalDate().atStartOfDay()
            val todayTotal = ConsumptionWarnings.select(ConsumptionWarnings.currentAmount.sum())
                .where {
                    (ConsumptionWarnings.userId eq userId) and
                    (ConsumptionWarnings.appId eq appId) and
                    (ConsumptionWarnings.createdAt greaterEq todayStart)
                }.firstOrNull()?.get(ConsumptionWarnings.currentAmount.sum()) ?: 0.0
            (todayTotal + amount) >= threshold
        }
        "single_limit" -> amount >= threshold
        "frequency_limit" -> {
            val hourAgo = LocalDateTime.now().minusHours(1)
            val recentCount = ConsumptionWarnings.selectAll().where {
                (ConsumptionWarnings.userId eq userId) and
                (ConsumptionWarnings.appId eq appId) and
                (ConsumptionWarnings.createdAt greaterEq hourAgo)
            }.count()
            recentCount >= threshold.toLong()
        }
        else -> false
    }
}

private fun getWarningTitle(ruleType: String): String {
    return when (ruleType) {
        "daily_limit" -> "每日消费限额预警"
        "single_limit" -> "单笔消费限额预警"
        "frequency_limit" -> "消费频率预警"
        else -> "消费预警"
    }
}

private fun getWarningMessage(ruleType: String, current: Double, threshold: Double): String {
    return when (ruleType) {
        "daily_limit" -> "今日累计消费 ¥%.2f，已超过每日限额 ¥%.2f，请注意控制消费。".format(current, threshold)
        "single_limit" -> "本次消费 ¥%.2f，超过单笔限额 ¥%.2f，建议取消或减少消费。".format(current, threshold)
        "frequency_limit" -> "近期消费频率过高，已达 ${current.toInt()} 次，超过限制 ${threshold.toInt()} 次/小时。"
        else -> "消费行为触发预警规则，请注意消费安全。"
    }
}
