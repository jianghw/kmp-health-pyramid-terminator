package com.terminator.shared.network.api

import com.terminator.shared.model.ApiResponse
import com.terminator.shared.model.HealthApp
import com.terminator.shared.model.TaskExecution
import com.terminator.shared.model.TaskTemplate
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class TaskApi(private val client: HttpClient) {
    
    suspend fun getApps(): ApiResponse<List<HealthApp>> {
        return client.get("/api/apps").body()
    }
    
    suspend fun getAppTasks(appId: Long): ApiResponse<List<TaskTemplate>> {
        return client.get("/api/apps/$appId/tasks").body()
    }
    
    suspend fun executeTask(templateId: Long): ApiResponse<TaskExecution> {
        return client.post("/api/tasks/execute") {
            setBody(mapOf("template_id" to templateId))
        }.body()
    }
    
    suspend fun batchExecuteTasks(templateIds: List<Long>): ApiResponse<List<TaskExecution>> {
        return client.post("/api/tasks/batch-execute") {
            setBody(mapOf("template_ids" to templateIds))
        }.body()
    }
    
    suspend fun getTaskStatus(executionId: Long): ApiResponse<TaskExecution> {
        return client.get("/api/tasks/$executionId/status").body()
    }
    
    suspend fun getTaskHistory(
        page: Int = 1,
        pageSize: Int = 20
    ): ApiResponse<List<TaskExecution>> {
        return client.get("/api/tasks/history") {
            parameter("page", page)
            parameter("page_size", pageSize)
        }.body()
    }
}
