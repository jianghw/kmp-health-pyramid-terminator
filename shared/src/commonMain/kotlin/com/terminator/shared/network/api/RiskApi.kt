package com.terminator.shared.network.api

import com.terminator.shared.model.ApiResponse
import com.terminator.shared.model.RiskEvent
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class RiskApi(private val client: HttpClient) {
    
    suspend fun getRiskEvents(
        page: Int = 1,
        pageSize: Int = 20
    ): ApiResponse<List<RiskEvent>> {
        return client.get("/api/risks/events") {
            parameter("page", page)
            parameter("page_size", pageSize)
        }.body()
    }
    
    suspend fun getRiskSummary(): ApiResponse<RiskSummary> {
        return client.get("/api/risks/summary").body()
    }
    
    suspend fun getAppRiskScore(appId: Long): ApiResponse<AppRiskScore> {
        return client.get("/api/risks/apps/$appId/score").body()
    }
}

@kotlinx.serialization.Serializable
data class RiskSummary(
    val total_events: Int,
    val high_risk_count: Int,
    val medium_risk_count: Int,
    val low_risk_count: Int
)

@kotlinx.serialization.Serializable
data class AppRiskScore(
    val app_id: Long,
    val app_name: String,
    val risk_score: Int,
    val risk_level: String
)
