package com.terminator.shared.network.api

import com.terminator.shared.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class NotificationApi(private val client: HttpClient) {

    suspend fun getNotifications(page: Int = 1, pageSize: Int = 20): ApiResponse<List<Notification>> {
        return client.get("/api/notifications") {
            parameter("page", page)
            parameter("page_size", pageSize)
        }.body()
    }

    suspend fun getNotificationSummary(): ApiResponse<NotificationSummary> {
        return client.get("/api/notifications/summary").body()
    }

    suspend fun getNotification(notificationId: Long): ApiResponse<Notification> {
        return client.get("/api/notifications/$notificationId").body()
    }

    suspend fun createNotification(request: CreateNotificationRequest): ApiResponse<Notification> {
        return client.post("/api/notifications") {
            setBody(request)
        }.body()
    }

    suspend fun markAsRead(notificationId: Long): ApiResponse<Unit> {
        return client.put("/api/notifications/$notificationId/read").body()
    }

    suspend fun markAllAsRead(): ApiResponse<Unit> {
        return client.put("/api/notifications/read-all").body()
    }

    suspend fun deleteNotification(notificationId: Long): ApiResponse<Unit> {
        return client.delete("/api/notifications/$notificationId").body()
    }
}
