package com.terminator.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 任务执行结果数据类
 */
data class TaskExecutionResult(
    val appName: String,
    val taskName: String,
    val status: String,  // completed, failed, skipped, pending
    val message: String
)

/**
 * 执行状态数据类
 */
data class ExecutionStatus(
    val pendingCount: Int,
    val completedCount: Int,
    val lastExecutionTime: String?,
    val appStatuses: List<AppTaskStatus>
)

data class AppTaskStatus(
    val appName: String,
    val totalTasks: Int,
    val completedTasks: Int
)

/**
 * 一键执行界面 - 为老年人设计的简化操作界面
 *
 * 设计理念：
 * 1. 大按钮：一键执行所有任务，老年人只需点击一次
 * 2. 清晰反馈：用大字体和图标展示执行结果
 * 3. 简单易懂：避免复杂的专业术语
 *
 * 主要功能：
 * - 一键执行按钮：点击后自动完成所有待处理任务
 * - 实时进度：显示当前执行状态
 * - 结果摘要：展示成功/失败/跳过的任务数量
 * - 详细列表：查看每个任务的执行结果
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneTapScreen(
    onNavigateBack: () -> Unit
) {
    // ==========================================
    // 状态变量
    // ==========================================
    var isExecuting by remember { mutableStateOf(false) }
    var executionResults by remember { mutableStateOf<List<TaskExecutionResult>>(emptyList()) }
    var totalTasks by remember { mutableStateOf(0) }
    var completedTasks by remember { mutableStateOf(0) }
    var failedTasks by remember { mutableStateOf(0) }
    var skippedTasks by remember { mutableStateOf(0) }
    var showResults by remember { mutableStateOf(false) }

    // 待处理任务状态
    var pendingCount by remember { mutableStateOf(5) }  // 模拟数据
    var completedCount by remember { mutableStateOf(3) }  // 模拟数据
    var lastExecutionTime by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("一键执行") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // 顶部状态卡片
            // ==========================================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "今日任务概览",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // 任务统计
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TaskStatItem(
                                count = pendingCount,
                                label = "待处理",
                                color = MaterialTheme.colorScheme.error
                            )
                            TaskStatItem(
                                count = completedCount,
                                label = "已完成",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (lastExecutionTime != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "上次执行: $lastExecutionTime",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 一键执行按钮（核心功能）
            // ==========================================
            item {
                Button(
                    onClick = {
                        if (!isExecuting) {
                            isExecuting = true
                            showResults = false
                            executionResults = emptyList()
                            // TODO: 调用后端 /api/one-tap/execute 接口
                            // 模拟执行过程
                            simulateExecution(
                                onProgress = { results ->
                                    executionResults = results
                                },
                                onComplete = { total, completed, failed, skipped ->
                                    totalTasks = total
                                    completedTasks = completed
                                    failedTasks = failed
                                    skippedTasks = skipped
                                    isExecuting = false
                                    showResults = true
                                    pendingCount = failed + skipped
                                    completedCount += completed
                                    lastExecutionTime = "刚刚"
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    enabled = !isExecuting && pendingCount > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isExecuting)
                            MaterialTheme.colorScheme.surfaceVariant
                        else
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isExecuting) {
                        // 执行中状态
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "正在执行...",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        // 待执行状态
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "一键执行所有任务",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "点击后自动完成所有待处理任务",
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 执行结果摘要
            // ==========================================
            if (showResults) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                failedTasks == 0 -> MaterialTheme.colorScheme.primaryContainer
                                completedTasks == 0 -> MaterialTheme.colorScheme.errorContainer
                                else -> MaterialTheme.colorScheme.tertiaryContainer
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                when {
                                    failedTasks == 0 -> Icons.Default.CheckCircle
                                    completedTasks == 0 -> Icons.Default.Error
                                    else -> Icons.Default.Warning
                                },
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = when {
                                    failedTasks == 0 -> MaterialTheme.colorScheme.primary
                                    completedTasks == 0 -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.tertiary
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                when {
                                    failedTasks == 0 -> "全部完成！"
                                    completedTasks == 0 -> "执行失败"
                                    else -> "部分完成"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "成功: $completedTasks | 失败: $failedTasks | 跳过: $skippedTasks",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 详细执行结果列表
            // ==========================================
            if (executionResults.isNotEmpty()) {
                item {
                    Text(
                        "执行详情",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                items(executionResults) { result ->
                    TaskResultCard(result)
                }
            }

            // ==========================================
            // 底部提示信息
            // ==========================================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "一键执行会自动完成所有待处理的签到、听课、答题等任务。" +
                            "执行过程中请保持网络连接。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * 任务统计项组件
 */
@Composable
fun TaskStatItem(
    count: Int,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 任务结果卡片组件
 */
@Composable
fun TaskResultCard(result: TaskExecutionResult) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 状态图标
            Icon(
                when (result.status) {
                    "completed" -> Icons.Default.CheckCircle
                    "failed" -> Icons.Default.Error
                    "skipped" -> Icons.Default.SkipNext
                    else -> Icons.Default.HourglassEmpty
                },
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = when (result.status) {
                    "completed" -> MaterialTheme.colorScheme.primary
                    "failed" -> MaterialTheme.colorScheme.error
                    "skipped" -> MaterialTheme.colorScheme.outline
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 任务信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    result.taskName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    result.appName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 状态标签
            Badge(
                containerColor = when (result.status) {
                    "completed" -> MaterialTheme.colorScheme.primary
                    "failed" -> MaterialTheme.colorScheme.error
                    "skipped" -> MaterialTheme.colorScheme.outline
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Text(
                    when (result.status) {
                        "completed" -> "成功"
                        "failed" -> "失败"
                        "skipped" -> "跳过"
                        else -> "等待"
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * 模拟执行过程（实际项目中应调用后端API）
 */
private fun simulateExecution(
    onProgress: (List<TaskExecutionResult>) -> Unit,
    onComplete: (total: Int, completed: Int, failed: Int, skipped: Int) -> Unit
) {
    // 模拟数据
    val mockResults = listOf(
        TaskExecutionResult("健康中国", "每日签到", "completed", "签到成功，获得10积分"),
        TaskExecutionResult("健康中国", "观看健康视频", "completed", "视频观看完成"),
        TaskExecutionResult("平安健康", "健康知识答题", "completed", "AI答题成功，答对3题"),
        TaskExecutionResult("平安健康", "阅读健康文章", "failed", "网络超时，请重试"),
        TaskExecutionResult("丁香医生", "每日签到", "skipped", "今日已签到")
    )

    // 模拟逐步执行
    val results = mutableListOf<TaskExecutionResult>()
    mockResults.forEachIndexed { index, result ->
        Thread {
            Thread.sleep((500..1500).random().toLong())
            results.add(result)
            onProgress(results.toList())

            if (index == mockResults.size - 1) {
                onComplete(
                    mockResults.size,
                    mockResults.count { it.status == "completed" },
                    mockResults.count { it.status == "failed" },
                    mockResults.count { it.status == "skipped" }
                )
            }
        }.start()
    }
}
