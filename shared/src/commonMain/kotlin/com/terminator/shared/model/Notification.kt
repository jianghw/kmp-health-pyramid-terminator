package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    @SerialName("notification_id")
    val notificationId: Long,
    @SerialName("user_id")
    val userId: Long,
    val title: String,
    val body: String,
    @SerialName("notification_type")
    val notificationType: NotificationType,
    @SerialName("is_read")
    val isRead: Boolean,
    @SerialName("related_id")
    val relatedId: Long? = null,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
enum class NotificationType {
    @SerialName("warning")
    WARNING,
    @SerialName("task")
    TASK,
    @SerialName("risk")
    RISK,
    @SerialName("report")
    REPORT,
    @SerialName("system")
    SYSTEM
}

@Serializable
data class NotificationSummary(
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("unread_count")
    val unreadCount: Int
)

@Serializable
data class CreateNotificationRequest(
    @SerialName("user_id")
    val userId: Long,
    val title: String,
    val body: String,
    @SerialName("notification_type")
    val notificationType: String,
    @SerialName("related_id")
    val relatedId: Long? = null
)
