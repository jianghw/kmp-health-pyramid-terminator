package com.terminator.shared.repository

import com.terminator.shared.model.AppCredential
import com.terminator.shared.model.CredentialInfo
import com.terminator.shared.model.CredentialInput
import com.terminator.shared.model.CredentialStatus
import com.terminator.shared.model.CredentialType
import com.terminator.shared.network.api.CredentialApi
import com.terminator.shared.storage.CredentialStorage

class CredentialRepository(
    private val credentialApi: CredentialApi,
    private val credentialStorage: CredentialStorage
) {

    suspend fun saveCredential(input: CredentialInput): Result<AppCredential> {
        return try {
            val localData = buildLocalData(input)
            credentialStorage.saveCredential(
                appId = input.appId,
                credentialType = input.credentialType.name,
                encryptedData = localData,
                alias = input.alias
            )

            val response = credentialApi.saveCredential(input)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCredentials(appId: Long? = null): Result<List<CredentialInfo>> {
        return try {
            val response = credentialApi.getCredentials(appId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCredential(credentialId: Long, appId: Long, credentialType: String): Result<Unit> {
        return try {
            credentialStorage.removeCredential(appId, credentialType)
            val response = credentialApi.deleteCredential(credentialId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyCredential(credentialId: Long): Result<Boolean> {
        return try {
            val response = credentialApi.verifyCredential(credentialId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getLocalCredential(appId: Long, credentialType: CredentialType): String? {
        return credentialStorage.getCredential(appId, credentialType.name)?.encryptedData
    }

    fun hasLocalCredential(appId: Long, credentialType: CredentialType): Boolean {
        return credentialStorage.hasCredential(appId, credentialType.name)
    }

    fun clearLocalCredentials() {
        credentialStorage.clear()
    }

    private fun buildLocalData(input: CredentialInput): String {
        return when (input.credentialType) {
            CredentialType.ACCOUNT_PASSWORD -> "${input.username}|${input.password}"
            CredentialType.TOKEN -> input.token
            CredentialType.API_KEY -> input.apiKey
            CredentialType.COOKIE -> input.cookie
        }
    }
}
