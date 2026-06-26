package com.terminator.shared.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TaskModelTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testTaskTemplateDeserialization() {
        val jsonString = """
            {
                "template_id": 1,
                "app_id": 10,
                "task_name": "每日签到",
                "task_type": "sign_in",
                "template_config": "{}",
                "estimated_minutes": 2,
                "reward_points": 10,
                "status": "enabled",
                "created_at": "2026-01-01T00:00:00Z"
            }
        """.trimIndent()

        val template = json.decodeFromString(TaskTemplate.serializer(), jsonString)

        assertEquals(1L, template.templateId)
        assertEquals(10L, template.appId)
        assertEquals("每日签到", template.taskName)
        assertEquals(TaskType.SIGN_IN, template.taskType)
        assertEquals(2, template.estimatedMinutes)
        assertEquals(10, template.rewardPoints)
        assertEquals(TemplateStatus.ENABLED, template.status)
    }

    @Test
    fun testTaskTypeAllValues() {
        val types = mapOf(
            "sign_in" to TaskType.SIGN_IN,
            "course_listen" to TaskType.COURSE_LISTEN,
            "survey" to TaskType.SURVEY,
            "reading" to TaskType.READING,
            "exchange" to TaskType.EXCHANGE
        )

        types.forEach { (serialized, expected) ->
            assertEquals(expected, json.decodeFromString(TaskType.serializer(), "\"$serialized\""))
        }
    }

    @Test
    fun testTemplateStatusSerialization() {
        assertEquals("enabled", json.encodeToString(TemplateStatus.serializer(), TemplateStatus.ENABLED).trim('"'))
        assertEquals("disabled", json.encodeToString(TemplateStatus.serializer(), TemplateStatus.DISABLED).trim('"'))
    }

    @Test
    fun testTaskTemplateSerialization() {
        val template = TaskTemplate(
            templateId = 5L,
            appId = 20L,
            taskName = "课程收听",
            taskType = TaskType.COURSE_LISTEN,
            templateConfig = "{\"url\":\"https://example.com\"}",
            estimatedMinutes = 15,
            rewardPoints = 50,
            status = TemplateStatus.ENABLED,
            createdAt = "2026-06-01T00:00:00Z"
        )

        val encoded = json.encodeToString(TaskTemplate.serializer(), template)
        val decoded = json.decodeFromString(TaskTemplate.serializer(), encoded)

        assertEquals(template.templateId, decoded.templateId)
        assertEquals(template.taskName, decoded.taskName)
        assertEquals(template.taskType, decoded.taskType)
        assertEquals(template.estimatedMinutes, decoded.estimatedMinutes)
    }

    @Test
    fun testTaskExecutionDeserialization() {
        val jsonString = """
            {
                "execution_id": 100,
                "user_id": 1,
                "template_id": 5,
                "status": "completed",
                "started_at": "2026-06-25T08:00:00Z",
                "completed_at": "2026-06-25T08:02:00Z",
                "result_data": "{\"points\":10}",
                "error_message": null,
                "retry_count": 0,
                "created_at": "2026-06-25T08:00:00Z"
            }
        """.trimIndent()

        val execution = json.decodeFromString(TaskExecution.serializer(), jsonString)

        assertEquals(100L, execution.executionId)
        assertEquals(1L, execution.userId)
        assertEquals(5L, execution.templateId)
        assertEquals(ExecutionStatus.COMPLETED, execution.status)
        assertNull(execution.errorMessage)
        assertEquals(0, execution.retryCount)
    }

    @Test
    fun testExecutionStatusAllValues() {
        val statuses = mapOf(
            "pending" to ExecutionStatus.PENDING,
            "running" to ExecutionStatus.RUNNING,
            "completed" to ExecutionStatus.COMPLETED,
            "failed" to ExecutionStatus.FAILED,
            "partial" to ExecutionStatus.PARTIAL
        )

        statuses.forEach { (serialized, expected) ->
            assertEquals(expected, json.decodeFromString(ExecutionStatus.serializer(), "\"$serialized\""))
        }
    }

    @Test
    fun testTaskExecutionFailedStatus() {
        val jsonString = """
            {
                "execution_id": 200,
                "user_id": 1,
                "template_id": 5,
                "status": "failed",
                "started_at": "2026-06-25T09:00:00Z",
                "completed_at": null,
                "result_data": null,
                "error_message": "网络连接超时",
                "retry_count": 3,
                "created_at": "2026-06-25T09:00:00Z"
            }
        """.trimIndent()

        val execution = json.decodeFromString(TaskExecution.serializer(), jsonString)

        assertEquals(ExecutionStatus.FAILED, execution.status)
        assertEquals("网络连接超时", execution.errorMessage)
        assertEquals(3, execution.retryCount)
    }
}
