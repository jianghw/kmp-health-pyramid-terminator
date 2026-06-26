package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskTemplateLibrary(
    @SerialName("library_id")
    val libraryId: Long,
    @SerialName("template_name")
    val templateName: String,
    @SerialName("app_type")
    val appType: String,
    @SerialName("task_type")
    val taskType: TaskType,
    val description: String,
    @SerialName("template_config")
    val templateConfig: TemplateConfig,
    @SerialName("estimated_minutes")
    val estimatedMinutes: Int,
    @SerialName("reward_points")
    val rewardPoints: Int,
    val category: TemplateCategory,
    val tags: List<String>,
    val usageCount: Int = 0,
    val rating: Float = 0f
)

@Serializable
data class TemplateConfig(
    val steps: List<TemplateStep>,
    val parameters: Map<String, String> = emptyMap(),
    val schedule: String? = null,
    val timeout: Int = 300
)

@Serializable
data class TemplateStep(
    val stepId: Int,
    val action: String,
    val description: String,
    val selector: String? = null,
    val value: String? = null,
    val waitTime: Int = 0,
    val optional: Boolean = false
)

@Serializable
enum class TemplateCategory {
    @SerialName("sign_in")
    SIGN_IN,
    @SerialName("course")
    COURSE,
    @SerialName("survey")
    SURVEY,
    @SerialName("reading")
    READING,
    @SerialName("exchange")
    EXCHANGE,
    @SerialName("custom")
    CUSTOM
}

@Serializable
data class TemplateExportData(
    val version: String,
    @SerialName("export_time")
    val exportTime: String,
    val templates: List<TaskTemplateLibrary>
)

@Serializable
data class TemplateImportResult(
    val total: Int,
    val success: Int,
    val failed: Int,
    val errors: List<String>
)

object PresetTemplates {

    fun getAllTemplates(): List<TaskTemplateLibrary> = listOf(
        createSignInTemplate(),
        createCourseListenTemplate(),
        createSurveyTemplate(),
        createReadingTemplate(),
        createExchangeTemplate()
    )

    fun getTemplatesByCategory(category: TemplateCategory): List<TaskTemplateLibrary> {
        return getAllTemplates().filter { it.category == category }
    }

    fun getTemplatesByAppType(appType: String): List<TaskTemplateLibrary> {
        return getAllTemplates().filter { it.appType == appType }
    }

    private fun createSignInTemplate() = TaskTemplateLibrary(
        libraryId = 1001,
        templateName = "每日签到",
        appType = "wechat_miniprogram",
        taskType = TaskType.SIGN_IN,
        description = "每日签到领取积分奖励",
        templateConfig = TemplateConfig(
            steps = listOf(
                TemplateStep(1, "open_app", "打开应用首页", selector = "#app-home"),
                TemplateStep(2, "click_sign_in", "点击签到按钮", selector = "#sign-in-btn", waitTime = 2),
                TemplateStep(3, "confirm", "确认签到", selector = "#confirm-btn", waitTime = 1),
                TemplateStep(4, "verify", "验证签到结果", selector = "#sign-in-status")
            ),
            parameters = mapOf(
                "reward_type" to "points",
                "reward_amount" to "10"
            ),
            schedule = "0 8 * * *"
        ),
        estimatedMinutes = 2,
        rewardPoints = 10,
        category = TemplateCategory.SIGN_IN,
        tags = listOf("签到", "每日", "积分")
    )

    private fun createCourseListenTemplate() = TaskTemplateLibrary(
        libraryId = 1002,
        templateName = "课程学习",
        appType = "wechat_miniprogram",
        taskType = TaskType.COURSE_LISTEN,
        description = "完成健康课程学习任务",
        templateConfig = TemplateConfig(
            steps = listOf(
                TemplateStep(1, "open_app", "打开应用", selector = "#app-home"),
                TemplateStep(2, "navigate_course", "进入课程页面", selector = "#course-tab"),
                TemplateStep(3, "select_course", "选择今日课程", selector = ".course-item:first"),
                TemplateStep(4, "play_video", "播放课程视频", selector = "#play-btn", waitTime = 5),
                TemplateStep(5, "wait_completion", "等待播放完成", selector = "#progress-bar", waitTime = 300),
                TemplateStep(6, "complete", "完成课程", selector = "#complete-btn", waitTime = 2)
            ),
            parameters = mapOf(
                "min_watch_time" to "300",
                "auto_play" to "true"
            ),
            timeout = 600
        ),
        estimatedMinutes = 10,
        rewardPoints = 30,
        category = TemplateCategory.COURSE,
        tags = listOf("课程", "学习", "视频")
    )

    private fun createSurveyTemplate() = TaskTemplateLibrary(
        libraryId = 1003,
        templateName = "健康问卷",
        appType = "wechat_miniprogram",
        taskType = TaskType.SURVEY,
        description = "完成健康状况问卷调查",
        templateConfig = TemplateConfig(
            steps = listOf(
                TemplateStep(1, "open_app", "打开应用", selector = "#app-home"),
                TemplateStep(2, "navigate_survey", "进入问卷页面", selector = "#survey-tab"),
                TemplateStep(3, "start_survey", "开始问卷", selector = "#start-survey-btn"),
                TemplateStep(4, "answer_questions", "自动填写答案", selector = ".question-item"),
                TemplateStep(5, "submit", "提交问卷", selector = "#submit-btn", waitTime = 2),
                TemplateStep(6, "verify", "验证提交成功", selector = ".success-message")
            ),
            parameters = mapOf(
                "auto_answer" to "true",
                "answer_strategy" to "random"
            ),
            timeout = 180
        ),
        estimatedMinutes = 5,
        rewardPoints = 20,
        category = TemplateCategory.SURVEY,
        tags = listOf("问卷", "调查", "健康")
    )

    private fun createReadingTemplate() = TaskTemplateLibrary(
        libraryId = 1004,
        templateName = "阅读打卡",
        appType = "wechat_miniprogram",
        taskType = TaskType.READING,
        description = "完成健康文章阅读任务",
        templateConfig = TemplateConfig(
            steps = listOf(
                TemplateStep(1, "open_app", "打开应用", selector = "#app-home"),
                TemplateStep(2, "navigate_article", "进入文章列表", selector = "#article-tab"),
                TemplateStep(3, "select_article", "选择推荐文章", selector = ".article-item:first"),
                TemplateStep(4, "scroll_read", "滚动阅读文章", selector = "#article-content"),
                TemplateStep(5, "complete", "完成阅读", selector = "#read-complete-btn", waitTime = 2)
            ),
            parameters = mapOf(
                "min_read_time" to "60",
                "scroll_speed" to "normal"
            ),
            timeout = 120
        ),
        estimatedMinutes = 3,
        rewardPoints = 15,
        category = TemplateCategory.READING,
        tags = listOf("阅读", "文章", "打卡")
    )

    private fun createExchangeTemplate() = TaskTemplateLibrary(
        libraryId = 1005,
        templateName = "积分兑换",
        appType = "wechat_miniprogram",
        taskType = TaskType.EXCHANGE,
        description = "使用积分兑换健康礼品",
        templateConfig = TemplateConfig(
            steps = listOf(
                TemplateStep(1, "open_app", "打开应用", selector = "#app-home"),
                TemplateStep(2, "navigate_exchange", "进入兑换页面", selector = "#exchange-tab"),
                TemplateStep(3, "check_points", "查看积分余额", selector = "#points-display"),
                TemplateStep(4, "select_item", "选择兑换商品", selector = ".exchange-item:first"),
                TemplateStep(5, "confirm_exchange", "确认兑换", selector = "#confirm-exchange-btn", waitTime = 2),
                TemplateStep(6, "verify", "验证兑换成功", selector = ".success-message")
            ),
            parameters = mapOf(
                "min_points" to "100",
                "preferred_category" to "health"
            ),
            timeout = 120
        ),
        estimatedMinutes = 3,
        rewardPoints = 0,
        category = TemplateCategory.EXCHANGE,
        tags = listOf("兑换", "积分", "礼品")
    )
}
