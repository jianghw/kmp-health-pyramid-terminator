package com.terminator.shared.network

import com.terminator.shared.model.ApiResponse
import com.terminator.shared.model.User
import com.terminator.shared.model.UserRole
import com.terminator.shared.model.UserStatus
import com.terminator.shared.network.api.AuthApi
import com.terminator.shared.network.api.LoginData
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthApiTest {

    private fun createMockClient(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
    ): HttpClient {
        return HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }

    @Test
    fun testSendCodeSuccess() = runTest {
        val client = createMockClient { request ->
            respond(
                content = """{"success":true,"message":"验证码已发送"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val authApi = AuthApi(client)
        val response = authApi.sendCode("13800138000")

        assertTrue(response.success)
        assertEquals("验证码已发送", response.message)
    }

    @Test
    fun testLoginSuccess() = runTest {
        val client = createMockClient { request ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "登录成功",
                        "data": {
                            "token": "test_token_123",
                            "user": {
                                "user_id": 1,
                                "phone": "13800138000",
                                "nickname": "张大爷",
                                "role": "elder",
                                "status": "enabled",
                                "created_at": "2026-01-01T00:00:00Z",
                                "updated_at": "2026-01-01T00:00:00Z"
                            }
                        }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val authApi = AuthApi(client)
        val response = authApi.login("13800138000", "123456")

        assertTrue(response.success)
        assertEquals("登录成功", response.message)
        assertEquals("test_token_123", response.data?.token)
        assertEquals(1L, response.data?.user?.userId)
        assertEquals(UserRole.ELDER, response.data?.user?.role)
    }

    @Test
    fun testLoginFailure() = runTest {
        val client = createMockClient { request ->
            respond(
                content = """{"success":false,"message":"验证码错误"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val authApi = AuthApi(client)
        val response = authApi.login("13800138000", "wrong_code")

        assertFalse(response.success)
        assertEquals("验证码错误", response.message)
    }

    @Test
    fun testLogoutSuccess() = runTest {
        val client = createMockClient { request ->
            respond(
                content = """{"success":true,"message":"已退出登录"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val authApi = AuthApi(client)
        val response = authApi.logout()

        assertTrue(response.success)
        assertEquals("已退出登录", response.message)
    }

    @Test
    fun testRefreshTokenSuccess() = runTest {
        val client = createMockClient { request ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "刷新成功",
                        "data": {
                            "token": "new_token_456",
                            "user": {
                                "user_id": 1,
                                "phone": "13800138000",
                                "nickname": "张大爷",
                                "role": "elder",
                                "status": "enabled",
                                "created_at": "2026-01-01T00:00:00Z",
                                "updated_at": "2026-01-01T00:00:00Z"
                            }
                        }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val authApi = AuthApi(client)
        val response = authApi.refreshToken()

        assertTrue(response.success)
        assertEquals("new_token_456", response.data?.token)
    }
}
