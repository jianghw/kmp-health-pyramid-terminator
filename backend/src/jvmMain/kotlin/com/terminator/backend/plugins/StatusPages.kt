package com.terminator.backend.plugins

import com.terminator.backend.model.ApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

/**
 * 全局异常处理插件 - 捕获未处理的异常并返回友好的错误响应
 *
 * 这个插件的作用：
 * 1. 防止服务器因为未捕获的异常而崩溃
 * 2. 防止内部错误信息（如堆栈跟踪）泄露给客户端
 * 3. 提供统一的错误响应格式
 *
 * 安装后，所有未处理的异常都会被拦截并转换为标准的JSON错误响应。
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        // 处理参数验证异常（如请求参数格式错误）
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse<Nothing>(
                    success = false,
                    message = "请求参数错误: ${cause.message}",
                    code = "INVALID_PARAMETER"
                )
            )
        }

        // 处理认证失败异常（如token无效或过期）
        exception<AuthenticationException> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ApiResponse<Nothing>(
                    success = false,
                    message = cause.message ?: "认证失败",
                    code = "AUTH_FAILED"
                )
            )
        }

        // 处理权限不足异常
        exception<AuthorizationException> { call, cause ->
            call.respond(
                HttpStatusCode.Forbidden,
                ApiResponse<Nothing>(
                    success = false,
                    message = cause.message ?: "权限不足",
                    code = "FORBIDDEN"
                )
            )
        }

        // 处理资源不存在异常
        exception<ResourceNotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ApiResponse<Nothing>(
                    success = false,
                    message = cause.message ?: "资源不存在",
                    code = "NOT_FOUND"
                )
            )
        }

        // 处理所有其他未捕获的异常（兜底处理）
        exception<Throwable> { call, cause ->
            // 记录错误日志（生产环境应使用SLF4J）
            println("未捕获的异常: ${cause.javaClass.simpleName}: ${cause.message}")
            cause.printStackTrace()

            // 返回通用错误信息，不暴露内部细节
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse<Nothing>(
                    success = false,
                    message = "服务器内部错误，请稍后重试",
                    code = "INTERNAL_ERROR"
                )
            )
        }

        // 处理HTTP状态码错误（如404 Not Found）
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                status,
                ApiResponse<Nothing>(
                    success = false,
                    message = "请求的资源不存在",
                    code = "NOT_FOUND"
                )
            )
        }

        // 处理HTTP 405 Method Not Allowed
        status(HttpStatusCode.MethodNotAllowed) { call, status ->
            call.respond(
                status,
                ApiResponse<Nothing>(
                    success = false,
                    message = "不支持的请求方法",
                    code = "METHOD_NOT_ALLOWED"
                )
            )
        }
    }
}

/** 认证失败异常 */
class AuthenticationException(message: String = "认证失败") : RuntimeException(message)

/** 权限不足异常 */
class AuthorizationException(message: String = "权限不足") : RuntimeException(message)

/** 资源不存在异常 */
class ResourceNotFoundException(message: String = "资源不存在") : RuntimeException(message)
