package com.terminator.shared.repository

import com.terminator.shared.model.RiskAction
import com.terminator.shared.model.RiskEventType
import com.terminator.shared.model.RiskSeverity
import com.terminator.shared.network.api.RiskApi
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RiskRepositoryTest {

    private fun createRiskApi(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
    ): RiskApi {
        val client = HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
        return RiskApi(client)
    }

    @Test
    fun testGetRiskEventsSuccess() = runTest {
        val riskApi = createRiskApi { _ ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "查询成功",
                        "data": [
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
                        ]
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = RiskRepository(riskApi)
        val result = repository.getRiskEvents(1, 20)

        assertTrue(result.isSuccess)
        val events = result.getOrNull()!!
        assertEquals(1, events.size)
        assertEquals(RiskEventType.MARKETING_INDUCEMENT, events[0].eventType)
        assertEquals(RiskSeverity.HIGH, events[0].severity)
        assertEquals(RiskAction.BLOCKED, events[0].actionTaken)
    }

    @Test
    fun testGetRiskEventsEmpty() = runTest {
        val riskApi = createRiskApi { _ ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "查询成功",
                        "data": []
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = RiskRepository(riskApi)
        val result = repository.getRiskEvents()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun testGetRiskSummarySuccess() = runTest {
        val riskApi = createRiskApi { _ ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "查询成功",
                        "data": {
                            "total_events": 15,
                            "high_risk_count": 3,
                            "medium_risk_count": 5,
                            "low_risk_count": 7
                        }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = RiskRepository(riskApi)
        val result = repository.getRiskSummary()

        assertTrue(result.isSuccess)
        val summary = result.getOrNull()!!
        assertEquals(15, summary.total_events)
        assertEquals(3, summary.high_risk_count)
        assertEquals(5, summary.medium_risk_count)
        assertEquals(7, summary.low_risk_count)
    }

    @Test
    fun testGetAppRiskScoreSuccess() = runTest {
        val riskApi = createRiskApi { _ ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "查询成功",
                        "data": {
                            "app_id": 5,
                            "app_name": "养生课堂",
                            "risk_score": 75,
                            "risk_level": "high"
                        }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = RiskRepository(riskApi)
        val result = repository.getAppRiskScore(5L)

        assertTrue(result.isSuccess)
        val score = result.getOrNull()!!
        assertEquals(5L, score.app_id)
        assertEquals("养生课堂", score.app_name)
        assertEquals(75, score.risk_score)
        assertEquals("high", score.risk_level)
    }

    @Test
    fun testGetRiskEventsFailure() = runTest {
        val riskApi = createRiskApi { _ ->
            respond(
                content = """{"success":false,"message":"未授权"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = RiskRepository(riskApi)
        val result = repository.getRiskEvents()

        assertTrue(result.isFailure)
        assertEquals("未授权", result.exceptionOrNull()?.message)
    }

    @Test
    fun testGetRiskEventsNetworkError() = runTest {
        val riskApi = createRiskApi { _ ->
            respondError(HttpStatusCode.ServiceUnavailable, "Service Unavailable")
        }

        val repository = RiskRepository(riskApi)
        val result = repository.getRiskEvents()

        assertTrue(result.isFailure)
    }
}
