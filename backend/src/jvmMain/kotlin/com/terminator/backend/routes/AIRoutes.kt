package com.terminator.backend.routes

import com.terminator.backend.db.AIConfigs
import com.terminator.backend.db.QuestionBanks
import com.terminator.backend.db.QuestionEntries
import com.terminator.backend.model.*
import com.terminator.backend.service.AIService
import com.terminator.backend.util.EncryptionUtil
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * AI相关路由 - 定义所有AI功能的HTTP接口
 *
 * 这个文件定义了前端（Android/iOS/Web）可以通过HTTP请求访问的AI相关接口。
 * 路由（Route）就像是"门牌号"，告诉服务器"当收到某个URL的请求时，应该执行什么操作"。
 *
 * 主要功能模块：
 * 1. /api/ai - AI问答接口（向AI提问、搜索题库）
 * 2. /api/ai-configs - AI配置管理（增删改查AI配置）
 * 3. /api/question-banks - 题库管理（增删改查题库和题目）
 *
 * @param aiService AI服务实例，用于执行AI相关的业务逻辑
 */
fun Route.aiRoutes(aiService: AIService) {

    // ==========================================
    // AI问答接口 - /api/ai
    // ==========================================
    route("/api/ai") {

        /**
         * POST /api/ai/ask - 向AI提问
         *
         * 前端发送一道题目，后端调用AI接口获取答案。
         * 请求体包含：题目内容、选项、题目类型等信息。
         * 返回：AI给出的答案、置信度和解析。
         */
        post("/ask") {
            // 从HTTP请求体中解析出题目数据
            val request = call.receive<AIQuestionRequest>()

            try {
                // 调用AIService处理题目，获取AI答案
                val response = aiService.askQuestion(
                    question = request.question,
                    context = request.context,
                    options = request.options,
                    questionType = request.questionType,
                    configId = request.configId
                )
                // 返回成功响应，包装在 ApiResponse 中
                call.respond(ApiResponse(success = true, message = "查询成功", data = response))
            } catch (e: Exception) {
                // 如果AI调用失败，返回错误信息
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = e.message ?: "AI查询失败"))
            }
        }

        /**
         * POST /api/ai/search - 从题库中搜索题目
         *
         * 在本地题库中搜索匹配的题目，不调用AI接口。
         * 用于快速查找已有的标准答案。
         */
        post("/search") {
            val request = call.receive<SearchQuestionRequest>()

            // 在题库中搜索匹配的题目
            val result = aiService.searchQuestionFromBank(
                keyword = request.keyword,
                bankId = request.bankId,
                questionType = request.questionType
            )

            if (result != null) {
                // 找到匹配题目，返回答案
                call.respond(ApiResponse(success = true, message = "找到答案", data = result))
            } else {
                // 未找到匹配题目
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "未找到匹配的题目"))
            }
        }
    }

    // ==========================================
    // AI配置管理接口 - /api/ai-configs
    // ==========================================
    route("/api/ai-configs") {

        /**
         * GET /api/ai-configs - 获取所有AI配置列表
         *
         * 返回当前用户的所有AI配置，API密钥会被脱敏处理（只显示部分字符）。
         * 例如：sk-1234567890 显示为 sk-12****7890
         */
        get {
            // TODO: 实际项目中应该从登录token中获取用户ID
            val userId = 1L

            // 使用 transaction 执行数据库操作（Exposed ORM要求所有数据库操作在事务中执行）
            val configs = transaction {
                AIConfigs.selectAll().where { AIConfigs.userId eq userId }.map {
                    // 解密API密钥后进行脱敏处理
                    val encryptedKey = it[AIConfigs.apiKey]
                    val decryptedKey = EncryptionUtil.decryptOrNull(encryptedKey) ?: encryptedKey
                    val maskedKey = maskApiKey(decryptedKey)

                    // 将数据库行转换为响应对象
                    AIConfigResponse(
                        configId = it[AIConfigs.configId],
                        userId = it[AIConfigs.userId],
                        provider = it[AIConfigs.provider],
                        apiKeyMasked = maskedKey,
                        modelName = it[AIConfigs.modelName],
                        baseUrl = it[AIConfigs.baseUrl],
                        maxTokens = it[AIConfigs.maxTokens],
                        temperature = it[AIConfigs.temperature],
                        isEnabled = it[AIConfigs.isEnabled],
                        createdAt = it[AIConfigs.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        updatedAt = it[AIConfigs.updatedAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }

            call.respond(ApiResponse(success = true, message = "查询成功", data = configs))
        }

        /**
         * GET /api/ai-configs/{configId} - 获取单个AI配置详情
         *
         * 通过配置ID获取特定的AI配置信息。
         * {configId} 是URL路径参数，表示要查询的配置ID。
         */
        get("/{configId}") {
            // 从URL路径中提取配置ID，并转换为Long类型
            val configId = call.parameters["configId"]?.toLongOrNull()
            if (configId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的配置ID"))
                return@get  // 提前退出，不再执行后续代码
            }

            val config = transaction {
                AIConfigs.selectAll().where { AIConfigs.configId eq configId }.firstOrNull()?.let {
                    val encryptedKey = it[AIConfigs.apiKey]
                    val decryptedKey = EncryptionUtil.decryptOrNull(encryptedKey) ?: encryptedKey
                    val maskedKey = maskApiKey(decryptedKey)

                    AIConfigResponse(
                        configId = it[AIConfigs.configId],
                        userId = it[AIConfigs.userId],
                        provider = it[AIConfigs.provider],
                        apiKeyMasked = maskedKey,
                        modelName = it[AIConfigs.modelName],
                        baseUrl = it[AIConfigs.baseUrl],
                        maxTokens = it[AIConfigs.maxTokens],
                        temperature = it[AIConfigs.temperature],
                        isEnabled = it[AIConfigs.isEnabled],
                        createdAt = it[AIConfigs.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        updatedAt = it[AIConfigs.updatedAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }

            if (config != null) {
                call.respond(ApiResponse(success = true, message = "查询成功", data = config))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "配置不存在"))
            }
        }

        /**
         * POST /api/ai-configs - 创建新的AI配置
         *
         * 前端发送AI配置数据，后端保存到数据库。
         * 返回创建成功后的配置信息（密钥已脱敏）。
         */
        post {
            val request = call.receive<CreateAIConfigRequest>()
            val userId = 1L

            // 对API密钥进行加密存储（保护用户敏感信息）
            val encryptedApiKey = EncryptionUtil.encrypt(request.apiKey)

            val result = transaction {
                // 插入新配置到数据库（API密钥已加密）
                val configId = AIConfigs.insert {
                    it[AIConfigs.userId] = userId
                    it[provider] = request.provider
                    it[apiKey] = encryptedApiKey  // 存储加密后的密钥
                    it[modelName] = request.modelName
                    it[baseUrl] = request.baseUrl
                    it[maxTokens] = request.maxTokens
                    it[temperature] = request.temperature
                    it[isEnabled] = true
                    it[createdAt] = LocalDateTime.now()
                    it[updatedAt] = LocalDateTime.now()
                } get AIConfigs.configId

                // 对前端返回脱敏后的密钥（基于原始密钥脱敏）
                val maskedKey = maskApiKey(request.apiKey)

                AIConfigResponse(
                    configId = configId,
                    userId = userId,
                    provider = request.provider,
                    apiKeyMasked = maskedKey,
                    modelName = request.modelName,
                    baseUrl = request.baseUrl,
                    maxTokens = request.maxTokens,
                    temperature = request.temperature,
                    isEnabled = true,
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }

            call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "配置创建成功", data = result))
        }

        /**
         * PUT /api/ai-configs/{configId} - 更新AI配置
         *
         * 更新指定ID的AI配置信息。
         * PUT 方法通常用于更新整个资源。
         */
        put("/{configId}") {
            val configId = call.parameters["configId"]?.toLongOrNull()
            if (configId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的配置ID"))
                return@put
            }

            val request = call.receive<CreateAIConfigRequest>()

            // 对新密钥进行加密后存储
            val encryptedApiKey = EncryptionUtil.encrypt(request.apiKey)

            transaction {
                AIConfigs.update({ AIConfigs.configId eq configId }) {
                    it[provider] = request.provider
                    it[apiKey] = encryptedApiKey  // 存储加密后的密钥
                    it[modelName] = request.modelName
                    it[baseUrl] = request.baseUrl
                    it[maxTokens] = request.maxTokens
                    it[temperature] = request.temperature
                    it[updatedAt] = LocalDateTime.now()
                }
            }

            call.respond(ApiResponse<Nothing>(success = true, message = "配置更新成功"))
        }

        /**
         * DELETE /api/ai-configs/{configId} - 删除AI配置
         *
         * 永久删除指定ID的AI配置，此操作不可恢复。
         */
        delete("/{configId}") {
            val configId = call.parameters["configId"]?.toLongOrNull()
            if (configId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的配置ID"))
                return@delete
            }

            transaction {
                AIConfigs.deleteWhere { AIConfigs.configId eq configId }
            }

            call.respond(ApiResponse<Nothing>(success = true, message = "配置删除成功"))
        }

        /**
         * PUT /api/ai-configs/{configId}/toggle - 切换AI配置的启用状态
         *
         * 将配置从"启用"切换为"禁用"，或从"禁用"切换为"启用"。
         * 这是一个开关操作，不需要发送请求体。
         */
        put("/{configId}/toggle") {
            val configId = call.parameters["configId"]?.toLongOrNull()
            if (configId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的配置ID"))
                return@put
            }

            // 先查询当前配置
            val config = transaction {
                AIConfigs.selectAll().where { AIConfigs.configId eq configId }.firstOrNull()
            }

            if (config == null) {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "配置不存在"))
                return@put
            }

            // 切换启用状态（! 表示取反：true变false，false变true）
            transaction {
                AIConfigs.update({ AIConfigs.configId eq configId }) {
                    it[isEnabled] = !config[AIConfigs.isEnabled]
                    it[updatedAt] = LocalDateTime.now()
                }
            }

            call.respond(ApiResponse<Nothing>(success = true, message = "配置状态已更新"))
        }
    }

    // ==========================================
    // 题库管理接口 - /api/question-banks
    // ==========================================
    route("/api/question-banks") {

        /**
         * GET /api/question-banks - 获取所有题库列表
         *
         * 返回当前用户创建的所有题库信息。
         */
        get {
            val userId = 1L
            val banks = transaction {
                QuestionBanks.selectAll().where { QuestionBanks.userId eq userId }.map {
                    QuestionBankResponse(
                        bankId = it[QuestionBanks.bankId],
                        userId = it[QuestionBanks.userId],
                        bankName = it[QuestionBanks.bankName],
                        description = it[QuestionBanks.description],
                        questionCount = it[QuestionBanks.questionCount],
                        createdAt = it[QuestionBanks.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        updatedAt = it[QuestionBanks.updatedAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }

            call.respond(ApiResponse(success = true, message = "查询成功", data = banks))
        }

        /**
         * GET /api/question-banks/{bankId} - 获取单个题库详情
         *
         * 通过题库ID获取特定题库的详细信息。
         */
        get("/{bankId}") {
            val bankId = call.parameters["bankId"]?.toLongOrNull()
            if (bankId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的题库ID"))
                return@get
            }

            val bank = transaction {
                QuestionBanks.selectAll().where { QuestionBanks.bankId eq bankId }.firstOrNull()?.let {
                    QuestionBankResponse(
                        bankId = it[QuestionBanks.bankId],
                        userId = it[QuestionBanks.userId],
                        bankName = it[QuestionBanks.bankName],
                        description = it[QuestionBanks.description],
                        questionCount = it[QuestionBanks.questionCount],
                        createdAt = it[QuestionBanks.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        updatedAt = it[QuestionBanks.updatedAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }

            if (bank != null) {
                call.respond(ApiResponse(success = true, message = "查询成功", data = bank))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "题库不存在"))
            }
        }

        /**
         * POST /api/question-banks - 创建新题库
         *
         * 创建一个新的题库，初始题目数量为0。
         */
        post {
            val request = call.receive<CreateQuestionBankRequest>()
            val userId = 1L

            val result = transaction {
                val bankId = QuestionBanks.insert {
                    it[QuestionBanks.userId] = userId
                    it[bankName] = request.bankName
                    it[description] = request.description
                    it[questionCount] = 0               // 新建题库题目数量为0
                    it[createdAt] = LocalDateTime.now()
                    it[updatedAt] = LocalDateTime.now()
                } get QuestionBanks.bankId

                QuestionBankResponse(
                    bankId = bankId,
                    userId = userId,
                    bankName = request.bankName,
                    description = request.description,
                    questionCount = 0,
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }

            call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "题库创建成功", data = result))
        }

        /**
         * PUT /api/question-banks/{bankId} - 更新题库信息
         *
         * 更新题库的名称和描述。
         */
        put("/{bankId}") {
            val bankId = call.parameters["bankId"]?.toLongOrNull()
            if (bankId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的题库ID"))
                return@put
            }

            val request = call.receive<CreateQuestionBankRequest>()

            transaction {
                QuestionBanks.update({ QuestionBanks.bankId eq bankId }) {
                    it[bankName] = request.bankName
                    it[description] = request.description
                    it[updatedAt] = LocalDateTime.now()
                }
            }

            call.respond(ApiResponse<Nothing>(success = true, message = "题库更新成功"))
        }

        /**
         * DELETE /api/question-banks/{bankId} - 删除题库
         *
         * 删除题库及其所有关联的题目。
         * 注意：会先删除题库中的所有题目，再删除题库本身（级联删除）。
         */
        delete("/{bankId}") {
            val bankId = call.parameters["bankId"]?.toLongOrNull()
            if (bankId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的题库ID"))
                return@delete
            }

            transaction {
                // 先删除题库中的所有题目
                QuestionEntries.deleteWhere { QuestionEntries.bankId eq bankId }
                // 再删除题库本身
                QuestionBanks.deleteWhere { QuestionBanks.bankId eq bankId }
            }

            call.respond(ApiResponse<Nothing>(success = true, message = "题库删除成功"))
        }

        /**
         * GET /api/question-banks/{bankId}/entries - 获取题库中的所有题目
         *
         * 获取指定题库中所有题目的列表。
         * 返回的题目数据中，选项和标签会从逗号分隔的字符串转换为列表。
         */
        get("/{bankId}/entries") {
            val bankId = call.parameters["bankId"]?.toLongOrNull()
            if (bankId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的题库ID"))
                return@get
            }

            val entries = transaction {
                QuestionEntries.selectAll().where { QuestionEntries.bankId eq bankId }.map {
                    val optionsStr = it[QuestionEntries.options]
                    val tagsStr = it[QuestionEntries.tags]

                    QuestionEntryResponse(
                        entryId = it[QuestionEntries.entryId],
                        bankId = it[QuestionEntries.bankId],
                        question = it[QuestionEntries.question],
                        questionType = it[QuestionEntries.questionType],
                        options = optionsStr?.split(",")?.map { it.trim() },  // 将 "A,B,C" 转换为 ["A","B","C"]
                        correctAnswer = it[QuestionEntries.correctAnswer],
                        explanation = it[QuestionEntries.explanation],
                        tags = if (tagsStr.isNotBlank()) tagsStr.split(",").map { it.trim() } else emptyList(),
                        createdAt = it[QuestionEntries.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }

            call.respond(ApiResponse(success = true, message = "查询成功", data = entries))
        }

        /**
         * POST /api/question-banks/{bankId}/entries - 向题库添加新题目
         *
         * 向指定题库中添加一道新题目，同时更新题库的题目计数。
         * 选项列表会转换为逗号分隔的字符串存储到数据库。
         */
        post("/{bankId}/entries") {
            val bankId = call.parameters["bankId"]?.toLongOrNull()
            if (bankId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的题库ID"))
                return@post
            }

            val request = call.receive<CreateQuestionEntryRequest>()

            val result = transaction {
                // 插入新题目
                val entryId = QuestionEntries.insert {
                    it[QuestionEntries.bankId] = bankId
                    it[question] = request.question
                    it[questionType] = request.questionType
                    it[options] = request.options?.joinToString(",")  // 列表转字符串："A,B,C"
                    it[correctAnswer] = request.correctAnswer
                    it[explanation] = request.explanation
                    it[tags] = request.tags.joinToString(",")  // 标签列表转字符串
                    it[createdAt] = LocalDateTime.now()
                } get QuestionEntries.entryId

                // 更新题库的题目数量（+1）
                // SqlExpressionBuilder 允许我们在SQL中执行 "question_count + 1" 这样的表达式
                QuestionBanks.update({ QuestionBanks.bankId eq bankId }) {
                    with(SqlExpressionBuilder) {
                        it.update(QuestionBanks.questionCount, QuestionBanks.questionCount + 1)
                    }
                    it[updatedAt] = LocalDateTime.now()
                }

                QuestionEntryResponse(
                    entryId = entryId,
                    bankId = bankId,
                    question = request.question,
                    questionType = request.questionType,
                    options = request.options,
                    correctAnswer = request.correctAnswer,
                    explanation = request.explanation,
                    tags = request.tags,
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }

            call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "题目添加成功", data = result))
        }

        /**
         * DELETE /api/question-banks/{bankId}/entries/{entryId} - 删除题目
         *
         * 从题库中删除指定题目，同时更新题库的题目计数。
         * 使用 bankId 和 entryId 双重条件确保安全性。
         */
        delete("/{bankId}/entries/{entryId}") {
            val bankId = call.parameters["bankId"]?.toLongOrNull()
            val entryId = call.parameters["entryId"]?.toLongOrNull()

            if (bankId == null || entryId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的参数"))
                return@delete
            }

            transaction {
                // 使用 and 组合两个条件，确保只能删除指定题库中的题目
                QuestionEntries.deleteWhere {
                    (QuestionEntries.entryId eq entryId) and (QuestionEntries.bankId eq bankId)
                }

                // 更新题库的题目数量（-1）
                QuestionBanks.update({ QuestionBanks.bankId eq bankId }) {
                    with(SqlExpressionBuilder) {
                        it.update(QuestionBanks.questionCount, QuestionBanks.questionCount - 1)
                    }
                    it[updatedAt] = LocalDateTime.now()
                }
            }

            call.respond(ApiResponse<Nothing>(success = true, message = "题目删除成功"))
        }
    }
}

/**
 * API密钥脱敏函数 - 将密钥转换为部分隐藏的格式
 *
 * 例如：
 * - "sk-1234567890abcdef" -> "sk-12**********cdef"
 * - "short" -> "****"
 */
private fun maskApiKey(apiKey: String): String {
    return if (apiKey.length > 8) {
        "${apiKey.take(4)}${"*".repeat(apiKey.length - 8)}${apiKey.takeLast(4)}"
    } else {
        "****"
    }
}
