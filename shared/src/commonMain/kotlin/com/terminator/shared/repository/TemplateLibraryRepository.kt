package com.terminator.shared.repository

import com.terminator.shared.model.PresetTemplates
import com.terminator.shared.model.TaskTemplateLibrary
import com.terminator.shared.model.TemplateCategory
import com.terminator.shared.model.TemplateExportData
import com.terminator.shared.model.TemplateImportResult
import com.terminator.shared.storage.TokenStorage
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TemplateLibraryRepository(
    private val tokenStorage: TokenStorage
) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val customTemplates = mutableListOf<TaskTemplateLibrary>()

    fun getAllTemplates(): List<TaskTemplateLibrary> {
        return PresetTemplates.getAllTemplates() + customTemplates
    }

    fun getTemplatesByCategory(category: TemplateCategory): List<TaskTemplateLibrary> {
        return getAllTemplates().filter { it.category == category }
    }

    fun getTemplatesByAppType(appType: String): List<TaskTemplateLibrary> {
        return getAllTemplates().filter { it.appType == appType }
    }

    fun searchTemplates(keyword: String): List<TaskTemplateLibrary> {
        val lowerKeyword = keyword.lowercase()
        return getAllTemplates().filter { template ->
            template.templateName.lowercase().contains(lowerKeyword) ||
                template.description.lowercase().contains(lowerKeyword) ||
                template.tags.any { it.lowercase().contains(lowerKeyword) }
        }
    }

    fun getTemplateById(libraryId: Long): TaskTemplateLibrary? {
        return getAllTemplates().find { it.libraryId == libraryId }
    }

    fun addCustomTemplate(template: TaskTemplateLibrary): TaskTemplateLibrary {
        val newId = (customTemplates.maxOfOrNull { it.libraryId } ?: 2000) + 1
        val newTemplate = template.copy(libraryId = newId)
        customTemplates.add(newTemplate)
        return newTemplate
    }

    fun removeCustomTemplate(libraryId: Long): Boolean {
        return customTemplates.removeAll { it.libraryId == libraryId }
    }

    fun exportTemplates(templateIds: List<Long>? = null): String {
        val templatesToExport = if (templateIds != null) {
            getAllTemplates().filter { it.libraryId in templateIds }
        } else {
            getAllTemplates()
        }

        val exportData = TemplateExportData(
            version = "1.0",
            exportTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
            templates = templatesToExport
        )

        return json.encodeToString(exportData)
    }

    fun importTemplates(jsonData: String): TemplateImportResult {
        return try {
            val importData = json.decodeFromString<TemplateExportData>(jsonData)

            var successCount = 0
            val errors = mutableListOf<String>()

            importData.templates.forEach { template ->
                try {
                    val exists = getAllTemplates().any {
                        it.templateName == template.templateName && it.appType == template.appType
                    }

                    if (!exists) {
                        addCustomTemplate(template)
                        successCount++
                    } else {
                        errors.add("模板 '${template.templateName}' 已存在，跳过导入")
                    }
                } catch (e: Exception) {
                    errors.add("导入模板 '${template.templateName}' 失败: ${e.message}")
                }
            }

            TemplateImportResult(
                total = importData.templates.size,
                success = successCount,
                failed = importData.templates.size - successCount,
                errors = errors
            )
        } catch (e: Exception) {
            TemplateImportResult(
                total = 0,
                success = 0,
                failed = 0,
                errors = listOf("解析导入数据失败: ${e.message}")
            )
        }
    }

    fun getTemplateCategories(): List<TemplateCategoryInfo> {
        return listOf(
            TemplateCategoryInfo(TemplateCategory.SIGN_IN, "签到", "每日签到任务"),
            TemplateCategoryInfo(TemplateCategory.COURSE, "课程", "课程学习任务"),
            TemplateCategoryInfo(TemplateCategory.SURVEY, "问卷", "问卷调查任务"),
            TemplateCategoryInfo(TemplateCategory.READING, "阅读", "阅读打卡任务"),
            TemplateCategoryInfo(TemplateCategory.EXCHANGE, "兑换", "积分兑换任务"),
            TemplateCategoryInfo(TemplateCategory.CUSTOM, "自定义", "自定义任务模板")
        )
    }

    fun getTemplateStats(): TemplateStats {
        val allTemplates = getAllTemplates()
        return TemplateStats(
            totalTemplates = allTemplates.size,
            presetTemplates = PresetTemplates.getAllTemplates().size,
            customTemplates = customTemplates.size,
            categoryCounts = allTemplates.groupBy { it.category }.mapValues { it.value.size }
        )
    }
}

data class TemplateCategoryInfo(
    val category: TemplateCategory,
    val name: String,
    val description: String
)

data class TemplateStats(
    val totalTemplates: Int,
    val presetTemplates: Int,
    val customTemplates: Int,
    val categoryCounts: Map<TemplateCategory, Int>
)
