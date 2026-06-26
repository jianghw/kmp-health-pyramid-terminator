package com.terminator.backend.service

import com.terminator.backend.db.AIConfigs
import com.terminator.backend.db.QuestionBanks
import com.terminator.backend.db.QuestionEntries
import com.terminator.backend.model.AIQuestionResponse
import com.terminator.backend.util.EncryptionUtil
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.security.MessageDigest

/**
 * AI提供商配置 - 内部使用的数据类
 *
 * 与数据库中的AIConfigs表对应，但只包含调用AI接口所需的字段。
 * @param provider AI提供商名称（openai/qwen/zhipu/baidu）
 * @param apiKey 解密后的API密钥
 * @param modelName 模型名称（如 gpt-4、qwen-turbo 等）
 * @param baseUrl API基础URL
 */
data class AIProviderConfig(
    val provider: String,
    val apiKey: String,
    val modelName: String,
    val baseUrl: String
)

/**
 * AI服务类 - 负责调用各大模型API进行智能答题
 *
 * 支持的AI提供商：
 * - OpenAI (GPT-4, GPT-3.5)
 * - 通义千问 (阿里云)
 * - 智谱AI (GLM-4)
 * - 文心一言 (百度)
 *
 * 核心特性：
 * 1. API密钥加密存储：使用AES加密保护用户的API密钥
 * 2. AI答题自动入库：AI回答的题目会自动存入题库，下次直接从题库返回
 * 3. 多提供商支持：支持国内外主流AI大模型
 */
class AIService {
    // HTTP客户端 - 用于发送网络请求到AI API
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    companion object {
        // 题目内容指纹的默认题库名称
        private const val AUTO_BANK_NAME = "AI自动学习题库"
    }

    /**
     * 向AI提问并获取答案
     *
     * 执行流程：
     * 1. 先从题库中搜索匹配的题目（精确匹配 + 指纹匹配）
     * 2. 如果题库中有答案，直接返回（快速路径）
     * 3. 如果题库中没有，调用AI接口获取答案
     * 4. 将AI的答案自动存入题库（自动学习）
     *
     * @param question 题目内容
     * @param context 上下文信息（可选）
     * @param options 选项列表（选择题时提供）
     * @param questionType 题目类型
     * @param configId 指定使用的AI配置ID（可选）
     * @return AI的答案、置信度和解析
     * @throws Exception 当AI调用失败时抛出异常
     */
    suspend fun askQuestion(
        question: String,
        context: String? = null,
        options: List<String>? = null,
        questionType: String,
        configId: Long? = null
    ): AIQuestionResponse = withContext(Dispatchers.IO) {
        // 第一步：尝试从题库中搜索匹配的题目
        val bankResult = searchQuestionFromBank(question, null, questionType)
        if (bankResult != null) {
            // 题库中有答案，直接返回（不消耗AI API调用次数）
            return@withContext AIQuestionResponse(
                answer = bankResult.correctAnswer,
                confidence = 0.95,  // 题库答案置信度高
                explanation = bankResult.explanation,
                provider = "题库匹配"
            )
        }

        // 第二步：获取AI配置（从数据库读取并解密API密钥）
        val config = getConfig(configId)
            ?: throw Exception("未找到可用的AI配置，请先配置AI服务")

        // 第三步：构建提示词并调用AI
        val prompt = buildPrompt(question, context, options, questionType)
        val aiResponse = callAI(prompt, config)

        // 第四步：解析AI的回答
        val parsedAnswer = extractAnswer(aiResponse.answer, questionType, options)

        // 第五步：将AI答题结果自动存入题库（自动学习机制）
        saveAnswerToBank(question, questionType, options, parsedAnswer, aiResponse.explanation, config.provider)

        AIQuestionResponse(
            answer = parsedAnswer,
            confidence = aiResponse.confidence,
            explanation = aiResponse.explanation,
            provider = config.provider
        )
    }

    /**
     * 从题库中搜索匹配的题目
     *
     * 支持两种匹配方式：
     * 1. 模糊匹配：题目内容包含关键词
     * 2. 指纹匹配：题目的MD5指纹完全一致（处理轻微格式差异）
     */
    fun searchQuestionFromBank(
        keyword: String,
        bankId: Long? = null,
        questionType: String? = null
    ): com.terminator.backend.model.QuestionEntryResponse? {
        return transaction {
            var query = QuestionEntries.selectAll()

            if (bankId != null) {
                query = query.andWhere { QuestionEntries.bankId eq bankId }
            }

            if (questionType != null) {
                query = query.andWhere { QuestionEntries.questionType eq questionType }
            }

            // 计算题目的MD5指纹，用于精确匹配
            val fingerprint = md5(keyword.trim())

            // 优先使用指纹匹配（精确），其次使用模糊匹配
            query
                .orderBy(QuestionEntries.createdAt to SortOrder.DESC)
                .firstOrNull { entry ->
                    // 指纹匹配（处理空格、标点等轻微差异）
                    md5(entry[QuestionEntries.question].trim()) == fingerprint ||
                    // 模糊匹配
                    entry[QuestionEntries.question].contains(keyword, ignoreCase = true)
                }?.let {
                    val optionsStr = it[QuestionEntries.options]
                    val tagsStr = it[QuestionEntries.tags]

                    com.terminator.backend.model.QuestionEntryResponse(
                        entryId = it[QuestionEntries.entryId],
                        bankId = it[QuestionEntries.bankId],
                        question = it[QuestionEntries.question],
                        questionType = it[QuestionEntries.questionType],
                        options = optionsStr?.split(",")?.map { it.trim() },
                        correctAnswer = it[QuestionEntries.correctAnswer],
                        explanation = it[QuestionEntries.explanation],
                        tags = if (tagsStr.isNotBlank()) tagsStr.split(",").map { it.trim() } else emptyList(),
                        createdAt = it[QuestionEntries.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
        }
    }

    /**
     * 将AI答题结果自动存入题库
     *
     * 这是"自动学习"机制的核心：当AI回答了一道题库中没有的题目时，
     * 系统会自动将"题目+答案"存入题库，下次遇到相同题目时直接从题库返回，
     * 节省AI API调用成本，提高响应速度。
     */
    private fun saveAnswerToBank(
        question: String,
        questionType: String,
        options: List<String>?,
        answer: String,
        explanation: String?,
        provider: String
    ) {
        try {
            transaction {
                // 查找或创建"AI自动学习题库"
                val autoBankId = QuestionBanks.selectAll().where { QuestionBanks.bankName eq AUTO_BANK_NAME }.firstOrNull()?.get(QuestionBanks.bankId) ?: run {
                    // 自动学习题库不存在，创建一个
                    QuestionBanks.insert {
                        it[userId] = 1  // 系统用户
                        it[bankName] = AUTO_BANK_NAME
                        it[description] = "AI答题自动生成的题库，包含AI回答过的所有题目"
                        it[questionCount] = 0
                        it[createdAt] = LocalDateTime.now()
                        it[updatedAt] = LocalDateTime.now()
                    } get QuestionBanks.bankId
                }

                // 检查题目是否已存在（避免重复入库）
                val fingerprint = md5(question.trim())
                val exists = QuestionEntries.selectAll().any { entry ->
                    md5(entry[QuestionEntries.question].trim()) == fingerprint
                }

                if (!exists) {
                    // 题目不存在，插入新记录
                    QuestionEntries.insert {
                        it[QuestionEntries.bankId] = autoBankId
                        it[QuestionEntries.question] = question
                        it[QuestionEntries.questionType] = questionType
                        it[QuestionEntries.options] = options?.joinToString(",")
                        it[QuestionEntries.correctAnswer] = answer
                        it[QuestionEntries.explanation] = explanation ?: "由$provider 自动生成"
                        it[QuestionEntries.tags] = "AI自动入库,$provider"
                        it[QuestionEntries.createdAt] = LocalDateTime.now()
                    }

                    // 更新题库的题目计数
                    QuestionBanks.update({ QuestionBanks.bankId eq autoBankId }) {
                        with(SqlExpressionBuilder) {
                            it.update(QuestionBanks.questionCount, QuestionBanks.questionCount + 1)
                        }
                        it[updatedAt] = LocalDateTime.now()
                    }
                }
            }
        } catch (e: Exception) {
            // 自动入库失败不影响主流程，只记录日志
            println("AI答题自动入库失败: ${e.message}")
        }
    }

    /**
     * 从数据库获取AI配置并解密API密钥
     *
     * @param configId 指定配置ID，为null时使用第一个启用的配置
     * @return 解密后的AI配置，如果没有可用配置返回null
     */
    private fun getConfig(configId: Long? = null): AIProviderConfig? {
        return transaction {
            val query = if (configId != null) {
                AIConfigs.selectAll().where { AIConfigs.configId eq configId }
            } else {
                AIConfigs.selectAll().where { AIConfigs.isEnabled eq true }
            }

            query.firstOrNull()?.let {
                val encryptedKey = it[AIConfigs.apiKey]
                // 解密API密钥（支持未加密的旧数据）
                val decryptedKey = EncryptionUtil.decryptOrNull(encryptedKey) ?: encryptedKey

                AIProviderConfig(
                    provider = it[AIConfigs.provider],
                    apiKey = decryptedKey,
                    modelName = it[AIConfigs.modelName],
                    baseUrl = it[AIConfigs.baseUrl]
                )
            }
        }
    }

    /**
     * 调用AI接口的核心方法
     *
     * 根据provider字段自动选择对应的AI提供商进行调用。
     * 每个提供商的API格式不同，需要分别处理。
     */
    private suspend fun callAI(prompt: String, config: AIProviderConfig): RawAIResponse {
        return when (config.provider) {
            "openai" -> callOpenAI(prompt, config)
            "qwen" -> callQwen(prompt, config)
            "zhipu" -> callZhipu(prompt, config)
            "baidu" -> callBaidu(prompt, config)
            else -> throw Exception("不支持的AI提供商: ${config.provider}")
        }
    }

    /**
     * 调用OpenAI API
     */
    private suspend fun callOpenAI(prompt: String, config: AIProviderConfig): RawAIResponse {
        val response = client.post("${config.baseUrl}/v1/chat/completions") {
            header("Authorization", "Bearer ${config.apiKey}")
            contentType(ContentType.Application.Json)
            setBody(OpenAIRequest(
                model = config.modelName,
                messages = listOf(Message("user", prompt)),
                maxTokens = 1000,
                temperature = 0.7
            ))
        }

        val result = response.body<OpenAIResponse>()
        return RawAIResponse(
            answer = result.choices.firstOrNull()?.message?.content ?: "无法获取答案",
            confidence = 0.8,
            explanation = null
        )
    }

    /**
     * 调用通义千问 API
     */
    private suspend fun callQwen(prompt: String, config: AIProviderConfig): RawAIResponse {
        val response = client.post("${config.baseUrl}/api/v1/services/aigc/text-generation/generation") {
            header("Authorization", "Bearer ${config.apiKey}")
            contentType(ContentType.Application.Json)
            setBody(QwenRequest(
                model = config.modelName,
                input = QwenInput(listOf(Message("user", prompt))),
                parameters = QwenParameters(maxTokens = 1000, temperature = 0.7)
            ))
        }

        val result = response.body<QwenResponse>()
        return RawAIResponse(
            answer = result.output.choices.firstOrNull()?.message?.content ?: "无法获取答案",
            confidence = 0.8,
            explanation = null
        )
    }

    /**
     * 调用智谱AI API
     */
    private suspend fun callZhipu(prompt: String, config: AIProviderConfig): RawAIResponse {
        val response = client.post("${config.baseUrl}/api/paas/v4/chat/completions") {
            header("Authorization", "Bearer ${config.apiKey}")
            contentType(ContentType.Application.Json)
            setBody(OpenAIRequest(
                model = config.modelName,
                messages = listOf(Message("user", prompt)),
                maxTokens = 1000,
                temperature = 0.7
            ))
        }

        val result = response.body<OpenAIResponse>()
        return RawAIResponse(
            answer = result.choices.firstOrNull()?.message?.content ?: "无法获取答案",
            confidence = 0.8,
            explanation = null
        )
    }

    /**
     * 调用文心一言（百度）API
     *
     * 文心一言使用OAuth2.0认证，需要先获取access_token，再调用API。
     * API格式与OpenAI不同，使用百度自有的接口规范。
     */
    private suspend fun callBaidu(prompt: String, config: AIProviderConfig): RawAIResponse {
        // 百度API使用 api_key 和 secret_key 获取 access_token
        // 如果配置的apiKey格式为 "api_key,secret_key"，则需要先获取token
        val apiKeyParts = config.apiKey.split(",")
        val accessToken = if (apiKeyParts.size == 2) {
            getBaiduAccessToken(apiKeyParts[0], apiKeyParts[1])
        } else {
            // 如果只有一个key，假设它已经是access_token
            config.apiKey
        }

        // 调用文心一言对话API
        val response = client.post("${config.baseUrl}/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/${config.modelName}") {
            contentType(ContentType.Application.Json)
            setBody(BaiduRequest(
                messages = listOf(Message("user", prompt)),
                temperature = 0.7,
                maxOutputTokens = 1000
            ))
            parameter("access_token", accessToken)
        }

        val result = response.body<BaiduResponse>()
        return RawAIResponse(
            answer = result.result ?: "无法获取答案",
            confidence = 0.8,
            explanation = null
        )
    }

    /**
     * 获取百度API的access_token
     *
     * 百度使用OAuth2.0认证，需要先用API Key和Secret Key换取access_token。
     * access_token有效期通常为30天，这里每次都重新获取（简单实现）。
     * 生产环境应该缓存token直到过期。
     */
    private suspend fun getBaiduAccessToken(apiKey: String, secretKey: String): String {
        val response = client.post("https://aip.baidubce.com/oauth/2.0/token") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(listOf(
                "grant_type" to "client_credentials",
                "client_id" to apiKey,
                "client_secret" to secretKey
            ).formUrlEncode())
        }

        val result = response.body<BaiduTokenResponse>()
        return result.access_token ?: throw Exception("获取百度access_token失败，请检查API Key和Secret Key")
    }

    /**
     * 构建AI提示词 - 将题目信息转换为AI能理解的格式
     *
     * 不同题目类型使用不同的提示词模板，引导AI给出标准化的答案。
     */
    private fun buildPrompt(
        question: String,
        context: String?,
        options: List<String>?,
        questionType: String
    ): String {
        val sb = StringBuilder()

        // 添加系统角色设定
        sb.appendLine("你是一个专业的知识问答助手，擅长回答各类题目。请直接给出答案，不要解释。")
        sb.appendLine()

        // 如果有上下文，先提供背景知识
        if (!context.isNullOrBlank()) {
            sb.appendLine("背景信息：$context")
            sb.appendLine()
        }

        // 添加题目内容
        sb.appendLine("题目：$question")
        sb.appendLine()

        // 根据题目类型添加选项或答案格式要求
        when (questionType) {
            "single_choice" -> {
                sb.appendLine("这是一道单选题，只能选择一个正确答案。")
                if (!options.isNullOrEmpty()) {
                    sb.appendLine("选项：")
                    options.forEachIndexed { index, option ->
                        sb.appendLine("${('A' + index)}. $option")
                    }
                }
                sb.appendLine()
                sb.appendLine("请直接回复正确选项的字母（如 A），不要解释。")
            }
            "multiple_choice" -> {
                sb.appendLine("这是一道多选题，可以选择多个正确答案。")
                if (!options.isNullOrEmpty()) {
                    sb.appendLine("选项：")
                    options.forEachIndexed { index, option ->
                        sb.appendLine("${('A' + index)}. $option")
                    }
                }
                sb.appendLine()
                sb.appendLine("请直接回复正确选项的字母（如 A,C），用逗号分隔，不要解释。")
            }
            "judgment" -> {
                sb.appendLine("这是一道判断题。")
                sb.appendLine("请直接回复'正确'或'错误'，不要解释。")
            }
            "fill_blank" -> {
                sb.appendLine("这是一道填空题。")
                sb.appendLine("请直接回复填空的答案，不要解释。")
            }
            "short_answer" -> {
                sb.appendLine("这是一道简答题。")
                sb.appendLine("请用简洁的语言回答，不要超过100字。")
            }
        }

        return sb.toString()
    }

    /**
     * 从AI的回答中提取标准化答案
     *
     * AI的回答通常是自然语言，需要从中提取出标准格式的答案。
     * 例如：从"正确答案是A"中提取出"A"。
     */
    private fun extractAnswer(rawAnswer: String, questionType: String, options: List<String>?): String {
        val answer = rawAnswer.trim()

        return when (questionType) {
            "single_choice" -> {
                // 提取单个字母答案（A/B/C/D等）
                val letterMatch = Regex("[A-Za-z]").find(answer)
                letterMatch?.value?.uppercase() ?: answer.take(1).uppercase()
            }
            "multiple_choice" -> {
                // 提取多个字母答案（A,C等）
                val letters = Regex("[A-Za-z]").findAll(answer)
                    .map { it.value.uppercase() }
                    .distinct()
                    .sorted()
                    .joinToString(",")
                letters.ifEmpty { answer }
            }
            "judgment" -> {
                // 提取判断答案（正确/错误）
                when {
                    answer.contains("正确") || answer.contains("对") || answer.lowercase().contains("correct") || answer.lowercase().contains("true") -> "正确"
                    answer.contains("错误") || answer.contains("错") || answer.lowercase().contains("wrong") || answer.lowercase().contains("false") -> "错误"
                    else -> answer
                }
            }
            else -> answer
        }
    }

    /**
     * 计算字符串的MD5哈希值
     *
     * 用于题目的指纹匹配，避免重复入库。
     * MD5是一种哈希算法，将任意长度的字符串转换为固定长度的32位十六进制字符串。
     */
    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}

// ==========================================
// AI提供商的请求/响应数据类
// ==========================================

@Serializable
data class OpenAIRequest(
    val model: String,
    val messages: List<Message>,
    val maxTokens: Int = 1000,
    val temperature: Double = 0.7
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class OpenAIResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message
)

@Serializable
data class QwenRequest(
    val model: String,
    val input: QwenInput,
    val parameters: QwenParameters
)

@Serializable
data class QwenInput(
    val messages: List<Message>
)

@Serializable
data class QwenParameters(
    val maxTokens: Int,
    val temperature: Double
)

@Serializable
data class QwenResponse(
    val output: QwenOutput
)

@Serializable
data class QwenOutput(
    val choices: List<Choice>
)

@Serializable
data class BaiduRequest(
    val messages: List<Message>,
    val temperature: Double,
    val maxOutputTokens: Int
)

@Serializable
data class BaiduResponse(
    val result: String? = null,
    val error_code: Int? = null,
    val error_msg: String? = null
)

@Serializable
data class BaiduTokenResponse(
    val access_token: String? = null,
    val error: String? = null,
    val error_description: String? = null
)

data class RawAIResponse(
    val answer: String,
    val confidence: Double,
    val explanation: String?
)
