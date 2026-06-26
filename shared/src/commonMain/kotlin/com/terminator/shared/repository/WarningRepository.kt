package com.terminator.shared.repository

import com.terminator.shared.model.*
import com.terminator.shared.network.api.WarningApi

class WarningRepository(private val warningApi: WarningApi) {

    suspend fun evaluateConsumption(appId: Long, amount: Double): Result<EvaluateConsumptionResult> {
        return try {
            val response = warningApi.evaluateConsumption(appId, amount)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWarnings(): Result<List<ConsumptionWarning>> {
        return try {
            val response = warningApi.getWarnings()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWarning(warningId: Long): Result<ConsumptionWarning> {
        return try {
            val response = warningApi.getWarning(warningId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun dismissWarning(warningId: Long): Result<Unit> {
        return try {
            val response = warningApi.dismissWarning(warningId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRules(): Result<List<ConsumptionRule>> {
        return try {
            val response = warningApi.getRules()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createRule(request: CreateRuleRequest): Result<ConsumptionRule> {
        return try {
            val response = warningApi.createRule(request)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleRule(ruleId: Long): Result<Unit> {
        return try {
            val response = warningApi.toggleRule(ruleId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRule(ruleId: Long): Result<Unit> {
        return try {
            val response = warningApi.deleteRule(ruleId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
