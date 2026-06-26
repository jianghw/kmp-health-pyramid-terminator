package com.terminator.shared.repository

import com.terminator.shared.model.*
import com.terminator.shared.network.api.NotificationApi

class NotificationRepository(private val notificationApi: NotificationApi) {

    suspend fun getNotifications(page: Int = 1, pageSize: Int = 20): Result<List<Notification>> {
        return try {
            val response = notificationApi.getNotifications(page, pageSize)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSummary(): Result<NotificationSummary> {
        return try {
            val response = notificationApi.getNotificationSummary()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotification(notificationId: Long): Result<Notification> {
        return try {
            val response = notificationApi.getNotification(notificationId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createNotification(request: CreateNotificationRequest): Result<Notification> {
        return try {
            val response = notificationApi.createNotification(request)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsRead(notificationId: Long): Result<Unit> {
        return try {
            val response = notificationApi.markAsRead(notificationId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAllAsRead(): Result<Unit> {
        return try {
            val response = notificationApi.markAllAsRead()
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNotification(notificationId: Long): Result<Unit> {
        return try {
            val response = notificationApi.deleteNotification(notificationId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
