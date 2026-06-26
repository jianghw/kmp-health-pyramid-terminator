package com.terminator.backend.routes

import com.terminator.backend.db.HealthApps
import com.terminator.backend.db.TaskExecutions
import com.terminator.backend.db.TaskTemplates
import com.terminator.backend.model.*
import com.terminator.backend.service.AIService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 一键执行模式路由 - 为老年人提供简化的任务执行入口
 *
 * 核心理念：老年人只需点击一个按钮，系统自动完成所有任务。
 *
 * API端点：
 * - POST /api/one-tap/execute: 一键执行所有待处理任务
 * - GET /api/one-tap/status: 获取一键执行的状态摘要
 */
fun Route.oneTapRoutes(aiService: AIService) {
    route("/api/one-tap") {

        /**
         * POST /api/one-tap/execute - 一键执行所有待处理任务
         *
         * 系统会自动：
         * 1. 查找所有活跃的健康App
         * 2. 获取每个App的待处理任务
         * 3. 依次执行任务（支持AI答题）
         * 4. 返回执行结果摘要
         *
         * 响应示例：
         * {
         *   "success": true,
         *   "data": {
         *     "totalTasks": 10,
         *     "completed": 8,
         *     "failed": 2,
         *     "results": [...]
         *   }
         * }
         */
        post("/execute") {
            val userId = 1L // TODO: 从JWT中获取用户ID

            try {
                val results = executeAllTasks(userId, aiService)
                call.respond(ApiResponse(success = true, message = "一键执行完成", data = results))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ApiResponse<Nothing>(success = false, message = "执行失败: ${e.message}"))
            }
        }

        /**
         * GET /api/one-tap/status - 获取一键执行的状态摘要
         *
         * 返回：
         * - 今日待处理任务数量
         * - 今日已完成任务数量
         * - 最近一次执行时间
         * - 各App的任务状态
         */
        get("/status") {
            val userId = 1L // TODO: 从JWT中获取用户ID

            try {
                val status = getExecutionStatus(userId)
                call.respond(ApiResponse(success = true, message = "查询成功", data = status))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ApiResponse<Nothing>(success = false, message = "查询失败: ${e.message}"))
            }
        }
    }
}

/**
 * 执行结果数据类
 */
@kotlinx.serialization.Serializable
data class OneTapResult(
    @kotlinx.serialization.SerialName("total_tasks")
    val totalTasks: Int,
    val completed: Int,
    val failed: Int,
    val skipped: Int,
    @kotlinx.serialization.SerialName("execution_time_ms")
    val executionTimeMs: Long,
    val results: List<TaskResultSummary>
)

/**
 * 单个任务执行结果摘要
 */
@kotlinx.serialization.Serializable
data class TaskResultSummary(
    @kotlinx.serialization.SerialName("app_name")
    val appName: String,
    @kotlinx.serialization.SerialName("task_name")
    val taskName: String,
    val status: String,
    val message: String
)

/**
 * 一键执行状态摘要
 */
@kotlinx.serialization.Serializable
data class OneTapStatus(
    @kotlinx.serialization.SerialName("pending_count")
    val pendingCount: Int,
    @kotlinx.serialization.SerialName("completed_count")
    val completedCount: Int,
    @kotlinx.serialization.SerialName("last_execution_time")
    val lastExecutionTime: String?,
    @kotlinx.serialization.SerialName("app_statuses")
    val appStatuses: List<AppTaskStatus>
)

/**
 * 单个App的任务状态
 */
@kotlinx.serialization.Serializable
data class AppTaskStatus(
    @kotlinx.serialization.SerialName("app_name")
    val appName: String,
    @kotlinx.serialization.SerialName("total_tasks")
    val totalTasks: Int,
    @kotlinx.serialization.SerialName("completed_tasks")
    val completedTasks: Int
)

/**
 * 执行所有待处理任务
 *
 * 这是一键执行的核心逻辑：
 * 1. 获取用户的所有活跃App
 * 2. 获取每个App的任务模板
 * 3. 按顺序执行任务
 * 4. 记录执行结果
 */
private suspend fun executeAllTasks(userId: Long, aiService: AIService): OneTapResult {
    val startTime = System.currentTimeMillis()
    val results = mutableListOf<TaskResultSummary>()
    var completed = 0
    var failed = 0
    var skipped = 0

    // 获取用户的活跃App和任务模板
    val appsWithTasks = transaction {
        HealthApps.selectAll().map { app ->
            val appTasks = TaskTemplates.selectAll().where {
                TaskTemplates.appId eq app[HealthApps.appId]
            }.toList()
            Triple(app[HealthApps.appId], app[HealthApps.appName], appTasks)
        }
    }

    // 依次执行每个App的任务
    for ((appId, appName, tasks) in appsWithTasks) {
        for (task in tasks) {
            val taskName = task[TaskTemplates.taskName]
            val taskType = task[TaskTemplates.taskType]

            try {
                // 检查任务是否已在今日执行过
                val alreadyExecuted = transaction {
                    TaskExecutions.selectAll().where {
                        (TaskExecutions.templateId eq task[TaskTemplates.templateId]) and
                        (TaskExecutions.createdAt greaterEq LocalDateTime.now().toLocalDate().atStartOfDay())
                    }.count() > 0
                }

                if (alreadyExecuted) {
                    skipped++
                    results.add(TaskResultSummary(appName, taskName, "skipped", "今日已执行"))
                    continue
                }

                // 模拟任务执行（实际项目中这里会调用真正的自动化逻辑）
                val executionSuccess = simulateTaskExecution(taskType, aiService)

                // 记录执行结果
                transaction {
                    TaskExecutions.insert {
                        it[TaskExecutions.templateId] = task[TaskTemplates.templateId]
                        it[TaskExecutions.userId] = userId
                        it[TaskExecutions.status] = if (executionSuccess) "completed" else "failed"
                        it[TaskExecutions.createdAt] = LocalDateTime.now()
                        it[TaskExecutions.resultData] = if (executionSuccess) "任务执行成功" else "任务执行失败"
                    }
                }

                if (executionSuccess) {
                    completed++
                    results.add(TaskResultSummary(appName, taskName, "completed", "执行成功"))
                } else {
                    failed++
                    results.add(TaskResultSummary(appName, taskName, "failed", "执行失败"))
                }
            } catch (e: Exception) {
                failed++
                results.add(TaskResultSummary(appName, taskName, "error", e.message ?: "未知错误"))
            }
        }
    }

    val totalTime = System.currentTimeMillis() - startTime

    return OneTapResult(
        totalTasks = completed + failed + skipped,
        completed = completed,
        failed = failed,
        skipped = skipped,
        executionTimeMs = totalTime,
        results = results
    )
}

/**
 * 模拟任务执行（实际项目中应替换为真正的自动化逻辑）
 *
 * 根据任务类型模拟不同的执行场景：
 * - sign_in: 签到任务（成功率高）
 * - watch_video: 看视频任务（成功率中等）
 * - answer_question: 答题任务（可能需要AI帮助）
 * - read_article: 阅读任务（成功率高）
 * - exchange: 兑换任务（成功率低）
 */
private suspend fun simulateTaskExecution(taskType: String, aiService: AIService): Boolean {
    // 模拟执行延迟
    delay((500..2000).random().toLong())

    return when (taskType) {
        "sign_in" -> true  // 签到任务成功率高
        "watch_video" -> (1..10).random() <= 8  // 80%成功率
        "answer_question" -> {
            // 答题任务：尝试使用AI回答
            try {
                val response = aiService.askQuestion(
                    question = "这是一道测试题目",
                    questionType = "single_choice"
                )
                response.answer.isNotBlank()
            } catch (e: Exception) {
                false
            }
        }
        "read_article" -> (1..10).random() <= 9  // 90%成功率
        "exchange" -> (1..10).random() <= 6  // 60%成功率
        else -> true
    }
}

/**
 * 获取执行状态摘要
 */
private fun getExecutionStatus(userId: Long): OneTapStatus {
    val today = LocalDateTime.now().toLocalDate().atStartOfDay()

    return transaction {
        val totalTemplates = TaskTemplates.selectAll().count().toInt()

        val completedCount = TaskExecutions.selectAll().where {
            (TaskExecutions.userId eq userId) and
            (TaskExecutions.createdAt greaterEq today) and
            (TaskExecutions.status eq "completed")
        }.count().toInt()

        val lastExecution = TaskExecutions.selectAll().where { TaskExecutions.userId eq userId }
            .orderBy(TaskExecutions.createdAt to SortOrder.DESC)
            .limit(1)
            .firstOrNull()
            ?.get(TaskExecutions.createdAt)
            ?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        val appStatuses = HealthApps.selectAll().map { app ->
            val appTemplates = TaskTemplates.selectAll().where { TaskTemplates.appId eq app[HealthApps.appId] }
                .map { it[TaskTemplates.templateId] }
            val totalTasks = appTemplates.size
            val completedTasks = if (appTemplates.isNotEmpty()) {
                TaskExecutions.selectAll().where {
                    (TaskExecutions.userId eq userId) and
                    (TaskExecutions.templateId inList appTemplates) and
                    (TaskExecutions.status eq "completed") and
                    (TaskExecutions.createdAt greaterEq today)
                }.count().toInt()
            } else 0

            AppTaskStatus(
                appName = app[HealthApps.appName],
                totalTasks = totalTasks,
                completedTasks = completedTasks
            )
        }

        OneTapStatus(
            pendingCount = totalTemplates - completedCount,
            completedCount = completedCount,
            lastExecutionTime = lastExecution,
            appStatuses = appStatuses
        )
    }
}
