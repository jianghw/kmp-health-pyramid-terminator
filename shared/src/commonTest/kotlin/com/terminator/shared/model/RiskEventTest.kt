package com.terminator.shared.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RiskEventTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testRiskEventDeserialization() {
        val jsonString = """
            {
                "event_id": 1,
                "user_id": 10,
                "app_id": 5,
                "event_type": "marketing_inducement",
                "severity": "high",
                "description": "检测到营销诱导内容",
                "evidence": "课程中包含限时购买话术",
                "action_taken": "blocked",
                "created_at": "2026-06-25T10:00:00Z"
            }
        """.trimIndent()

        val event = json.decodeFromString(RiskEvent.serializer(), jsonString)

        assertEquals(1L, event.eventId)
        assertEquals(10L, event.userId)
        assertEquals(5L, event.appId)
        assertEquals(RiskEventType.MARKETING_INDUCEMENT, event.eventType)
        assertEquals(RiskSeverity.HIGH, event.severity)
        assertEquals(RiskAction.BLOCKED, event.actionTaken)
        assertEquals("检测到营销诱导内容", event.description)
        assertEquals("课程中包含限时购买话术", event.evidence)
    }

    @Test
    fun testRiskEventWithNullEvidence() {
        val jsonString = """
            {
                "event_id": 2,
                "user_id": 10,
                "app_id": 5,
                "event_type": "false_claim",
                "severity": "medium",
                "description": "虚假宣传识别",
                "evidence": null,
                "action_taken": "warned",
                "created_at": "2026-06-25T11:00:00Z"
            }
        """.trimIndent()

        val event = json.decodeFromString(RiskEvent.serializer(), jsonString)
        assertNull(event.evidence)
        assertEquals(RiskAction.WARNED, event.actionTaken)
    }

    @Test
    fun testRiskEventTypeAllValues() {
        val types = mapOf(
            "marketing_inducement" to RiskEventType.MARKETING_INDUCEMENT,
            "false_claim" to RiskEventType.FALSE_CLAIM,
            "price_anomaly" to RiskEventType.PRICE_ANOMALY,
            "consumption_trigger" to RiskEventType.CONSUMPTION_TRIGGER
        )

        types.forEach { (serialized, expected) ->
            assertEquals(expected, json.decodeFromString(RiskEventType.serializer(), "\"$serialized\""))
        }
    }

    @Test
    fun testRiskSeverityAllValues() {
        val severities = mapOf(
            "low" to RiskSeverity.LOW,
            "medium" to RiskSeverity.MEDIUM,
            "high" to RiskSeverity.HIGH,
            "critical" to RiskSeverity.CRITICAL
        )

        severities.forEach { (serialized, expected) ->
            assertEquals(expected, json.decodeFromString(RiskSeverity.serializer(), "\"$serialized\""))
        }
    }

    @Test
    fun testRiskActionAllValues() {
        val actions = mapOf(
            "none" to RiskAction.NONE,
            "warned" to RiskAction.WARNED,
            "blocked" to RiskAction.BLOCKED,
            "notified_family" to RiskAction.NOTIFIED_FAMILY
        )

        actions.forEach { (serialized, expected) ->
            assertEquals(expected, json.decodeFromString(RiskAction.serializer(), "\"$serialized\""))
        }
    }

    @Test
    fun testRiskEventSerialization() {
        val event = RiskEvent(
            eventId = 3L,
            userId = 20L,
            appId = 8L,
            eventType = RiskEventType.PRICE_ANOMALY,
            severity = RiskSeverity.LOW,
            description = "价格异常",
            evidence = "商品价格高于市场均价",
            actionTaken = RiskAction.NOTIFIED_FAMILY,
            createdAt = "2026-06-25T12:00:00Z"
        )

        val encoded = json.encodeToString(RiskEvent.serializer(), event)
        val decoded = json.decodeFromString(RiskEvent.serializer(), encoded)

        assertEquals(event.eventId, decoded.eventId)
        assertEquals(event.eventType, decoded.eventType)
        assertEquals(event.severity, decoded.severity)
        assertEquals(event.actionTaken, decoded.actionTaken)
        assertEquals(event.description, decoded.description)
    }
}
