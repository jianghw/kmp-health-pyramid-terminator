package com.terminator.backend.routes

import com.terminator.backend.db.Notifications
import com.terminator.backend.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Route.notificationRoutes() {
    route("/api/notifications") {
        get {
            val userId = 1L
            val page = call.parameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.parameters["page_size"]?.toIntOrNull() ?: 20

            val notifications = transaction {
                Notifications.selectAll().where {
                    Notifications.userId eq userId
                }.orderBy(Notifications.createdAt, SortOrder.DESC)
                    .limit(pageSize, offset = ((page - 1) * pageSize).toLong())
                    .map {
                        NotificationResponse(
                            notificationId = it[Notifications.notificationId],
                            userId = it[Notifications.userId],
                            title = it[Notifications.title],
                            body = it[Notifications.body],
                            notificationType = it[Notifications.notificationType],
                            isRead = it[Notifications.isRead],
                            relatedId = it[Notifications.relatedId],
                            createdAt = it[Notifications.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        )
                    }
            }

            call.respond(ApiResponse(success = true, message = "查询成功", data = notifications))
        }

        get("/summary") {
            val userId = 1L

            val summary = transaction {
                val total = Notifications.selectAll().where { Notifications.userId eq userId }.count()
                val unread = Notifications.selectAll().where {
                    (Notifications.userId eq userId) and (Notifications.isRead eq false)
                }.count()

                NotificationSummaryResponse(
                    totalCount = total.toInt(),
                    unreadCount = unread.toInt()
                )
            }

            call.respond(ApiResponse(success = true, message = "查询成功", data = summary))
        }

        get("/{notificationId}") {
            val notificationId = call.parameters["notificationId"]?.toLongOrNull()
            if (notificationId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的通知ID"))
                return@get
            }

            val notification = transaction {
                Notifications.selectAll().where {
                    Notifications.notificationId eq notificationId
                }.firstOrNull()?.let {
                    NotificationResponse(
                        notificationId = it[Notifications.notificationId],
                        userId = it[Notifications.userId],
                        title = it[Notifications.title],
                        body = it[Notifications.body],
                        notificationType = it[Notifications.notificationType],
                        isRead = it[Notifications.isRead],
                        relatedId = it[Notifications.relatedId],
                        createdAt = it[Notifications.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }

            if (notification != null) {
                call.respond(ApiResponse(success = true, message = "查询成功", data = notification))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "通知不存在"))
            }
        }

        post {
            val request = call.receive<CreateNotificationRequest>()

            val result = transaction {
                val notificationId = Notifications.insert {
                    it[userId] = request.userId
                    it[title] = request.title
                    it[body] = request.body
                    it[notificationType] = request.notificationType
                    it[isRead] = false
                    it[relatedId] = request.relatedId
                    it[createdAt] = LocalDateTime.now()
                } get Notifications.notificationId

                NotificationResponse(
                    notificationId = notificationId,
                    userId = request.userId,
                    title = request.title,
                    body = request.body,
                    notificationType = request.notificationType,
                    isRead = false,
                    relatedId = request.relatedId,
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }

            call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "通知创建成功", data = result))
        }

        put("/{notificationId}/read") {
            val notificationId = call.parameters["notificationId"]?.toLongOrNull()
            if (notificationId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的通知ID"))
                return@put
            }

            transaction {
                Notifications.update({ Notifications.notificationId eq notificationId }) {
                    it[isRead] = true
                }
            }

            call.respond(ApiResponse<Nothing>(success = true, message = "通知已标记为已读"))
        }

        put("/read-all") {
            val userId = 1L

            transaction {
                Notifications.update({ Notifications.userId eq userId }) {
                    it[isRead] = true
                }
            }

            call.respond(ApiResponse<Nothing>(success = true, message = "所有通知已标记为已读"))
        }

        delete("/{notificationId}") {
            val notificationId = call.parameters["notificationId"]?.toLongOrNull()
            if (notificationId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的通知ID"))
                return@delete
            }

            transaction {
                Notifications.deleteWhere { Notifications.notificationId eq notificationId }
            }

            call.respond(ApiResponse<Nothing>(success = true, message = "通知已删除"))
        }
    }
}

fun createNotification(
    userId: Long,
    title: String,
    body: String,
    type: String,
    relatedId: Long? = null
): Long {
    return transaction {
        Notifications.insert {
            it[Notifications.userId] = userId
            it[Notifications.title] = title
            it[Notifications.body] = body
            it[notificationType] = type
            it[isRead] = false
            it[Notifications.relatedId] = relatedId
            it[createdAt] = LocalDateTime.now()
        } get Notifications.notificationId
    }
}
