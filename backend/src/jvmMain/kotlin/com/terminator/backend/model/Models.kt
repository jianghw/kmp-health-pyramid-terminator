package com.terminator.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val code: String? = null
)

@Serializable
data class SendCodeRequest(
    val phone: String
)

@Serializable
data class LoginRequest(
    val phone: String,
    val code: String
)

@Serializable
data class LoginResponse(
    val token: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    val user: UserResponse
)

@Serializable
data class UserResponse(
    @SerialName("user_id")
    val userId: Long,
    val phone: String,
    val nickname: String,
    val role: String,
    val status: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class BindFamilyRequest(
    @SerialName("elder_user_id")
    val elderUserId: Long,
    val relationship: String
)

@Serializable
data class FamilyBindingResponse(
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

@Serializable
data class CreateAppRequest(
    @SerialName("app_name")
    val appName: String,
    @SerialName("app_type")
    val appType: String,
    @SerialName("app_icon")
    val appIcon: String? = null,
    @SerialName("risk_score")
    val riskScore: Int? = null,
    val config: String? = null
)

@Serializable
data class HealthAppResponse(
    @SerialName("app_id")
    val appId: Long,
    @SerialName("app_name")
    val appName: String,
    @SerialName("app_type")
    val appType: String,
    @SerialName("app_icon")
    val appIcon: String,
    @SerialName("risk_score")
    val riskScore: Int,
    val status: String,
    val config: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class CreateTaskTemplateRequest(
    @SerialName("task_name")
    val taskName: String,
    @SerialName("task_type")
    val taskType: String,
    @SerialName("template_config")
    val templateConfig: String? = null,
    @SerialName("estimated_minutes")
    val estimatedMinutes: Int? = null,
    @SerialName("reward_points")
    val rewardPoints: Int? = null
)

@Serializable
data class TaskTemplateResponse(
    @SerialName("template_id")
    val templateId: Long,
    @SerialName("app_id")
    val appId: Long,
    @SerialName("task_name")
    val taskName: String,
    @SerialName("task_type")
    val taskType: String,
    @SerialName("template_config")
    val templateConfig: String,
    @SerialName("estimated_minutes")
    val estimatedMinutes: Int,
    @SerialName("reward_points")
    val rewardPoints: Int,
    val status: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class ExecuteTaskRequest(
    @SerialName("template_id")
    val templateId: Long? = null,
    @SerialName("template_ids")
    val templateIds: List<Long> = emptyList()
)

@Serializable
data class TaskExecutionResponse(
    @SerialName("execution_id")
    val executionId: Long,
    @SerialName("user_id")
    val userId: Long,
    @SerialName("template_id")
    val templateId: Long,
    val status: String,
    @SerialName("started_at")
    val startedAt: String?,
    @SerialName("completed_at")
    val completedAt: String?,
    @SerialName("result_data")
    val resultData: String?,
    @SerialName("error_message")
    val errorMessage: String?,
    @SerialName("retry_count")
    val retryCount: Int,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class RiskEventResponse(
    @SerialName("event_id")
    val eventId: Long,
    @SerialName("user_id")
    val userId: Long,
    @SerialName("app_id")
    val appId: Long,
    @SerialName("event_type")
    val eventType: String,
    val severity: String,
    val description: String,
    val evidence: String?,
    @SerialName("action_taken")
    val actionTaken: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class RiskSummaryResponse(
    @SerialName("total_events")
    val totalEvents: Int,
    @SerialName("high_risk_count")
    val highRiskCount: Int,
    @SerialName("medium_risk_count")
    val mediumRiskCount: Int,
    @SerialName("low_risk_count")
    val lowRiskCount: Int
)

@Serializable
data class AppRiskScoreResponse(
    @SerialName("app_id")
    val appId: Long,
    @SerialName("app_name")
    val appName: String,
    @SerialName("risk_score")
    val riskScore: Int,
    @SerialName("risk_level")
    val riskLevel: String
)

@Serializable
data class ConsumptionWarningResponse(
    @SerialName("warning_id")
    val warningId: Long,
    @SerialName("user_id")
    val userId: Long,
    @SerialName("app_id")
    val appId: Long,
    @SerialName("rule_type")
    val ruleType: String,
    @SerialName("warning_level")
    val warningLevel: String,
    val title: String,
    val message: String,
    @SerialName("current_amount")
    val currentAmount: Double,
    @SerialName("threshold_amount")
    val thresholdAmount: Double,
    val status: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class CreateConsumptionRuleRequest(
    @SerialName("rule_type")
    val ruleType: String,
    @SerialName("rule_name")
    val ruleName: String,
    @SerialName("threshold_amount")
    val thresholdAmount: Double,
    @SerialName("time_window")
    val timeWindow: String
)

@Serializable
data class ConsumptionRuleResponse(
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
data class EvaluateConsumptionResponse(
    val allowed: Boolean,
    val warning: ConsumptionWarningResponse? = null,
    val message: String
)

@Serializable
data class NotificationResponse(
    @SerialName("notification_id")
    val notificationId: Long,
    @SerialName("user_id")
    val userId: Long,
    val title: String,
    val body: String,
    @SerialName("notification_type")
    val notificationType: String,
    @SerialName("is_read")
    val isRead: Boolean,
    @SerialName("related_id")
    val relatedId: Long?,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class CreateNotificationRequest(
    @SerialName("user_id")
    val userId: Long,
    val title: String,
    val body: String,
    @SerialName("notification_type")
    val notificationType: String,
    @SerialName("related_id")
    val relatedId: Long? = null
)

@Serializable
data class NotificationSummaryResponse(
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("unread_count")
    val unreadCount: Int
)

@Serializable
data class ReportResponse(
    @SerialName("report_id")
    val reportId: Long,
    @SerialName("user_id")
    val userId: Long,
    @SerialName("report_type")
    val reportType: String,
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
data class GenerateReportRequest(
    @SerialName("report_type")
    val reportType: String,
    @SerialName("report_date")
    val reportDate: String? = null
)

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

/**
 * 创建/更新AI配置的请求体
 * 前端发送此数据到后端来创建或修改AI配置
 * @SerialName 注解用于指定JSON序列化时的字段名
 */
@Serializable
data class CreateAIConfigRequest(
    val provider: String,                      // AI提供商（如 "openai", "qwen", "zhipu", "baidu"）
    @SerialName("api_key")
    val apiKey: String,                        // API密钥（从AI提供商控制台获取）
    @SerialName("model_name")
    val modelName: String,                     // 模型名称（如 "gpt-4", "qwen-turbo"）
    @SerialName("base_url")
    val baseUrl: String,                       // API接口地址
    @SerialName("max_tokens")
    val maxTokens: Int = 1000,                 // 最大token数（默认1000）
    val temperature: Double = 0.7              // 温度参数（默认0.7）
)

/**
 * AI配置的响应体 - 返回给前端的配置信息
 * 注意：apiKeyMasked 是脱敏后的密钥，不会暴露完整密钥给前端
 */
@Serializable
data class AIConfigResponse(
    @SerialName("config_id")
    val configId: Long,                        // 配置ID
    @SerialName("user_id")
    val userId: Long,                          // 所属用户ID
    val provider: String,                      // AI提供商
    @SerialName("api_key_masked")
    val apiKeyMasked: String,                  // 脱敏后的API密钥（如 sk-12****90）
    @SerialName("model_name")
    val modelName: String,                     // 模型名称
    @SerialName("base_url")
    val baseUrl: String,                       // API地址
    @SerialName("max_tokens")
    val maxTokens: Int,                        // 最大token数
    val temperature: Double,                   // 温度参数
    @SerialName("is_enabled")
    val isEnabled: Boolean,                    // 是否启用
    @SerialName("created_at")
    val createdAt: String,                     // 创建时间
    @SerialName("updated_at")
    val updatedAt: String                      // 更新时间
)

/**
 * AI答题请求体 - 前端发送题目给后端请求AI回答
 */
@Serializable
data class AIQuestionRequest(
    val question: String,                      // 题目内容（必填）
    val context: String? = null,               // 上下文信息（可选）
    val options: List<String>? = null,         // 选项列表（选择题时提供）
    @SerialName("question_type")
    val questionType: String,                  // 题目类型
    @SerialName("config_id")
    val configId: Long? = null                 // 指定AI配置ID（可选，默认使用第一个启用的配置）
)

/**
 * AI答题响应体 - 后端返回的AI答案
 */
@Serializable
data class AIQuestionResponse(
    val answer: String,                        // AI给出的答案
    val confidence: Double,                    // 置信度（0.0-1.0，越高越确信）
    val explanation: String? = null,           // 答案解析（可选）
    val provider: String                       // 使用的AI提供商名称
)

/**
 * 创建题库请求体
 */
@Serializable
data class CreateQuestionBankRequest(
    @SerialName("bank_name")
    val bankName: String,                      // 题库名称
    val description: String                    // 题库描述
)

/**
 * 题库响应体
 */
@Serializable
data class QuestionBankResponse(
    @SerialName("bank_id")
    val bankId: Long,                          // 题库ID
    @SerialName("user_id")
    val userId: Long,                          // 所属用户ID
    @SerialName("bank_name")
    val bankName: String,                      // 题库名称
    val description: String,                   // 题库描述
    @SerialName("question_count")
    val questionCount: Int,                    // 题目数量
    @SerialName("created_at")
    val createdAt: String,                     // 创建时间
    @SerialName("updated_at")
    val updatedAt: String                      // 更新时间
)

/**
 * 创建题目请求体
 */
@Serializable
data class CreateQuestionEntryRequest(
    @SerialName("bank_id")
    val bankId: Long,                          // 所属题库ID
    val question: String,                      // 题目内容
    @SerialName("question_type")
    val questionType: String,                  // 题目类型
    val options: List<String>? = null,         // 选项列表
    @SerialName("correct_answer")
    val correctAnswer: String,                 // 正确答案
    val explanation: String? = null,           // 答案解析
    val tags: List<String> = emptyList()       // 标签列表
)

/**
 * 题目响应体
 */
@Serializable
data class QuestionEntryResponse(
    @SerialName("entry_id")
    val entryId: Long,                         // 题目ID
    @SerialName("bank_id")
    val bankId: Long,                          // 所属题库ID
    val question: String,                      // 题目内容
    @SerialName("question_type")
    val questionType: String,                  // 题目类型
    val options: List<String>?,                // 选项列表
    @SerialName("correct_answer")
    val correctAnswer: String,                 // 正确答案
    val explanation: String?,                  // 答案解析
    val tags: List<String>,                    // 标签列表
    @SerialName("created_at")
    val createdAt: String                      // 创建时间
)

/**
 * 搜索题目请求体 - 在题库中按关键词搜索题目
 */
@Serializable
data class SearchQuestionRequest(
    val keyword: String,                       // 搜索关键词
    @SerialName("bank_id")
    val bankId: Long? = null,                  // 限定题库ID（可选）
    @SerialName("question_type")
    val questionType: String? = null           // 限定题目类型（可选）
)

/**
 * 自动答题配置 - 用于配置自动化答题的详细参数
 *
 * 这个配置用于浏览器自动化场景，定义了：
 * - 如何在网页上定位题目和选项元素
 * - 使用哪个AI或题库来获取答案
 * - 备用策略等
 */
@Serializable
data class AnswerQuestionConfig(
    @SerialName("action")
    val action: String = "answer_question",    // 动作类型（固定值）
    @SerialName("question_selector")
    val questionSelector: String,              // 题目的CSS选择器
    @SerialName("options_selector")
    val optionsSelector: String? = null,       // 选项的CSS选择器
    @SerialName("input_selector")
    val inputSelector: String? = null,         // 输入框的CSS选择器
    @SerialName("submit_selector")
    val submitSelector: String,                // 提交按钮的CSS选择器
    @SerialName("answer_source")
    val answerSource: String,                  // 答案来源（"ai" 或 "bank"）
    @SerialName("ai_config_id")
    val aiConfigId: Long? = null,              // AI配置ID（answer_source为"ai"时使用）
    @SerialName("knowledge_base_id")
    val knowledgeBaseId: Long? = null,         // 知识库/题库ID（answer_source为"bank"时使用）
    @SerialName("fallback_strategy")
    val fallbackStrategy: String,              // 备用策略
    @SerialName("wait_before_answer")
    val waitBeforeAnswer: Int? = null          // 答题前等待时间（毫秒）
)
