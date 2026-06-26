package com.terminator.shared.repository

import com.terminator.shared.model.ApiResponse
import com.terminator.shared.model.TaskExecution
import com.terminator.shared.model.TaskTemplate
import com.terminator.shared.network.api.TaskApi

class TaskRepository(private val taskApi: TaskApi) {
    
    suspend fun executeTask(templateId: Long): Result<TaskExecution> {
        return try {
            val response = taskApi.executeTask(templateId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun batchExecuteTasks(templateIds: List<Long>): Result<List<TaskExecution>> {
        return try {
            val response = taskApi.batchExecuteTasks(templateIds)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getTaskStatus(executionId: Long): Result<TaskExecution> {
        return try {
            val response = taskApi.getTaskStatus(executionId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getTaskHistory(page: Int = 1, pageSize: Int = 20): Result<List<TaskExecution>> {
        return try {
            val response = taskApi.getTaskHistory(page, pageSize)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAppTasks(appId: Long): Result<List<TaskTemplate>> {
        return try {
            val response = taskApi.getAppTasks(appId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
