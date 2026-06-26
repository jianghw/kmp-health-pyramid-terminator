package com.terminator.shared.network.api

import com.terminator.shared.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class ReportApi(private val client: HttpClient) {

    suspend fun generateReport(reportType: String, reportDate: String? = null): ApiResponse<Report> {
        return client.post("/api/reports/generate") {
            setBody(GenerateReportRequest(reportType = reportType, reportDate = reportDate))
        }.body()
    }

    suspend fun getReports(type: String? = null): ApiResponse<List<Report>> {
        return client.get("/api/reports") {
            if (type != null) parameter("type", type)
        }.body()
    }

    suspend fun getReport(reportId: Long): ApiResponse<Report> {
        return client.get("/api/reports/$reportId").body()
    }

    suspend fun getLatestReport(type: String = "daily"): ApiResponse<Report> {
        return client.get("/api/reports/latest") {
            parameter("type", type)
        }.body()
    }

    suspend fun deleteReport(reportId: Long): ApiResponse<Unit> {
        return client.delete("/api/reports/$reportId").body()
    }
}
