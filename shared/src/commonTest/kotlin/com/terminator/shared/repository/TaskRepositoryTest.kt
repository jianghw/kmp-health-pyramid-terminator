package com.terminator.shared.repository

import com.terminator.shared.model.*
import com.terminator.shared.network.api.TaskApi
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TaskRepositoryTest {

    private fun createTaskApi(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
    ): TaskApi {
        val client = HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
        return TaskApi(client)
    }

    @Test
    fun testExecuteTaskSuccess() = runTest {
        val taskApi = createTaskApi { _ ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "任务已开始执行",
                        "data": {
                            "execution_id": 100,
                            "user_id": 1,
                            "template_id": 1,
                            "status": "running",
                            "started_at": "2026-06-25T08:00:00Z",
                            "completed_at": null,
                            "result_data": null,
                            "error_message": null,
                            "retry_count": 0,
                            "created_at": "2026-06-25T08:00:00Z"
                        }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = TaskRepository(taskApi)
        val result = repository.executeTask(1L)

        assertTrue(result.isSuccess)
        val execution = result.getOrNull()!!
        assertEquals(100L, execution.executionId)
        assertEquals(ExecutionStatus.RUNNING, execution.status)
    }

    @Test
    fun testExecuteTaskFailure() = runTest {
        val taskApi = createTaskApi { _ ->
            respond(
                content = """{"success":false,"message":"任务模板不存在"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = TaskRepository(taskApi)
        val result = repository.executeTask(999L)

        assertTrue(result.isFailure)
        assertEquals("任务模板不存在", result.exceptionOrNull()?.message)
    }

    @Test
    fun testBatchExecuteTasksSuccess() = runTest {
        val taskApi = createTaskApi { _ ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "批量任务已开始",
                        "data": [
                            {
                                "execution_id": 101,
                                "user_id": 1,
                                "template_id": 1,
                                "status": "pending",
                                "started_at": null,
                                "completed_at": null,
                                "result_data": null,
                                "error_message": null,
                                "retry_count": 0,
                                "created_at": "2026-06-25T08:00:00Z"
                            }
                        ]
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = TaskRepository(taskApi)
        val result = repository.batchExecuteTasks(listOf(1L, 2L))

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun testGetTaskStatusSuccess() = runTest {
        val taskApi = createTaskApi { _ ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "查询成功",
                        "data": {
                            "execution_id": 100,
                            "user_id": 1,
                            "template_id": 1,
                            "status": "completed",
                            "started_at": "2026-06-25T08:00:00Z",
                            "completed_at": "2026-06-25T08:02:00Z",
                            "result_data": "{\"points\":10}",
                            "error_message": null,
                            "retry_count": 0,
                            "created_at": "2026-06-25T08:00:00Z"
                        }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = TaskRepository(taskApi)
        val result = repository.getTaskStatus(100L)

        assertTrue(result.isSuccess)
        assertEquals(ExecutionStatus.COMPLETED, result.getOrNull()?.status)
    }

    @Test
    fun testGetTaskHistorySuccess() = runTest {
        val taskApi = createTaskApi { _ ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "查询成功",
                        "data": [
                            {
                                "execution_id": 200,
                                "user_id": 1,
                                "template_id": 1,
                                "status": "completed",
                                "started_at": "2026-06-24T08:00:00Z",
                                "completed_at": "2026-06-24T08:02:00Z",
                                "result_data": null,
                                "error_message": null,
                                "retry_count": 0,
                                "created_at": "2026-06-24T08:00:00Z"
                            }
                        ]
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = TaskRepository(taskApi)
        val result = repository.getTaskHistory(1, 20)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun testGetAppTasksSuccess() = runTest {
        val taskApi = createTaskApi { _ ->
            respond(
                content = """
                    {
                        "success": true,
                        "message": "查询成功",
                        "data": [
                            {
                                "template_id": 1,
                                "app_id": 10,
                                "task_name": "每日签到",
                                "task_type": "sign_in",
                                "template_config": "{}",
                                "estimated_minutes": 2,
                                "reward_points": 10,
                                "status": "enabled",
                                "created_at": "2026-01-01T00:00:00Z"
                            }
                        ]
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = TaskRepository(taskApi)
        val result = repository.getAppTasks(10L)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("每日签到", result.getOrNull()?.first()?.taskName)
    }

    @Test
    fun testNetworkExceptionHandling() = runTest {
        val taskApi = createTaskApi { _ ->
            respondError(HttpStatusCode.InternalServerError, "Server Error")
        }

        val repository = TaskRepository(taskApi)
        val result = repository.executeTask(1L)

        assertTrue(result.isFailure)
    }
}
