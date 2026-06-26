package com.terminator.shared.repository

import com.terminator.shared.model.UserRole
import com.terminator.shared.network.api.AuthApi
import com.terminator.shared.storage.TokenStorage
import com.russhwolf.settings.MapSettings
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthRepositoryTest {

    private fun createAuthApi(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
    ): AuthApi {
        val client = HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
        return AuthApi(client)
    }

    private fun createTokenStorage(): TokenStorage {
        return TokenStorage(MapSettings())
    }

    @Test
    fun testLoginSuccess() = runTest {
        val authApi = createAuthApi { _ ->
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

        val tokenStorage = createTokenStorage()
        val repository = AuthRepository(authApi, tokenStorage)
        val result = repository.login("13800138000", "123456")

        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals(1L, user.userId)
        assertEquals("张大爷", user.nickname)
        assertEquals(UserRole.ELDER, user.role)
        assertEquals("test_token_123", tokenStorage.token)
        assertEquals(1L, tokenStorage.userId)
    }

    @Test
    fun testLoginFailure() = runTest {
        val authApi = createAuthApi { _ ->
            respond(
                content = """{"success":false,"message":"验证码错误"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val tokenStorage = createTokenStorage()
        val repository = AuthRepository(authApi, tokenStorage)
        val result = repository.login("13800138000", "wrong")

        assertTrue(result.isFailure)
        assertEquals("验证码错误", result.exceptionOrNull()?.message)
        assertNull(tokenStorage.token)
    }

    @Test
    fun testLoginNullData() = runTest {
        val authApi = createAuthApi { _ ->
            respond(
                content = """{"success":true,"message":"登录成功","data":null}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val tokenStorage = createTokenStorage()
        val repository = AuthRepository(authApi, tokenStorage)
        val result = repository.login("13800138000", "123456")

        assertTrue(result.isFailure)
    }

    @Test
    fun testSendVerificationCodeSuccess() = runTest {
        val authApi = createAuthApi { _ ->
            respond(
                content = """{"success":true,"message":"验证码已发送"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val tokenStorage = createTokenStorage()
        val repository = AuthRepository(authApi, tokenStorage)
        val result = repository.sendVerificationCode("13800138000")

        assertTrue(result.isSuccess)
    }

    @Test
    fun testSendVerificationCodeFailure() = runTest {
        val authApi = createAuthApi { _ ->
            respond(
                content = """{"success":false,"message":"发送频率过高，请稍后再试"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val tokenStorage = createTokenStorage()
        val repository = AuthRepository(authApi, tokenStorage)
        val result = repository.sendVerificationCode("13800138000")

        assertTrue(result.isFailure)
        assertEquals("发送频率过高，请稍后再试", result.exceptionOrNull()?.message)
    }

    @Test
    fun testRefreshTokenSuccess() = runTest {
        val authApi = createAuthApi { _ ->
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

        val tokenStorage = createTokenStorage()
        val repository = AuthRepository(authApi, tokenStorage)
        val result = repository.refreshToken()

        assertTrue(result.isSuccess)
        assertEquals("张大爷", result.getOrNull()?.nickname)
        assertEquals("new_token_456", tokenStorage.token)
    }

    @Test
    fun testLogoutSuccess() = runTest {
        val authApi = createAuthApi { _ ->
            respond(
                content = """{"success":true,"message":"已退出登录"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val tokenStorage = createTokenStorage()
        tokenStorage.token = "existing_token"
        tokenStorage.userId = 42L
        val repository = AuthRepository(authApi, tokenStorage)
        val result = repository.logout()

        assertTrue(result.isSuccess)
        assertNull(tokenStorage.token)
        assertNull(tokenStorage.userId)
    }

    @Test
    fun testIsLoggedInWhenTokenExists() {
        val tokenStorage = createTokenStorage()
        tokenStorage.token = "test_token"
        val repository = AuthRepository(createAuthApi { respond("", HttpStatusCode.OK) }, tokenStorage)

        assertTrue(repository.isLoggedIn())
    }

    @Test
    fun testIsLoggedInWhenNoToken() {
        val tokenStorage = createTokenStorage()
        val repository = AuthRepository(createAuthApi { respond("", HttpStatusCode.OK) }, tokenStorage)

        assertFalse(repository.isLoggedIn())
    }

    @Test
    fun testGetCurrentUserId() {
        val tokenStorage = createTokenStorage()
        tokenStorage.userId = 99L
        val repository = AuthRepository(createAuthApi { respond("", HttpStatusCode.OK) }, tokenStorage)

        assertEquals(99L, repository.getCurrentUserId())
    }

    @Test
    fun testGetCurrentUserIdNull() {
        val tokenStorage = createTokenStorage()
        val repository = AuthRepository(createAuthApi { respond("", HttpStatusCode.OK) }, tokenStorage)

        assertNull(repository.getCurrentUserId())
    }

    @Test
    fun testNetworkException() = runTest {
        val authApi = createAuthApi { _ ->
            respondError(HttpStatusCode.BadGateway, "Bad Gateway")
        }

        val tokenStorage = createTokenStorage()
        val repository = AuthRepository(authApi, tokenStorage)
        val result = repository.login("13800138000", "123456")

        assertTrue(result.isFailure)
    }
}
