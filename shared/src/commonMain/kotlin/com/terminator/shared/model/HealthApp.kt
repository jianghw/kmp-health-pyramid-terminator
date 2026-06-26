package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HealthApp(
    @SerialName("app_id")
    val appId: Long,
    @SerialName("app_name")
    val appName: String,
    @SerialName("app_type")
    val appType: AppType,
    @SerialName("app_icon")
    val appIcon: String,
    @SerialName("risk_score")
    val riskScore: Int,
    val status: AppStatus,
    val config: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
enum class AppType {
    @SerialName("wechat_miniprogram")
    WECHAT_MINIPROGRAM,
    @SerialName("independent_app")
    INDEPENDENT_APP,
    @SerialName("h5")
    H5
}

@Serializable
enum class AppStatus {
    @SerialName("enabled")
    ENABLED,
    @SerialName("disabled")
    DISABLED
}
