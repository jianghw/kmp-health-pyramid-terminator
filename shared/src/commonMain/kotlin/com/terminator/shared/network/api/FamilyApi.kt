package com.terminator.shared.network.api

import com.terminator.shared.model.ApiResponse
import com.terminator.shared.model.FamilyBinding
import com.terminator.shared.model.TaskExecution
import com.terminator.shared.model.RiskEvent
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class FamilyApi(private val client: HttpClient) {
    
    suspend fun bindFamilyMember(
        elderUserId: Long,
        relationship: String
    ): ApiResponse<FamilyBinding> {
        return client.post("/api/family/bind") {
            setBody(mapOf(
                "elder_user_id" to elderUserId,
                "relationship" to relationship
            ))
        }.body()
    }
    
    suspend fun getFamilyMembers(): ApiResponse<List<FamilyBinding>> {
        return client.get("/api/family/members").body()
    }
    
    suspend fun getElderTasks(
        elderId: Long,
        page: Int = 1,
        pageSize: Int = 20
    ): ApiResponse<List<TaskExecution>> {
        return client.get("/api/family/$elderId/tasks") {
            parameter("page", page)
            parameter("page_size", pageSize)
        }.body()
    }
    
    suspend fun getElderRisks(
        elderId: Long,
        page: Int = 1,
        pageSize: Int = 20
    ): ApiResponse<List<RiskEvent>> {
        return client.get("/api/family/$elderId/risks") {
            parameter("page", page)
            parameter("page_size", pageSize)
        }.body()
    }
}
