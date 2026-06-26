package com.terminator.backend.routes

import com.terminator.backend.db.HealthApps
import com.terminator.backend.db.RiskEvents
import com.terminator.backend.model.ApiResponse
import com.terminator.backend.model.AppRiskScoreResponse
import com.terminator.backend.model.RiskEventResponse
import com.terminator.backend.model.RiskSummaryResponse
import com.terminator.backend.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Route.riskRoutes(authService: AuthService) {
    route("/api/risks") {
        get("/events") {
            val events = transaction {
                RiskEvents.selectAll().map {
                    RiskEventResponse(
                        eventId = it[RiskEvents.eventId],
                        userId = it[RiskEvents.userId],
                        appId = it[RiskEvents.appId],
                        eventType = it[RiskEvents.eventType],
                        severity = it[RiskEvents.severity],
                        description = it[RiskEvents.description],
                        evidence = it[RiskEvents.evidence],
                        actionTaken = it[RiskEvents.actionTaken],
                        createdAt = it[RiskEvents.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }
            
            call.respond(ApiResponse(success = true, message = "查询成功", data = events))
        }
        
        get("/summary") {
            val summary = transaction {
                val total = RiskEvents.selectAll().count()
                val highRisk = RiskEvents.selectAll().where { RiskEvents.severity eq "high" }.count()
                val mediumRisk = RiskEvents.selectAll().where { RiskEvents.severity eq "medium" }.count()
                val lowRisk = RiskEvents.selectAll().where { RiskEvents.severity eq "low" }.count()
                
                RiskSummaryResponse(
                    totalEvents = total.toInt(),
                    highRiskCount = highRisk.toInt(),
                    mediumRiskCount = mediumRisk.toInt(),
                    lowRiskCount = lowRisk.toInt()
                )
            }
            
            call.respond(ApiResponse(success = true, message = "查询成功", data = summary))
        }
        
        get("/apps/{appId}/score") {
            val appId = call.parameters["appId"]?.toLongOrNull()
            if (appId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Nothing>(success = false, message = "无效的应用ID"))
                return@get
            }
            
            val appRisk = transaction {
                HealthApps.selectAll().where { HealthApps.appId eq appId }.firstOrNull()?.let {
                    AppRiskScoreResponse(
                        appId = it[HealthApps.appId],
                        appName = it[HealthApps.appName],
                        riskScore = it[HealthApps.riskScore],
                        riskLevel = when {
                            it[HealthApps.riskScore] >= 80 -> "critical"
                            it[HealthApps.riskScore] >= 60 -> "high"
                            it[HealthApps.riskScore] >= 40 -> "medium"
                            else -> "low"
                        }
                    )
                }
            }
            
            if (appRisk != null) {
                call.respond(ApiResponse(success = true, message = "查询成功", data = appRisk))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Nothing>(success = false, message = "应用不存在"))
            }
        }
    }
}
