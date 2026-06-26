package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppCredential(
    @SerialName("credential_id")
    val credentialId: Long,
    @SerialName("app_id")
    val appId: Long,
    @SerialName("app_name")
    val appName: String,
    @SerialName("credential_type")
    val credentialType: CredentialType,
    @SerialName("encrypted_data")
    val encryptedData: String,
    val alias: String,
    val status: CredentialStatus,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
enum class CredentialType {
    @SerialName("account_password")
    ACCOUNT_PASSWORD,
    @SerialName("token")
    TOKEN,
    @SerialName("api_key")
    API_KEY,
    @SerialName("cookie")
    COOKIE
}

@Serializable
enum class CredentialStatus {
    @SerialName("active")
    ACTIVE,
    @SerialName("expired")
    EXPIRED,
    @SerialName("invalid")
    INVALID
}

@Serializable
data class CredentialInput(
    @SerialName("app_id")
    val appId: Long,
    @SerialName("credential_type")
    val credentialType: CredentialType,
    val username: String = "",
    val password: String = "",
    val token: String = "",
    @SerialName("api_key")
    val apiKey: String = "",
    val cookie: String = "",
    val alias: String = ""
)

@Serializable
data class CredentialInfo(
    @SerialName("credential_id")
    val credentialId: Long,
    @SerialName("app_id")
    val appId: Long,
    @SerialName("app_name")
    val appName: String,
    @SerialName("credential_type")
    val credentialType: CredentialType,
    val alias: String,
    val status: CredentialStatus,
    @SerialName("last_used_at")
    val lastUsedAt: String? = null,
    @SerialName("created_at")
    val createdAt: String
)
