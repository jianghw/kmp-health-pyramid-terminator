package com.terminator.backend.routes

import com.terminator.backend.db.HealthApps
import com.terminator.backend.db.TaskTemplates
import com.terminator.backend.model.ApiResponse
import com.terminator.backend.model.CreateAppRequest
import com.terminator.backend.model.CreateTaskTemplateRequest
import com.terminator.backend.model.HealthAppResponse
import com.terminator.backend.model.TaskTemplateResponse
import com.terminator.backend.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Route.appRoutes(authService: AuthService) {
    route("/api/apps") {
        get {
            val apps = transaction {
                HealthApps.selectAll().map {
                    HealthAppResponse(
                        appId = it[HealthApps.appId],
                        appName = it[HealthApps.appName],
                        appType = it[HealthApps.appType],
                        appIcon = it[HealthApps.appIcon],
                        riskScore = it[HealthApps.riskScore],
                        status = it[HealthApps.status],
                        config = it[HealthApps.config],
                        createdAt = it[HealthApps.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }
            call.respond(ApiResponse(success = true, message = "查询成功", data = apps))
        }
        
        post {
            val request = call.receive<CreateAppRequest>()
            
            val result = transaction {
                val appId = HealthApps.insert {
                    it[appName] = request.appName
                    it[appType] = request.appType
                    it[appIcon] = request.appIcon ?: ""
                    it[riskScore] = request.riskScore ?: 0
                    it[status] = "enabled"
                    it[config] = request.config ?: "{}"
                    it[createdAt] = LocalDateTime.now()
                } get HealthApps.appId
                
                HealthAppResponse(
                    appId = appId,
                    appName = request.appName,
                    appType = request.appType,
                    appIcon = request.appIcon ?: "",
                    riskScore = request.riskScore ?: 0,
                    status = "enabled",
                    config = request.config ?: "{}",
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }
            
            call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "创建成功", data = result))
        }
        
        get("/{appId}") {
            val appId = call.parameters["appId"]?.toLongOrNull()
            if (appId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的应用ID"))
                return@get
            }
            
            val app = transaction {
                HealthApps.selectAll().where { HealthApps.appId eq appId }.firstOrNull()?.let {
                    HealthAppResponse(
                        appId = it[HealthApps.appId],
                        appName = it[HealthApps.appName],
                        appType = it[HealthApps.appType],
                        appIcon = it[HealthApps.appIcon],
                        riskScore = it[HealthApps.riskScore],
                        status = it[HealthApps.status],
                        config = it[HealthApps.config],
                        createdAt = it[HealthApps.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }
            
            if (app != null) {
                call.respond(ApiResponse(success = true, message = "查询成功", data = app))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "应用不存在"))
            }
        }
        
        get("/{appId}/tasks") {
            val appId = call.parameters["appId"]?.toLongOrNull()
            if (appId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的应用ID"))
                return@get
            }
            
            val tasks = transaction {
                TaskTemplates.selectAll().where { TaskTemplates.appId eq appId }.map {
                    TaskTemplateResponse(
                        templateId = it[TaskTemplates.templateId],
                        appId = it[TaskTemplates.appId],
                        taskName = it[TaskTemplates.taskName],
                        taskType = it[TaskTemplates.taskType],
                        templateConfig = it[TaskTemplates.templateConfig],
                        estimatedMinutes = it[TaskTemplates.estimatedMinutes],
                        rewardPoints = it[TaskTemplates.rewardPoints],
                        status = it[TaskTemplates.status],
                        createdAt = it[TaskTemplates.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }
            
            call.respond(ApiResponse(success = true, message = "查询成功", data = tasks))
        }
        
        post("/{appId}/tasks") {
            val appId = call.parameters["appId"]?.toLongOrNull()
            if (appId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的应用ID"))
                return@post
            }
            
            val request = call.receive<CreateTaskTemplateRequest>()
            
            val result = transaction {
                val templateId = TaskTemplates.insert {
                    it[TaskTemplates.appId] = appId
                    it[taskName] = request.taskName
                    it[taskType] = request.taskType
                    it[templateConfig] = request.templateConfig ?: "{}"
                    it[estimatedMinutes] = request.estimatedMinutes ?: 5
                    it[rewardPoints] = request.rewardPoints ?: 10
                    it[status] = "enabled"
                    it[createdAt] = LocalDateTime.now()
                } get TaskTemplates.templateId
                
                TaskTemplateResponse(
                    templateId = templateId,
                    appId = appId,
                    taskName = request.taskName,
                    taskType = request.taskType,
                    templateConfig = request.templateConfig ?: "{}",
                    estimatedMinutes = request.estimatedMinutes ?: 5,
                    rewardPoints = request.rewardPoints ?: 10,
                    status = "enabled",
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }
            
            call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "创建成功", data = result))
        }
    }
}
