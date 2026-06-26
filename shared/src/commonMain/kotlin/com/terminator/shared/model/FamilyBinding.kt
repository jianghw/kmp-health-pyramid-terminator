package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FamilyBinding(
    @SerialName("binding_id")
    val bindingId: Long,
    @SerialName("elder_user_id")
    val elderUserId: Long,
    @SerialName("family_user_id")
    val familyUserId: Long,
    val relationship: String,
    val permissions: String,
    @SerialName("created_at")
    val createdAt: String
)
