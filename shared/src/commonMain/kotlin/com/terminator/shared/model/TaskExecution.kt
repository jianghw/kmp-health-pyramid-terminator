package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskExecution(
    @SerialName("execution_id")
    val executionId: Long,
    @SerialName("user_id")
    val userId: Long,
    @SerialName("template_id")
    val templateId: Long,
    val status: ExecutionStatus,
    @SerialName("started_at")
    val startedAt: String?,
    @SerialName("completed_at")
    val completedAt: String?,
    @SerialName("result_data")
    val resultData: String?,
    @SerialName("error_message")
    val errorMessage: String?,
    @SerialName("retry_count")
    val retryCount: Int,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
enum class ExecutionStatus {
    @SerialName("pending")
    PENDING,
    @SerialName("running")
    RUNNING,
    @SerialName("completed")
    COMPLETED,
    @SerialName("failed")
    FAILED,
    @SerialName("partial")
    PARTIAL
}
