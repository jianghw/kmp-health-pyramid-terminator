package com.terminator.backend.plugins

import com.terminator.backend.routes.*
import com.terminator.backend.service.AIService
import com.terminator.backend.service.AuthService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val authService = AuthService()
    val aiService = AIService()

    routing {
        get("/") {
            call.respondText("KMP Terminator Backend is running!")
        }

        get("/api/health") {
            call.respond(mapOf(
                "status" to "ok",
                "service" to "kmp-terminator-backend",
                "version" to "1.0.0"
            ))
        }

        authRoutes(authService)
        appRoutes(authService)
        taskRoutes(authService)
        familyRoutes(authService)
        riskRoutes(authService)
        consumptionWarningRoutes()
        notificationRoutes()
        reportRoutes()
        aiRoutes(aiService)
        oneTapRoutes(aiService)  // 一键执行模式
    }
}
