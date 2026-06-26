package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 题库数据模型 - 表示一个题库（题目集合）
 *
 * 题库是题目的容器，用户可以创建多个题库来分类管理题目。
 * 例如："健康知识题库"、"安全常识题库"等。
 *
 * AI答题时会优先从题库中查找匹配的题目，找到则直接返回答案，
 * 找不到再调用AI接口生成答案。这样可以提高答题速度和准确率。
 *
 * 对应数据库表：question_banks
 */
@Serializable
data class QuestionBank(
    @SerialName("bank_id")
    val bankId: Long,             // 题库的唯一ID（数据库自动生成）
    @SerialName("user_id")
    val userId: Long,             // 所属用户的ID
    @SerialName("bank_name")
    val bankName: String,         // 题库名称（如"健康知识题库"）
    @SerialName("description")
    val description: String,      // 题库描述（简要说明题库内容）
    @SerialName("question_count")
    val questionCount: Int,       // 题库中的题目数量（自动统计）
    @SerialName("created_at")
    val createdAt: String,        // 创建时间
    @SerialName("updated_at")
    val updatedAt: String         // 最后更新时间
)

/**
 * 题目条目数据模型 - 表示题库中的一道具体题目
 *
 * 每个QuestionEntry代表一道完整的题目，包含：
 * - 题目内容和类型
 * - 选项（选择题时有值）
 * - 正确答案和解析
 * - 标签（用于分类和搜索）
 *
 * 对应数据库表：question_entries
 */
@Serializable
data class QuestionEntry(
    @SerialName("entry_id")
    val entryId: Long,            // 题目的唯一ID（数据库自动生成）
    @SerialName("bank_id")
    val bankId: Long,             // 所属题库的ID
    @SerialName("question")
    val question: String,         // 题目内容（如"以下哪个是健康的饮食习惯？"）
    @SerialName("question_type")
    val questionType: String,     // 题目类型（single_choice/multiple_choice/judgment/fill_blank/short_answer）
    @SerialName("options")
    val options: List<String>? = null,    // 选项列表（选择题时提供，如["A. 多吃蔬菜", "B. 多吃肉"]）
    @SerialName("correct_answer")
    val correctAnswer: String,    // 正确答案（如"A"或"A,C"或"正确"）
    @SerialName("explanation")
    val explanation: String? = null,  // 答案解析（可选，解释为什么这个答案是正确的）
    @SerialName("tags")
    val tags: List<String> = emptyList(), // 标签列表（用于分类搜索，如["健康", "饮食"]）
    @SerialName("created_at")
    val createdAt: String         // 创建时间
)

/**
 * 创建题库请求 - 用于前端向后端发送创建新题库的请求
 *
 * 创建题库时只需要提供名称和描述，
 * 其他字段（如ID、题目数量）由系统自动生成。
 */
@Serializable
data class CreateQuestionBankRequest(
    @SerialName("bank_name")
    val bankName: String,         // 题库名称
    @SerialName("description")
    val description: String       // 题库描述
)

/**
 * 创建题目请求 - 用于前端向后端发送添加新题目的请求
 *
 * 添加题目时需要提供完整的题目信息，
 * 包括所属题库、题目内容、类型、选项和正确答案。
 */
@Serializable
data class CreateQuestionEntryRequest(
    @SerialName("bank_id")
    val bankId: Long,             // 所属题库的ID
    @SerialName("question")
    val question: String,         // 题目内容
    @SerialName("question_type")
    val questionType: String,     // 题目类型
    @SerialName("options")
    val options: List<String>? = null,    // 选项列表（选择题时提供）
    @SerialName("correct_answer")
    val correctAnswer: String,    // 正确答案
    @SerialName("explanation")
    val explanation: String? = null,  // 答案解析（可选）
    @SerialName("tags")
    val tags: List<String> = emptyList()  // 标签列表（可选）
)

/**
 * 搜索题目请求 - 用于在题库中搜索匹配的题目
 *
 * 支持按关键词搜索，可以指定在某个题库中搜索，
 * 也可以指定题目类型进行过滤。
 */
@Serializable
data class SearchQuestionRequest(
    @SerialName("keyword")
    val keyword: String,              // 搜索关键词（会匹配题目内容）
    @SerialName("bank_id")
    val bankId: Long? = null,         // 指定题库ID（可选，不指定则搜索所有题库）
    @SerialName("question_type")
    val questionType: String? = null  // 指定题目类型过滤（可选）
)
