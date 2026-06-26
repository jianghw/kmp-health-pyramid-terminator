package com.terminator.shared.repository

import com.terminator.shared.model.Report
import com.terminator.shared.network.api.ReportApi

class ReportRepository(private val reportApi: ReportApi) {

    suspend fun generateReport(reportType: String, reportDate: String? = null): Result<Report> {
        return try {
            val response = reportApi.generateReport(reportType, reportDate)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReports(type: String? = null): Result<List<Report>> {
        return try {
            val response = reportApi.getReports(type)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReport(reportId: Long): Result<Report> {
        return try {
            val response = reportApi.getReport(reportId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLatestReport(type: String = "daily"): Result<Report> {
        return try {
            val response = reportApi.getLatestReport(type)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReport(reportId: Long): Result<Unit> {
        return try {
            val response = reportApi.deleteReport(reportId)
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
