package com.terminator.backend.routes

import com.terminator.backend.db.FamilyBindings
import com.terminator.backend.db.Users
import com.terminator.backend.model.ApiResponse
import com.terminator.backend.model.BindFamilyRequest
import com.terminator.backend.model.FamilyBindingResponse
import com.terminator.backend.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Route.familyRoutes(authService: AuthService) {
    route("/api/family") {
        post("/bind") {
            val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
            if (token == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Nothing>(success = false, message = "未提供Token"))
                return@post
            }

            val request = call.receive<BindFamilyRequest>()

            val result = transaction {
                val elderExists = Users.selectAll().where { Users.userId eq request.elderUserId }.firstOrNull()
                if (elderExists == null) {
                    return@transaction null
                }

                val bindingId = FamilyBindings.insert {
                    it[elderUserId] = request.elderUserId
                    it[familyUserId] = 1
                    it[relationship] = request.relationship
                    it[permissions] = "{}"
                    it[createdAt] = LocalDateTime.now()
                } get FamilyBindings.bindingId

                FamilyBindingResponse(
                    bindingId = bindingId,
                    elderUserId = request.elderUserId,
                    familyUserId = 1,
                    relationship = request.relationship,
                    permissions = "{}",
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }

            if (result != null) {
                call.respond(ApiResponse(success = true, message = "绑定成功", data = result))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "用户不存在"))
            }
        }

        get("/members") {
            val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
            if (token == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Nothing>(success = false, message = "未提供Token"))
                return@get
            }

            val members = transaction {
                FamilyBindings.selectAll().map {
                    FamilyBindingResponse(
                        bindingId = it[FamilyBindings.bindingId],
                        elderUserId = it[FamilyBindings.elderUserId],
                        familyUserId = it[FamilyBindings.familyUserId],
                        relationship = it[FamilyBindings.relationship],
                        permissions = it[FamilyBindings.permissions],
                        createdAt = it[FamilyBindings.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }

            call.respond(ApiResponse(success = true, message = "查询成功", data = members))
        }

        get("/{elderId}/tasks") {
            val elderId = call.parameters["elderId"]?.toLongOrNull()
            if (elderId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的用户ID"))
                return@get
            }

            call.respond(ApiResponse(success = true, message = "查询成功", data = emptyList<Any>()))
        }

        get("/{elderId}/risks") {
            val elderId = call.parameters["elderId"]?.toLongOrNull()
            if (elderId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的用户ID"))
                return@get
            }

            call.respond(ApiResponse(success = true, message = "查询成功", data = emptyList<Any>()))
        }
    }
}
