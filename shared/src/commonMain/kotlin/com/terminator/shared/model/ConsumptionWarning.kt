package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsumptionWarning(
    @SerialName("warning_id")
    val warningId: Long,
    @SerialName("user_id")
    val userId: Long,
    @SerialName("app_id")
    val appId: Long,
    @SerialName("rule_type")
    val ruleType: String,
    @SerialName("warning_level")
    val warningLevel: WarningLevel,
    val title: String,
    val message: String,
    @SerialName("current_amount")
    val currentAmount: Double,
    @SerialName("threshold_amount")
    val thresholdAmount: Double,
    val status: WarningStatus,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
enum class WarningLevel {
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
enum class WarningStatus {
    @SerialName("active")
    ACTIVE,
    @SerialName("dismissed")
    DISMISSED
}

@Serializable
data class ConsumptionRule(
    @SerialName("rule_id")
    val ruleId: Long,
    @SerialName("user_id")
    val userId: Long,
    @SerialName("rule_type")
    val ruleType: String,
    @SerialName("rule_name")
    val ruleName: String,
    @SerialName("threshold_amount")
    val thresholdAmount: Double,
    @SerialName("time_window")
    val timeWindow: String,
    @SerialName("is_enabled")
    val isEnabled: Boolean,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class EvaluateConsumptionRequest(
    @SerialName("app_id")
    val appId: Long,
    val amount: Double
)

@Serializable
data class EvaluateConsumptionResult(
    val allowed: Boolean,
    val warning: ConsumptionWarning? = null,
    val message: String
)

@Serializable
data class CreateRuleRequest(
    @SerialName("rule_type")
    val ruleType: String,
    @SerialName("rule_name")
    val ruleName: String,
    @SerialName("threshold_amount")
    val thresholdAmount: Double,
    @SerialName("time_window")
    val timeWindow: String
)
