package com.terminator.shared.network.api

import com.terminator.shared.model.ApiResponse
import com.terminator.shared.model.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class AuthApi(private val client: HttpClient) {
    
    suspend fun sendCode(phone: String): ApiResponse<Unit> {
        return client.post("/api/auth/send-code") {
            setBody(mapOf("phone" to phone))
        }.body()
    }
    
    suspend fun login(phone: String, code: String): ApiResponse<LoginData> {
        return client.post("/api/auth/login") {
            setBody(mapOf("phone" to phone, "code" to code))
        }.body()
    }
    
    suspend fun refreshToken(): ApiResponse<LoginData> {
        return client.post("/api/auth/refresh").body()
    }
    
    suspend fun logout(): ApiResponse<Unit> {
        return client.post("/api/auth/logout").body()
    }
}

@kotlinx.serialization.Serializable
data class LoginData(
    val token: String,
    val user: User
)
