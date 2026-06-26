package com.terminator.shared.repository

import com.terminator.shared.model.WarningLevel
import com.terminator.shared.model.WarningStatus
import com.terminator.shared.network.api.WarningApi
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WarningRepositoryTest {

    private fun createWarningApi(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
    ): WarningApi {
        val client = HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
        return WarningApi(client)
    }

    @Test
    fun testGetWarningsSuccess() = runTest {
        val warningApi = createWarningApi { _ ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "查询成功",
                        "data": [
                            {
                                "warning_id": 1,
                                "user_id": 10,
                                "app_id": 5,
                                "rule_type": "single_payment",
                                "warning_level": "high",
                                "title": "单笔消费预警",
                                "message": "消费金额超过阈值",
                                "current_amount": 500.0,
                                "threshold_amount": 200.0,
                                "status": "active",
                                "created_at": "2026-06-25T14:00:00Z"
                            }
                        ]
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = WarningRepository(warningApi)
        val result = repository.getWarnings()

        assertTrue(result.isSuccess)
        val warnings = result.getOrNull()!!
        assertEquals(1, warnings.size)
        assertEquals(WarningLevel.HIGH, warnings[0].warningLevel)
        assertEquals(WarningStatus.ACTIVE, warnings[0].status)
    }

    @Test
    fun testGetWarningSuccess() = runTest {
        val warningApi = createWarningApi { _ ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "查询成功",
                        "data": {
                            "warning_id": 1,
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
                        }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = WarningRepository(warningApi)
        val result = repository.getWarning(1L)

        assertTrue(result.isSuccess)
        assertEquals(WarningLevel.CRITICAL, result.getOrNull()?.warningLevel)
        assertEquals(1000.0, result.getOrNull()?.currentAmount)
    }

    @Test
    fun testDismissWarningSuccess() = runTest {
        val warningApi = createWarningApi { _ ->
            respond(
                content = """{"success":true,"message":"已忽略预警"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = WarningRepository(warningApi)
        val result = repository.dismissWarning(1L)

        assertTrue(result.isSuccess)
    }

    @Test
    fun testEvaluateConsumptionAllowed() = runTest {
        val warningApi = createWarningApi { _ ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "评估完成",
                        "data": {
                            "allowed": true,
                            "message": "消费金额在限制范围内"
                        }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = WarningRepository(warningApi)
        val result = repository.evaluateConsumption(5L, 100.0)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.allowed)
        assertFalse(result.getOrNull()!!.warning != null)
    }

    @Test
    fun testEvaluateConsumptionBlocked() = runTest {
        val warningApi = createWarningApi { _ ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "评估完成",
                        "data": {
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
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = WarningRepository(warningApi)
        val result = repository.evaluateConsumption(5L, 1000.0)

        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull()!!.allowed)
        assertTrue(result.getOrNull()!!.warning != null)
        assertEquals(WarningLevel.CRITICAL, result.getOrNull()!!.warning?.warningLevel)
    }

    @Test
    fun testGetRulesSuccess() = runTest {
        val warningApi = createWarningApi { _ ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "查询成功",
                        "data": [
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
                        ]
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = WarningRepository(warningApi)
        val result = repository.getRules()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("每日消费上限", result.getOrNull()?.first()?.ruleName)
        assertTrue(result.getOrNull()?.first()?.isEnabled!!)
    }

    @Test
    fun testToggleRuleSuccess() = runTest {
        val warningApi = createWarningApi { _ ->
            respond(
                content = """{"success":true,"message":"已切换状态"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = WarningRepository(warningApi)
        val result = repository.toggleRule(1L)

        assertTrue(result.isSuccess)
    }

    @Test
    fun testDeleteRuleSuccess() = runTest {
        val warningApi = createWarningApi { _ ->
            respond(
                content = """{"success":true,"message":"已删除规则"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = WarningRepository(warningApi)
        val result = repository.deleteRule(1L)

        assertTrue(result.isSuccess)
    }

    @Test
    fun testNetworkException() = runTest {
        val warningApi = createWarningApi { _ ->
            respondError(HttpStatusCode.GatewayTimeout, "Gateway Timeout")
        }

        val repository = WarningRepository(warningApi)
        val result = repository.getWarnings()

        assertTrue(result.isFailure)
    }
}
