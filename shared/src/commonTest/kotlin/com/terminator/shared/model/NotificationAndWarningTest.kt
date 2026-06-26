package com.terminator.shared.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NotificationAndWarningTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testNotificationDeserialization() {
        val jsonString = """
            {
                "notification_id": 1,
                "user_id": 10,
                "title": "任务完成",
                "body": "微信健康打卡已完成",
                "notification_type": "task",
                "is_read": false,
                "related_id": 100,
                "created_at": "2026-06-25T08:00:00Z"
            }
        """.trimIndent()

        val notification = json.decodeFromString(Notification.serializer(), jsonString)

        assertEquals(1L, notification.notificationId)
        assertEquals(10L, notification.userId)
        assertEquals("任务完成", notification.title)
        assertEquals("微信健康打卡已完成", notification.body)
        assertEquals(NotificationType.TASK, notification.notificationType)
        assertFalse(notification.isRead)
        assertEquals(100L, notification.relatedId)
    }

    @Test
    fun testNotificationWithNullRelatedId() {
        val jsonString = """
            {
                "notification_id": 2,
                "user_id": 10,
                "title": "系统通知",
                "body": "系统维护通知",
                "notification_type": "system",
                "is_read": true,
                "created_at": "2026-06-25T09:00:00Z"
            }
        """.trimIndent()

        val notification = json.decodeFromString(Notification.serializer(), jsonString)
        assertNull(notification.relatedId)
        assertTrue(notification.isRead)
    }

    @Test
    fun testNotificationTypeAllValues() {
        val types = mapOf(
            "warning" to NotificationType.WARNING,
            "task" to NotificationType.TASK,
            "risk" to NotificationType.RISK,
            "report" to NotificationType.REPORT,
            "system" to NotificationType.SYSTEM
        )

        types.forEach { (serialized, expected) ->
            assertEquals(expected, json.decodeFromString(NotificationType.serializer(), "\"$serialized\""))
        }
    }

    @Test
    fun testNotificationSummaryDeserialization() {
        val jsonString = """
            {
                "total_count": 25,
                "unread_count": 5
            }
        """.trimIndent()

        val summary = json.decodeFromString(NotificationSummary.serializer(), jsonString)
        assertEquals(25, summary.totalCount)
        assertEquals(5, summary.unreadCount)
    }

    @Test
    fun testConsumptionWarningDeserialization() {
        val jsonString = """
            {
                "warning_id": 1,
                "user_id": 10,
                "app_id": 5,
                "rule_type": "single_payment",
                "warning_level": "high",
                "title": "单笔消费预警",
                "message": "本次消费金额已超过预设阈值",
                "current_amount": 500.0,
                "threshold_amount": 200.0,
                "status": "active",
                "created_at": "2026-06-25T14:00:00Z"
            }
        """.trimIndent()

        val warning = json.decodeFromString(ConsumptionWarning.serializer(), jsonString)

        assertEquals(1L, warning.warningId)
        assertEquals(10L, warning.userId)
        assertEquals(5L, warning.appId)
        assertEquals("single_payment", warning.ruleType)
        assertEquals(WarningLevel.HIGH, warning.warningLevel)
        assertEquals("单笔消费预警", warning.title)
        assertEquals(500.0, warning.currentAmount)
        assertEquals(200.0, warning.thresholdAmount)
        assertEquals(WarningStatus.ACTIVE, warning.status)
    }

    @Test
    fun testWarningLevelAllValues() {
        val levels = mapOf(
            "low" to WarningLevel.LOW,
            "medium" to WarningLevel.MEDIUM,
            "high" to WarningLevel.HIGH,
            "critical" to WarningLevel.CRITICAL
        )

        levels.forEach { (serialized, expected) ->
            assertEquals(expected, json.decodeFromString(WarningLevel.serializer(), "\"$serialized\""))
        }
    }

    @Test
    fun testWarningStatusSerialization() {
        assertEquals("active", json.encodeToString(WarningStatus.serializer(), WarningStatus.ACTIVE).trim('"'))
        assertEquals("dismissed", json.encodeToString(WarningStatus.serializer(), WarningStatus.DISMISSED).trim('"'))
    }

    @Test
    fun testConsumptionRuleDeserialization() {
        val jsonString = """
            {
                "rule_id": 1,
                "user_id": 10,
                "rule_type": "daily_total",
                "rule_name": "每日消费上限",
                "threshold_amount": 300.0,
                "time_window": "daily",
                "is_enabled": true,
                "created_at": "2026-06-01T00:00:00Z"
            }
        """.trimIndent()

        val rule = json.decodeFromString(ConsumptionRule.serializer(), jsonString)

        assertEquals(1L, rule.ruleId)
        assertEquals("每日消费上限", rule.ruleName)
        assertEquals(300.0, rule.thresholdAmount)
        assertEquals("daily", rule.timeWindow)
        assertTrue(rule.isEnabled)
    }

    @Test
    fun testEvaluateConsumptionResult() {
        val jsonString = """
            {
                "allowed": false,
                "warning": {
                    "warning_id": 2,
                    "user_id": 10,
                    "app_id": 5,
                    "rule_type": "single_payment",
                    "warning_level": "critical",
                    "title": "高额消费预警",
                    "message": "消费金额严重超标",
                    "current_amount": 1000.0,
                    "threshold_amount": 200.0,
                    "status": "active",
                    "created_at": "2026-06-25T15:00:00Z"
                },
                "message": "消费金额超过限制"
            }
        """.trimIndent()

        val result = json.decodeFromString(EvaluateConsumptionResult.serializer(), jsonString)

        assertFalse(result.allowed)
        assertEquals("消费金额超过限制", result.message)
        assertEquals(WarningLevel.CRITICAL, result.warning?.warningLevel)
    }

    @Test
    fun testEvaluateConsumptionResultAllowed() {
        val jsonString = """
            {
                "allowed": true,
                "message": "消费金额在限制范围内"
            }
        """.trimIndent()

        val result = json.decodeFromString(EvaluateConsumptionResult.serializer(), jsonString)

        assertTrue(result.allowed)
        assertNull(result.warning)
    }

    @Test
    fun testApiResponseDeserialization() {
        val jsonString = """
            {
                "success": true,
                "message": "操作成功",
                "data": {
                    "user_id": 1,
                    "phone": "13800138000",
                    "nickname": "张大爷",
                    "role": "elder",
                    "status": "enabled",
                    "created_at": "2026-01-01T00:00:00Z",
                    "updated_at": "2026-01-01T00:00:00Z"
                }
            }
        """.trimIndent()

        val response = json.decodeFromString(ApiResponse.serializer(User.serializer()), jsonString)

        assertTrue(response.success)
        assertEquals("操作成功", response.message)
        assertEquals(1L, response.data?.userId)
        assertEquals("张大爷", response.data?.nickname)
    }

    @Test
    fun testApiResponseWithoutData() {
        val jsonString = """
            {
                "success": false,
                "message": "操作失败",
                "code": "ERR_001"
            }
        """.trimIndent()

        val response = json.decodeFromString(ApiResponse.serializer(User.serializer()), jsonString)

        assertFalse(response.success)
        assertEquals("操作失败", response.message)
        assertNull(response.data)
        assertEquals("ERR_001", response.code)
    }
}
