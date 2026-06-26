package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskTemplate(
    @SerialName("template_id")
    val templateId: Long,
    @SerialName("app_id")
    val appId: Long,
    @SerialName("task_name")
    val taskName: String,
    @SerialName("task_type")
    val taskType: TaskType,
    @SerialName("template_config")
    val templateConfig: String,
    @SerialName("estimated_minutes")
    val estimatedMinutes: Int,
    @SerialName("reward_points")
    val rewardPoints: Int,
    val status: TemplateStatus,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
enum class TaskType {
    @SerialName("sign_in")
    SIGN_IN,
    @SerialName("course_listen")
    COURSE_LISTEN,
    @SerialName("survey")
    SURVEY,
    @SerialName("reading")
    READING,
    @SerialName("exchange")
    EXCHANGE
}

@Serializable
enum class TemplateStatus {
    @SerialName("enabled")
    ENABLED,
    @SerialName("disabled")
    DISABLED
}
