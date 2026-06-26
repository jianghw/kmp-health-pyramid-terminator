package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * AI配置数据模型 - 存储AI服务提供商的配置信息
 *
 * 这个类定义了一个AI配置的所有属性，包括：
 * - 使用哪个AI提供商（OpenAI、通义千问等）
 * - API密钥和模型参数
 * - 配置的启用状态
 *
 * 对应数据库表：ai_configs
 * 序列化注解 @Serializable 用于JSON数据的自动转换
 * @SerialName 用于指定JSON字段名（通常是下划线格式）
 */
@Serializable
data class AIConfig(
    @SerialName("config_id")
    val configId: Long,           // 配置的唯一ID（数据库自动生成）
    @SerialName("user_id")
    val userId: Long,             // 所属用户的ID
    @SerialName("provider")
    val provider: String,         // AI提供商名称（如 "openai", "qwen"）
    @SerialName("api_key")
    val apiKey: String,           // API密钥（调用AI接口所需的凭证）
    @SerialName("model_name")
    val modelName: String,        // 模型名称（如 "gpt-4", "qwen-turbo"）
    @SerialName("base_url")
    val baseUrl: String,          // API接口的基础地址
    @SerialName("max_tokens")
    val maxTokens: Int,           // 最大生成token数（控制AI回答长度）
    @SerialName("temperature")
    val temperature: Double,      // 温度参数（0.0-1.0，越高回答越随机/有创意）
    @SerialName("is_enabled")
    val isEnabled: Boolean,       // 是否启用该配置
    @SerialName("created_at")
    val createdAt: String,        // 创建时间
    @SerialName("updated_at")
    val updatedAt: String         // 最后更新时间
)

/**
 * AI提供商枚举 - 列出系统支持的所有AI服务
 *
 * 枚举（enum）是一种特殊的类，用于定义一组固定的常量值。
 * 每个枚举值都有一个 displayName（显示名称），用于在界面上展示。
 *
 * 使用枚举的好处：
 * - 限制只能选择预定义的AI提供商，避免输入错误
 * - 每个提供商都有对应的中文显示名称
 */
@Serializable
enum class AIProvider(val displayName: String) {
    OPENAI("OpenAI"),          // OpenAI的GPT系列模型
    QWEN("通义千问"),            // 阿里云的通义千问大模型
    ZHIPU("智谱AI"),            // 智谱AI的GLM系列模型
    BAIDU("文心一言"),           // 百度的文心一言大模型
    LOCAL("本地模型")            // 本地部署的模型（如Ollama等）
}

/**
 * AI请求数据模型 - 发送给AI的题目请求
 *
 * 当用户需要AI回答一个问题时，使用这个类来组织请求数据。
 * 包含题目内容、选项、上下文等信息。
 */
@Serializable
data class AIRequest(
    @SerialName("question")
    val question: String,           // 题目内容（必填）
    @SerialName("context")
    val context: String? = null,    // 上下文信息（可选，如相关背景知识）
    @SerialName("options")
    val options: List<String>? = null,  // 选项列表（可选，选择题时提供）
    @SerialName("question_type")
    val questionType: String        // 题目类型（如 "single_choice", "judgment"）
)

/**
 * AI响应数据模型 - AI返回的答案结果
 *
 * AI处理完题目后，通过这个类返回结果。
 * 包含答案内容、置信度（AI对答案的确信程度）和解析说明。
 */
@Serializable
data class AIResponse(
    @SerialName("answer")
    val answer: String,             // AI给出的答案
    @SerialName("confidence")
    val confidence: Double,         // 置信度（0.0-1.0，越高表示AI越确信）
    @SerialName("explanation")
    val explanation: String? = null // 答案解析（可选，解释为什么选这个答案）
)

/**
 * 答题动作配置 - 定义自动化答题的详细参数
 *
 * 这个类用于配置"自动答题"功能。它告诉系统：
 * - 在网页上如何找到题目和选项（通过CSS选择器）
 * - 答案来源是什么（AI或题库）
 * - 使用哪个AI配置和知识库
 * - 答题前等待多久
 *
 * CSS选择器 是用来在网页上定位元素的字符串，
 * 类似于"页面上第3个div里的第2个input框"这样的描述。
 */
@Serializable
data class AnswerQuestionAction(
    @SerialName("action")
    val action: String = "answer_question",  // 动作类型，固定为 "answer_question"
    @SerialName("question_selector")
    val questionSelector: String,     // 题目元素的CSS选择器
    @SerialName("options_selector")
    val optionsSelector: String? = null,  // 选项元素的CSS选择器（选择题用）
    @SerialName("input_selector")
    val inputSelector: String? = null,    // 输入框的CSS选择器（填空题用）
    @SerialName("submit_selector")
    val submitSelector: String,       // 提交按钮的CSS选择器
    @SerialName("answer_source")
    val answerSource: String,         // 答案来源（"ai"表示AI生成，"bank"表示题库查询）
    @SerialName("ai_config_id")
    val aiConfigId: Long? = null,     // 使用的AI配置ID（answer_source为"ai"时需要）
    @SerialName("knowledge_base_id")
    val knowledgeBaseId: Long? = null,    // 使用的知识库/题库ID（answer_source为"bank"时需要）
    @SerialName("fallback_strategy")
    val fallbackStrategy: String,     // 备用策略（当首选方式失败时的处理方式）
    @SerialName("wait_before_answer")
    val waitBeforeAnswer: Int? = null // 答题前等待时间（毫秒，用于模拟人工操作）
)
