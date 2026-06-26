package com.terminator.shared.network.api

import com.terminator.shared.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class WarningApi(private val client: HttpClient) {

    suspend fun evaluateConsumption(appId: Long, amount: Double): ApiResponse<EvaluateConsumptionResult> {
        return client.post("/api/warnings/evaluate") {
            setBody(EvaluateConsumptionRequest(appId = appId, amount = amount))
        }.body()
    }

    suspend fun getWarnings(): ApiResponse<List<ConsumptionWarning>> {
        return client.get("/api/warnings").body()
    }

    suspend fun getWarning(warningId: Long): ApiResponse<ConsumptionWarning> {
        return client.get("/api/warnings/$warningId").body()
    }

    suspend fun dismissWarning(warningId: Long): ApiResponse<Unit> {
        return client.put("/api/warnings/$warningId/dismiss").body()
    }

    suspend fun getRules(): ApiResponse<List<ConsumptionRule>> {
        return client.get("/api/consumption-rules").body()
    }

    suspend fun createRule(request: CreateRuleRequest): ApiResponse<ConsumptionRule> {
        return client.post("/api/consumption-rules") {
            setBody(request)
        }.body()
    }

    suspend fun updateRule(ruleId: Long, request: CreateRuleRequest): ApiResponse<Unit> {
        return client.put("/api/consumption-rules/$ruleId") {
            setBody(request)
        }.body()
    }

    suspend fun deleteRule(ruleId: Long): ApiResponse<Unit> {
        return client.delete("/api/consumption-rules/$ruleId").body()
    }

    suspend fun toggleRule(ruleId: Long): ApiResponse<Unit> {
        return client.put("/api/consumption-rules/$ruleId/toggle").body()
    }
}
