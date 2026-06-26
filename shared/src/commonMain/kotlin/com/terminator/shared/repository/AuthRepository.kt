package com.terminator.shared.repository

import com.terminator.shared.model.ApiResponse
import com.terminator.shared.model.User
import com.terminator.shared.network.api.AuthApi
import com.terminator.shared.network.api.LoginData
import com.terminator.shared.storage.TokenStorage

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage
) {
    
    suspend fun sendVerificationCode(phone: String): Result<Unit> {
        return try {
            val response = authApi.sendCode(phone)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(phone: String, code: String): Result<User> {
        return try {
            val response = authApi.login(phone, code)
            if (response.success && response.data != null) {
                tokenStorage.token = response.data.token
                tokenStorage.userId = response.data.user.userId
                Result.success(response.data.user)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun refreshToken(): Result<User> {
        return try {
            val response = authApi.refreshToken()
            if (response.success && response.data != null) {
                tokenStorage.token = response.data.token
                Result.success(response.data.user)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout(): Result<Unit> {
        return try {
            val response = authApi.logout()
            tokenStorage.clear()
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            tokenStorage.clear()
            Result.failure(e)
        }
    }
    
    fun isLoggedIn(): Boolean {
        return tokenStorage.token != null
    }
    
    fun getCurrentUserId(): Long? {
        return tokenStorage.userId
    }
}
