package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("user_id")
    val userId: Long,
    val phone: String,
    val nickname: String,
    val role: UserRole,
    val status: UserStatus,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
enum class UserRole {
    @SerialName("elder")
    ELDER,
    @SerialName("family_member")
    FAMILY_MEMBER,
    @SerialName("admin")
    ADMIN
}

@Serializable
enum class UserStatus {
    @SerialName("enabled")
    ENABLED,
    @SerialName("disabled")
    DISABLED
}
