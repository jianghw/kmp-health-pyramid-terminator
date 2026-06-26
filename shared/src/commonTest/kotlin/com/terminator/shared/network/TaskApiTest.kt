package com.terminator.shared.network

import com.terminator.shared.model.ExecutionStatus
import com.terminator.shared.model.TaskType
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TaskApiTest {

    private fun createMockClient(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
    ): HttpClient {
        return HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }

    @Test
    fun testGetAppTasks() = runTest {
        val client = createMockClient { request ->
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
                            },
                            {
                                "template_id": 2,
                                "app_id": 10,
                                "task_name": "课程收听",
                                "task_type": "course_listen",
                                "template_config": "{}",
                                "estimated_minutes": 15,
                                "reward_points": 50,
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

        val taskApi = TaskApi(client)
        val response = taskApi.getAppTasks(10L)

        assertTrue(response.success)
        val tasks = response.data!!
        assertEquals(2, tasks.size)
        assertEquals("每日签到", tasks[0].taskName)
        assertEquals(TaskType.SIGN_IN, tasks[0].taskType)
        assertEquals("课程收听", tasks[1].taskName)
        assertEquals(TaskType.COURSE_LISTEN, tasks[1].taskType)
    }

    @Test
    fun testExecuteTask() = runTest {
        val client = createMockClient { request ->
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

        val taskApi = TaskApi(client)
        val response = taskApi.executeTask(1L)

        assertTrue(response.success)
        assertEquals(100L, response.data?.executionId)
        assertEquals(ExecutionStatus.RUNNING, response.data?.status)
    }

    @Test
    fun testBatchExecuteTasks() = runTest {
        val client = createMockClient { request ->
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
                            },
                            {
                                "execution_id": 102,
                                "user_id": 1,
                                "template_id": 2,
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

        val taskApi = TaskApi(client)
        val response = taskApi.batchExecuteTasks(listOf(1L, 2L))

        assertTrue(response.success)
        val executions = response.data!!
        assertEquals(2, executions.size)
        assertEquals(ExecutionStatus.PENDING, executions[0].status)
    }

    @Test
    fun testGetTaskStatus() = runTest {
        val client = createMockClient { request ->
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

        val taskApi = TaskApi(client)
        val response = taskApi.getTaskStatus(100L)

        assertTrue(response.success)
        assertEquals(ExecutionStatus.COMPLETED, response.data?.status)
        assertNotNull(response.data?.completedAt)
    }

    @Test
    fun testGetTaskHistory() = runTest {
        val client = createMockClient { request ->
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

        val taskApi = TaskApi(client)
        val response = taskApi.getTaskHistory(1, 20)

        assertTrue(response.success)
        assertEquals(1, response.data?.size)
        assertEquals(200L, response.data?.first()?.executionId)
    }
}
