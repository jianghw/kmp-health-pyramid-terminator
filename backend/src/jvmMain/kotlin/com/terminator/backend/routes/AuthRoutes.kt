package com.terminator.backend.routes

import com.terminator.backend.model.*
import com.terminator.backend.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

/**
 * 认证相关路由 - 处理用户登录、注册和令牌管理
 *
 * API端点：
 * - POST /api/auth/send-code: 发送短信验证码
 * - POST /api/auth/login: 用户登录（返回双令牌）
 * - POST /api/auth/refresh: 刷新令牌
 * - POST /api/auth/logout: 用户登出
 */
fun Route.authRoutes(authService: AuthService) {
    route("/api/auth") {

        /**
         * POST /api/auth/send-code - 发送短信验证码
         *
         * 向指定手机号发送6位数字验证码，验证码有效期5分钟。
         */
        post("/send-code") {
            val request = call.receive<SendCodeRequest>()
            val code = authService.sendVerificationCode(request.phone)
            if (code.isNotEmpty()) {
                call.respond(ApiResponse<Nothing>(success = true, message = "验证码已发送"))
            } else {
                call.respond(HttpStatusCode.InternalServerError, ApiResponse<Nothing>(success = false, message = "发送验证码失败"))
            }
        }

        /**
         * POST /api/auth/login - 用户登录
         *
         * 使用手机号和验证码登录，成功后返回：
         * - token: access_token（有效期2小时）
         * - refreshToken: refresh_token（有效期7天）
         * - user: 用户信息
         */
        post("/login") {
            val request = call.receive<LoginRequest>()
            try {
                val result = authService.login(request.phone, request.code)
                call.respond(ApiResponse(success = true, message = "登录成功", data = result))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Nothing>(success = false, message = e.message ?: "登录失败"))
            }
        }

        /**
         * POST /api/auth/refresh - 刷新令牌
         *
         * 使用 refresh_token 获取新的 access_token 和 refresh_token。
         * 请求体中传入 refreshToken 字段。
         */
        post("/refresh") {
            val request = call.receive<RefreshTokenRequest>()
            try {
                val result = authService.refreshAccessToken(request.refreshToken)
                call.respond(ApiResponse(success = true, message = "刷新成功", data = result))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Nothing>(success = false, message = e.message ?: "刷新失败"))
            }
        }

        /**
         * POST /api/auth/logout - 用户登出
         *
         * 客户端应该在登出后删除本地存储的 token 和 refreshToken。
         */
        post("/logout") {
            call.respond(ApiResponse<Nothing>(success = true, message = "已登出"))
        }
    }
}

/**
 * 刷新令牌请求体
 */
@Serializable
data class RefreshTokenRequest(
    @kotlinx.serialization.SerialName("refresh_token")
    val refreshToken: String
)
