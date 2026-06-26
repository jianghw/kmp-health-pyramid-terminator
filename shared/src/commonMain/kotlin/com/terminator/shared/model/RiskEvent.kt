package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RiskEvent(
    @SerialName("event_id")
    val eventId: Long,
    @SerialName("user_id")
    val userId: Long,
    @SerialName("app_id")
    val appId: Long,
    @SerialName("event_type")
    val eventType: RiskEventType,
    val severity: RiskSeverity,
    val description: String,
    val evidence: String?,
    @SerialName("action_taken")
    val actionTaken: RiskAction,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
enum class RiskEventType {
    @SerialName("marketing_inducement")
    MARKETING_INDUCEMENT,
    @SerialName("false_claim")
    FALSE_CLAIM,
    @SerialName("price_anomaly")
    PRICE_ANOMALY,
    @SerialName("consumption_trigger")
    CONSUMPTION_TRIGGER
}

@Serializable
enum class RiskSeverity {
    @SerialName("low")
    LOW,
    @SerialName("medium")
    MEDIUM,
    @SerialName("high")
    HIGH,
    @SerialName("critical")
    CRITICAL
}

@Serializable
enum class RiskAction {
    @SerialName("none")
    NONE,
    @SerialName("warned")
    WARNED,
    @SerialName("blocked")
    BLOCKED,
    @SerialName("notified_family")
    NOTIFIED_FAMILY
}
