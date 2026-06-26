package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Report(
    @SerialName("report_id")
    val reportId: Long,
    @SerialName("user_id")
    val userId: Long,
    @SerialName("report_type")
    val reportType: ReportType,
    @SerialName("report_date")
    val reportDate: String,
    @SerialName("total_consumption")
    val totalConsumption: Double,
    @SerialName("total_warnings")
    val totalWarnings: Int,
    @SerialName("total_tasks")
    val totalTasks: Int,
    @SerialName("completed_tasks")
    val completedTasks: Int,
    @SerialName("risk_events")
    val riskEvents: Int,
    @SerialName("report_data")
    val reportData: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
enum class ReportType {
    @SerialName("daily")
    DAILY,
    @SerialName("weekly")
    WEEKLY,
    @SerialName("monthly")
    MONTHLY
}

@Serializable
data class DailyReportData(
    val date: String,
    @SerialName("consumption_summary")
    val consumptionSummary: ConsumptionSummary,
    @SerialName("task_summary")
    val taskSummary: TaskSummaryData,
    @SerialName("risk_summary")
    val riskSummary: RiskSummaryData,
    @SerialName("warning_summary")
    val warningSummary: WarningSummaryData
)

@Serializable
data class ConsumptionSummary(
    @SerialName("total_amount")
    val totalAmount: Double,
    @SerialName("app_breakdown")
    val appBreakdown: List<AppConsumption>
)

@Serializable
data class AppConsumption(
    @SerialName("app_id")
    val appId: Long,
    @SerialName("app_name")
    val appName: String,
    val amount: Double
)

@Serializable
data class TaskSummaryData(
    val total: Int,
    val completed: Int,
    val failed: Int,
    val pending: Int
)

@Serializable
data class RiskSummaryData(
    val total: Int,
    val high: Int,
    val medium: Int,
    val low: Int
)

@Serializable
data class WarningSummaryData(
    val total: Int,
    @SerialName("by_level")
    val byLevel: Map<String, Int>
)

@Serializable
data class GenerateReportRequest(
    @SerialName("report_type")
    val reportType: String,
    @SerialName("report_date")
    val reportDate: String? = null
)
