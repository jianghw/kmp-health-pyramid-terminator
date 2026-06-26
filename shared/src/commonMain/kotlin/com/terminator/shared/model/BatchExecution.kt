package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BatchExecution(
    @SerialName("batch_id")
    val batchId: String,
    @SerialName("task_ids")
    val taskIds: List<Long>,
    val status: BatchStatus,
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("completed_count")
    val completedCount: Int,
    @SerialName("failed_count")
    val failedCount: Int,
    @SerialName("pending_count")
    val pendingCount: Int,
    @SerialName("started_at")
    val startedAt: String?,
    @SerialName("estimated_completion")
    val estimatedCompletion: String?,
    val results: List<BatchTaskResult>
)

@Serializable
data class BatchTaskResult(
    @SerialName("task_id")
    val taskId: Long,
    @SerialName("task_name")
    val taskName: String,
    val status: ExecutionStatus,
    @SerialName("execution_id")
    val executionId: Long? = null,
    val progress: Float = 0f,
    val message: String? = null,
    @SerialName("started_at")
    val startedAt: String? = null,
    @SerialName("completed_at")
    val completedAt: String? = null
)

@Serializable
enum class BatchStatus {
    @SerialName("pending")
    PENDING,
    @SerialName("running")
    RUNNING,
    @SerialName("completed")
    COMPLETED,
    @SerialName("partial")
    PARTIAL,
    @SerialName("failed")
    FAILED,
    @SerialName("cancelled")
    CANCELLED
}

@Serializable
data class BatchExecutionConfig(
    @SerialName("max_concurrent")
    val maxConcurrent: Int = 3,
    @SerialName("stop_on_failure")
    val stopOnFailure: Boolean = false,
    @SerialName("retry_failed")
    val retryFailed: Boolean = true,
    @SerialName("max_retries")
    val maxRetries: Int = 2,
    @SerialName("delay_between_tasks")
    val delayBetweenTasks: Long = 1000
)

data class BatchProgress(
    val batchId: String,
    val totalTasks: Int,
    val completedTasks: Int,
    val failedTasks: Int,
    val currentTaskName: String?,
    val overallProgress: Float,
    val estimatedTimeRemaining: Long?,
    val status: BatchStatus
)
