package com.terminator.backend.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users : Table("users") {
    val userId = long("user_id").autoIncrement()
    val phone = varchar("phone", 20).uniqueIndex()
    val nickname = varchar("nickname", 50)
    val role = varchar("role", 20)
    val status = varchar("status", 20)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
    
    override val primaryKey = PrimaryKey(userId)
}

object FamilyBindings : Table("family_bindings") {
    val bindingId = long("binding_id").autoIncrement()
    val elderUserId = long("elder_user_id").references(Users.userId)
    val familyUserId = long("family_user_id").references(Users.userId)
    val relationship = varchar("relationship", 20)
    val permissions = text("permissions")
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    
    override val primaryKey = PrimaryKey(bindingId)
}

object HealthApps : Table("health_apps") {
    val appId = long("app_id").autoIncrement()
    val appName = varchar("app_name", 100)
    val appType = varchar("app_type", 20)
    val appIcon = text("app_icon")
    val riskScore = integer("risk_score").default(0)
    val status = varchar("status", 20)
    val config = text("config")
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    
    override val primaryKey = PrimaryKey(appId)
}

object TaskTemplates : Table("task_templates") {
    val templateId = long("template_id").autoIncrement()
    val appId = long("app_id").references(HealthApps.appId)
    val taskName = varchar("task_name", 100)
    val taskType = varchar("task_type", 20)
    val templateConfig = text("template_config")
    val estimatedMinutes = integer("estimated_minutes")
    val rewardPoints = integer("reward_points")
    val status = varchar("status", 20)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    
    override val primaryKey = PrimaryKey(templateId)
}

object TaskExecutions : Table("task_executions") {
    val executionId = long("execution_id").autoIncrement()
    val userId = long("user_id").references(Users.userId)
    val templateId = long("template_id").references(TaskTemplates.templateId)
    val status = varchar("status", 20)
    val startedAt = datetime("started_at").nullable()
    val completedAt = datetime("completed_at").nullable()
    val resultData = text("result_data").nullable()
    val errorMessage = text("error_message").nullable()
    val retryCount = integer("retry_count").default(0)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    
    override val primaryKey = PrimaryKey(executionId)
}

object RiskEvents : Table("risk_events") {
    val eventId = long("event_id").autoIncrement()
    val userId = long("user_id").references(Users.userId)
    val appId = long("app_id").references(HealthApps.appId)
    val eventType = varchar("event_type", 30)
    val severity = varchar("severity", 20)
    val description = text("description")
    val evidence = text("evidence").nullable()
    val actionTaken = varchar("action_taken", 20)
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(eventId)
}

object ConsumptionWarnings : Table("consumption_warnings") {
    val warningId = long("warning_id").autoIncrement()
    val userId = long("user_id").references(Users.userId)
    val appId = long("app_id").references(HealthApps.appId)
    val ruleType = varchar("rule_type", 30)
    val warningLevel = varchar("warning_level", 20)
    val title = varchar("title", 200)
    val message = text("message")
    val currentAmount = double("current_amount").default(0.0)
    val thresholdAmount = double("threshold_amount").default(0.0)
    val status = varchar("status", 20).default("active")
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(warningId)
}

object ConsumptionRules : Table("consumption_rules") {
    val ruleId = long("rule_id").autoIncrement()
    val userId = long("user_id").references(Users.userId)
    val ruleType = varchar("rule_type", 30)
    val ruleName = varchar("rule_name", 100)
    val thresholdAmount = double("threshold_amount")
    val timeWindow = varchar("time_window", 20)
    val isEnabled = bool("is_enabled").default(true)
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(ruleId)
}

object Notifications : Table("notifications") {
    val notificationId = long("notification_id").autoIncrement()
    val userId = long("user_id").references(Users.userId)
    val title = varchar("title", 200)
    val body = text("body")
    val notificationType = varchar("notification_type", 30)
    val isRead = bool("is_read").default(false)
    val relatedId = long("related_id").nullable()
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(notificationId)
}

object Reports : Table("reports") {
    val reportId = long("report_id").autoIncrement()
    val userId = long("user_id").references(Users.userId)
    val reportType = varchar("report_type", 20)
    val reportDate = varchar("report_date", 20)
    val totalConsumption = double("total_consumption").default(0.0)
    val totalWarnings = integer("total_warnings").default(0)
    val totalTasks = integer("total_tasks").default(0)
    val completedTasks = integer("completed_tasks").default(0)
    val riskEvents = integer("risk_events").default(0)
    val reportData = text("report_data")
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(reportId)
}

/**
 * AI配置表 - 存储用户的AI大模型API配置信息
 *
 * 每条记录代表一个AI服务配置，用户可以配置多个不同的AI提供商。
 * 例如：同时配置OpenAI和通义千问，在答题时可以选择使用哪个。
 *
 * 对应的API接口：/api/ai-configs
 */
object AIConfigs : Table("ai_configs") {
    val configId = long("config_id").autoIncrement()   // 配置ID（自增主键）
    val userId = long("user_id").references(Users.userId)  // 所属用户ID（外键关联Users表）
    val provider = varchar("provider", 50)              // AI提供商标识（如 "openai", "qwen", "zhipu"）
    val apiKey = text("api_key")                        // API密钥（在数据库中明文存储，返回前端时脱敏）
    val modelName = varchar("model_name", 100)          // 模型名称（如 "gpt-4", "qwen-turbo"）
    val baseUrl = text("base_url")                      // API接口地址（不同提供商地址不同）
    val maxTokens = integer("max_tokens").default(1000) // 最大token数（控制AI回答长度）
    val temperature = double("temperature").default(0.7) // 温度参数（0.0-1.0，越高越随机）
    val isEnabled = bool("is_enabled").default(true)     // 是否启用（禁用后不会被使用）
    val createdAt = datetime("created_at").default(LocalDateTime.now())               // 创建时间
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())               // 最后更新时间
    override val primaryKey = PrimaryKey(configId)       // 主键设置
}

/**
 * 题库表 - 存储用户创建的题库（题目集合）
 *
 * 一个用户可以创建多个题库，每个题库包含多道题目。
 * AI答题时会优先从题库中查找匹配的题目，提高答题效率。
 *
 * 对应的API接口：/api/question-banks
 */
object QuestionBanks : Table("question_banks") {
    val bankId = long("bank_id").autoIncrement()         // 题库ID（自增主键）
    val userId = long("user_id").references(Users.userId)  // 所属用户ID
    val bankName = varchar("bank_name", 100)             // 题库名称
    val description = text("description")                // 题库描述
    val questionCount = integer("question_count").default(0)  // 题目数量（添加/删除题目时自动更新）
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
    override val primaryKey = PrimaryKey(bankId)
}

/**
 * 题目条目表 - 存储题库中的具体题目
 *
 * 每条记录代表一道完整的题目，包含题目内容、类型、选项、正确答案等信息。
 * 选项和标签在数据库中以逗号分隔的字符串存储，读取时转换为列表。
 *
 * 对应的API接口：/api/question-banks/{bankId}/entries
 */
object QuestionEntries : Table("question_entries") {
    val entryId = long("entry_id").autoIncrement()       // 题目ID（自增主键）
    val bankId = long("bank_id").references(QuestionBanks.bankId)  // 所属题库ID（外键关联QuestionBanks表）
    val question = text("question")                      // 题目内容
    val questionType = varchar("question_type", 20)      // 题目类型（single_choice/multiple_choice/judgment/fill_blank/short_answer）
    val options = text("options").nullable()             // 选项列表（逗号分隔，如 "A.选项1,B.选项2,C.选项3"）
    val correctAnswer = text("correct_answer")           // 正确答案（如 "A" 或 "A,C" 或 "正确"）
    val explanation = text("explanation").nullable()      // 答案解析（可选，解释为什么这个答案正确）
    val tags = text("tags")                              // 标签（逗号分隔，如 "健康,饮食,营养"）
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    override val primaryKey = PrimaryKey(entryId)
}
