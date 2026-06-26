package com.terminator.shared.repository

import com.terminator.shared.model.ApiResponse
import com.terminator.shared.model.RiskEvent
import com.terminator.shared.network.api.AppRiskScore
import com.terminator.shared.network.api.RiskApi
import com.terminator.shared.network.api.RiskSummary

class RiskRepository(private val riskApi: RiskApi) {
    
    suspend fun getRiskEvents(page: Int = 1, pageSize: Int = 20): Result<List<RiskEvent>> {
        return try {
            val response = riskApi.getRiskEvents(page, pageSize)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRiskSummary(): Result<RiskSummary> {
        return try {
            val response = riskApi.getRiskSummary()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAppRiskScore(appId: Long): Result<AppRiskScore> {
        return try {
            val response = riskApi.getAppRiskScore(appId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
