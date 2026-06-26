package com.terminator.backend.routes

import com.terminator.backend.db.*
import com.terminator.backend.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Route.reportRoutes() {
    route("/api/reports") {
        post("/generate") {
            val request = call.receive<GenerateReportRequest>()
            val userId = 1L
            val reportDate = request.reportDate ?: LocalDate.now().toString()

            val existingReport = transaction {
                Reports.selectAll().where {
                    (Reports.userId eq userId) and
                    (Reports.reportType eq request.reportType) and
                    (Reports.reportDate eq reportDate)
                }.firstOrNull()
            }

            if (existingReport != null) {
                call.respond(HttpStatusCode.OK, ApiResponse(
                    success = true,
                    message = "报告已存在",
                    data = ReportResponse(
                        reportId = existingReport[Reports.reportId],
                        userId = existingReport[Reports.userId],
                        reportType = existingReport[Reports.reportType],
                        reportDate = existingReport[Reports.reportDate],
                        totalConsumption = existingReport[Reports.totalConsumption],
                        totalWarnings = existingReport[Reports.totalWarnings],
                        totalTasks = existingReport[Reports.totalTasks],
                        completedTasks = existingReport[Reports.completedTasks],
                        riskEvents = existingReport[Reports.riskEvents],
                        reportData = existingReport[Reports.reportData],
                        createdAt = existingReport[Reports.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                ))
                return@post
            }

            val reportData = generateReportData(userId, request.reportType, reportDate)

            val result = transaction {
                val reportId = Reports.insert {
                    it[Reports.userId] = userId
                    it[reportType] = request.reportType
                    it[Reports.reportDate] = reportDate
                    it[totalConsumption] = reportData.consumptionSummary.totalAmount
                    it[totalWarnings] = reportData.warningSummary.total
                    it[totalTasks] = reportData.taskSummary.total
                    it[completedTasks] = reportData.taskSummary.completed
                    it[riskEvents] = reportData.riskSummary.total
                    it[Reports.reportData] = Json.encodeToString(reportData)
                    it[createdAt] = LocalDateTime.now()
                } get Reports.reportId

                ReportResponse(
                    reportId = reportId,
                    userId = userId,
                    reportType = request.reportType,
                    reportDate = reportDate,
                    totalConsumption = reportData.consumptionSummary.totalAmount,
                    totalWarnings = reportData.warningSummary.total,
                    totalTasks = reportData.taskSummary.total,
                    completedTasks = reportData.taskSummary.completed,
                    riskEvents = reportData.riskSummary.total,
                    reportData = Json.encodeToString(reportData),
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }

            call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "报告生成成功", data = result))
        }

        get {
            val userId = 1L
            val reportType = call.parameters["type"]

            val reports = transaction {
                val query = Reports.selectAll().where { Reports.userId eq userId }.let {
                    if (reportType != null) it.andWhere { Reports.reportType eq reportType } else it
                }
                query.orderBy(Reports.createdAt, SortOrder.DESC).map {
                    ReportResponse(
                        reportId = it[Reports.reportId],
                        userId = it[Reports.userId],
                        reportType = it[Reports.reportType],
                        reportDate = it[Reports.reportDate],
                        totalConsumption = it[Reports.totalConsumption],
                        totalWarnings = it[Reports.totalWarnings],
                        totalTasks = it[Reports.totalTasks],
                        completedTasks = it[Reports.completedTasks],
                        riskEvents = it[Reports.riskEvents],
                        reportData = it[Reports.reportData],
                        createdAt = it[Reports.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }

            call.respond(ApiResponse(success = true, message = "查询成功", data = reports))
        }

        get("/{reportId}") {
            val reportId = call.parameters["reportId"]?.toLongOrNull()
            if (reportId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的报告ID"))
                return@get
            }

            val report = transaction {
                Reports.selectAll().where { Reports.reportId eq reportId }.firstOrNull()?.let {
                    ReportResponse(
                        reportId = it[Reports.reportId],
                        userId = it[Reports.userId],
                        reportType = it[Reports.reportType],
                        reportDate = it[Reports.reportDate],
                        totalConsumption = it[Reports.totalConsumption],
                        totalWarnings = it[Reports.totalWarnings],
                        totalTasks = it[Reports.totalTasks],
                        completedTasks = it[Reports.completedTasks],
                        riskEvents = it[Reports.riskEvents],
                        reportData = it[Reports.reportData],
                        createdAt = it[Reports.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }

            if (report != null) {
                call.respond(ApiResponse(success = true, message = "查询成功", data = report))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "报告不存在"))
            }
        }

        get("/latest") {
            val userId = 1L
            val reportType = call.parameters["type"] ?: "daily"

            val report = transaction {
                Reports.selectAll().where {
                    (Reports.userId eq userId) and (Reports.reportType eq reportType)
                }.orderBy(Reports.createdAt, SortOrder.DESC).firstOrNull()?.let {
                    ReportResponse(
                        reportId = it[Reports.reportId],
                        userId = it[Reports.userId],
                        reportType = it[Reports.reportType],
                        reportDate = it[Reports.reportDate],
                        totalConsumption = it[Reports.totalConsumption],
                        totalWarnings = it[Reports.totalWarnings],
                        totalTasks = it[Reports.totalTasks],
                        completedTasks = it[Reports.completedTasks],
                        riskEvents = it[Reports.riskEvents],
                        reportData = it[Reports.reportData],
                        createdAt = it[Reports.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }

            if (report != null) {
                call.respond(ApiResponse(success = true, message = "查询成功", data = report))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "暂无报告"))
            }
        }

        delete("/{reportId}") {
            val reportId = call.parameters["reportId"]?.toLongOrNull()
            if (reportId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的报告ID"))
                return@delete
            }

            transaction {
                Reports.deleteWhere { Reports.reportId eq reportId }
            }

            call.respond(ApiResponse<Nothing>(success = true, message = "报告已删除"))
        }
    }
}

private fun generateReportData(userId: Long, reportType: String, reportDate: String): DailyReportData {
    val date = LocalDate.parse(reportDate)
    val startDate = when (reportType) {
        "daily" -> date.atStartOfDay()
        "weekly" -> date.minusDays(6).atStartOfDay()
        "monthly" -> date.withDayOfMonth(1).atStartOfDay()
        else -> date.atStartOfDay()
    }
    val endDate = date.plusDays(1).atStartOfDay()

    val taskSummary = transaction {
        val total = TaskExecutions.selectAll().where {
            (TaskExecutions.userId eq userId) and
            (TaskExecutions.createdAt greaterEq startDate) and
            (TaskExecutions.createdAt less endDate)
        }.count()

        val completed = TaskExecutions.selectAll().where {
            (TaskExecutions.userId eq userId) and
            (TaskExecutions.status eq "completed") and
            (TaskExecutions.createdAt greaterEq startDate) and
            (TaskExecutions.createdAt less endDate)
        }.count()

        val failed = TaskExecutions.selectAll().where {
            (TaskExecutions.userId eq userId) and
            (TaskExecutions.status eq "failed") and
            (TaskExecutions.createdAt greaterEq startDate) and
            (TaskExecutions.createdAt less endDate)
        }.count()

        TaskSummaryData(
            total = total.toInt(),
            completed = completed.toInt(),
            failed = failed.toInt(),
            pending = (total - completed - failed).toInt()
        )
    }

    val riskSummary = transaction {
        val total = RiskEvents.selectAll().where {
            (RiskEvents.userId eq userId) and
            (RiskEvents.createdAt greaterEq startDate) and
            (RiskEvents.createdAt less endDate)
        }.count()

        val high = RiskEvents.selectAll().where {
            (RiskEvents.userId eq userId) and
            (RiskEvents.severity eq "high") and
            (RiskEvents.createdAt greaterEq startDate) and
            (RiskEvents.createdAt less endDate)
        }.count()

        val medium = RiskEvents.selectAll().where {
            (RiskEvents.userId eq userId) and
            (RiskEvents.severity eq "medium") and
            (RiskEvents.createdAt greaterEq startDate) and
            (RiskEvents.createdAt less endDate)
        }.count()

        val low = RiskEvents.selectAll().where {
            (RiskEvents.userId eq userId) and
            (RiskEvents.severity eq "low") and
            (RiskEvents.createdAt greaterEq startDate) and
            (RiskEvents.createdAt less endDate)
        }.count()

        RiskSummaryData(
            total = total.toInt(),
            high = high.toInt(),
            medium = medium.toInt(),
            low = low.toInt()
        )
    }

    val warningSummary = transaction {
        val total = ConsumptionWarnings.selectAll().where {
            (ConsumptionWarnings.userId eq userId) and
            (ConsumptionWarnings.createdAt greaterEq startDate) and
            (ConsumptionWarnings.createdAt less endDate)
        }.count().toInt()

        val levels = listOf("critical", "high", "medium", "low").associateWith { level ->
            ConsumptionWarnings.selectAll().where {
                (ConsumptionWarnings.userId eq userId) and
                (ConsumptionWarnings.warningLevel eq level) and
                (ConsumptionWarnings.createdAt greaterEq startDate) and
                (ConsumptionWarnings.createdAt less endDate)
            }.count().toInt()
        }

        WarningSummaryData(total = total, byLevel = levels)
    }

    val consumptionSummary = transaction {
        val totalAmount = ConsumptionWarnings.select(ConsumptionWarnings.currentAmount.sum())
            .where {
                (ConsumptionWarnings.userId eq userId) and
                (ConsumptionWarnings.createdAt greaterEq startDate) and
                (ConsumptionWarnings.createdAt less endDate)
            }.firstOrNull()?.get(ConsumptionWarnings.currentAmount.sum()) ?: 0.0

        val appBreakdown = ConsumptionWarnings.select(
            ConsumptionWarnings.appId,
            ConsumptionWarnings.currentAmount.sum()
        ).where {
            (ConsumptionWarnings.userId eq userId) and
            (ConsumptionWarnings.createdAt greaterEq startDate) and
            (ConsumptionWarnings.createdAt less endDate)
        }.groupBy(ConsumptionWarnings.appId).map { row ->
            val appId = row[ConsumptionWarnings.appId]
            val appName = HealthApps.selectAll().where { HealthApps.appId eq appId }
                .firstOrNull()?.get(HealthApps.appName) ?: "未知应用"
            AppConsumption(
                appId = appId,
                appName = appName,
                amount = row[ConsumptionWarnings.currentAmount.sum()] ?: 0.0
            )
        }

        ConsumptionSummary(totalAmount = totalAmount, appBreakdown = appBreakdown)
    }

    return DailyReportData(
        date = reportDate,
        consumptionSummary = consumptionSummary,
        taskSummary = taskSummary,
        riskSummary = riskSummary,
        warningSummary = warningSummary
    )
}
