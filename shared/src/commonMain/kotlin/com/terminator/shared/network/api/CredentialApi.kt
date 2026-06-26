package com.terminator.shared.network.api

import com.terminator.shared.model.ApiResponse
import com.terminator.shared.model.AppCredential
import com.terminator.shared.model.CredentialInfo
import com.terminator.shared.model.CredentialInput
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class CredentialApi(private val client: HttpClient) {

    suspend fun saveCredential(input: CredentialInput): ApiResponse<AppCredential> {
        return client.post("/api/credentials") {
            setBody(input)
        }.body()
    }

    suspend fun getCredentials(appId: Long? = null): ApiResponse<List<CredentialInfo>> {
        return client.get("/api/credentials") {
            if (appId != null) {
                parameter("app_id", appId)
            }
        }.body()
    }

    suspend fun getCredentialDetail(credentialId: Long): ApiResponse<AppCredential> {
        return client.get("/api/credentials/$credentialId").body()
    }

    suspend fun deleteCredential(credentialId: Long): ApiResponse<Unit> {
        return client.delete("/api/credentials/$credentialId").body()
    }

    suspend fun verifyCredential(credentialId: Long): ApiResponse<Boolean> {
        return client.post("/api/credentials/$credentialId/verify").body()
    }

    suspend fun updateCredential(credentialId: Long, input: CredentialInput): ApiResponse<AppCredential> {
        return client.put("/api/credentials/$credentialId") {
            setBody(input)
        }.body()
    }
}
