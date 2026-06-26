package com.terminator.backend.routes

import com.terminator.backend.db.TaskExecutions
import com.terminator.backend.db.TaskTemplates
import com.terminator.backend.model.ApiResponse
import com.terminator.backend.model.ExecuteTaskRequest
import com.terminator.backend.model.TaskExecutionResponse
import com.terminator.backend.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Route.taskRoutes(authService: AuthService) {
    route("/api/tasks") {
        post("/execute") {
            val request = call.receive<ExecuteTaskRequest>()
            
            val result = transaction {
                val template = TaskTemplates.selectAll().where { TaskTemplates.templateId eq request.templateId!! }.firstOrNull()
                if (template == null) {
                    return@transaction null
                }
                
                val executionId = TaskExecutions.insert {
                    it[userId] = 1
                    it[templateId] = request.templateId!!
                    it[status] = "pending"
                    it[createdAt] = LocalDateTime.now()
                } get TaskExecutions.executionId
                
                TaskExecutionResponse(
                    executionId = executionId,
                    userId = 1,
                    templateId = request.templateId!!,
                    status = "pending",
                    startedAt = null,
                    completedAt = null,
                    resultData = null,
                    errorMessage = null,
                    retryCount = 0,
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }
            
            if (result != null) {
                call.respond(ApiResponse(success = true, message = "任务已提交", data = result))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "任务模板不存在"))
            }
        }
        
        post("/batch-execute") {
            val request = call.receive<ExecuteTaskRequest>()
            
            val results = transaction {
                request.templateIds.map { templateId ->
                    val executionId = TaskExecutions.insert {
                        it[userId] = 1
                        it[TaskExecutions.templateId] = templateId
                        it[status] = "pending"
                        it[createdAt] = LocalDateTime.now()
                    } get TaskExecutions.executionId
                    
                    TaskExecutionResponse(
                        executionId = executionId,
                        userId = 1,
                        templateId = templateId,
                        status = "pending",
                        startedAt = null,
                        completedAt = null,
                        resultData = null,
                        errorMessage = null,
                        retryCount = 0,
                        createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }
            
            call.respond(ApiResponse(success = true, message = "批量任务已提交", data = results))
        }
        
        get("/{executionId}/status") {
            val executionId = call.parameters["executionId"]?.toLongOrNull()
            if (executionId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的执行ID"))
                return@get
            }
            
            val execution = transaction {
                TaskExecutions.selectAll().where { TaskExecutions.executionId eq executionId }.firstOrNull()?.let {
                    TaskExecutionResponse(
                        executionId = it[TaskExecutions.executionId],
                        userId = it[TaskExecutions.userId],
                        templateId = it[TaskExecutions.templateId],
                        status = it[TaskExecutions.status],
                        startedAt = it[TaskExecutions.startedAt]?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        completedAt = it[TaskExecutions.completedAt]?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        resultData = it[TaskExecutions.resultData],
                        errorMessage = it[TaskExecutions.errorMessage],
                        retryCount = it[TaskExecutions.retryCount],
                        createdAt = it[TaskExecutions.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }
            
            if (execution != null) {
                call.respond(ApiResponse(success = true, message = "查询成功", data = execution))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "执行记录不存在"))
            }
        }
        
        get("/history") {
            val executions = transaction {
                TaskExecutions.selectAll().map {
                    TaskExecutionResponse(
                        executionId = it[TaskExecutions.executionId],
                        userId = it[TaskExecutions.userId],
                        templateId = it[TaskExecutions.templateId],
                        status = it[TaskExecutions.status],
                        startedAt = it[TaskExecutions.startedAt]?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        completedAt = it[TaskExecutions.completedAt]?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        resultData = it[TaskExecutions.resultData],
                        errorMessage = it[TaskExecutions.errorMessage],
                        retryCount = it[TaskExecutions.retryCount],
                        createdAt = it[TaskExecutions.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }
            
            call.respond(ApiResponse(success = true, message = "查询成功", data = executions))
        }
    }
}
