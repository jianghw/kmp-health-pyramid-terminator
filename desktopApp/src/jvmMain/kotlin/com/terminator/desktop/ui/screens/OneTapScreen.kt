package com.terminator.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.terminator.desktop.ui.theme.BrandColors
import com.terminator.desktop.ui.theme.CardColors
import com.terminator.desktop.ui.theme.HeaderColors

data class TaskExecutionResult(
    val appName: String,
    val taskName: String,
    val status: String,
    val message: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneTapScreen(
    onNavigateBack: () -> Unit
) {
    var isExecuting by remember { mutableStateOf(false) }
    var executionResults by remember { mutableStateOf<List<TaskExecutionResult>>(emptyList()) }
    var totalTasks by remember { mutableStateOf(0) }
    var completedTasks by remember { mutableStateOf(0) }
    var failedTasks by remember { mutableStateOf(0) }
    var skippedTasks by remember { mutableStateOf(0) }
    var showResults by remember { mutableStateOf(false) }
    var pendingCount by remember { mutableStateOf(5) }
    var completedCount by remember { mutableStateOf(3) }
    var lastExecutionTime by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = BrandColors.SurfaceBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OneTapHeader(onBack = onNavigateBack)
            }

            item {
                TaskOverviewCard(
                    pendingCount = pendingCount,
                    completedCount = completedCount,
                    lastExecutionTime = lastExecutionTime,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            item {
                ExecuteButton(
                    isExecuting = isExecuting,
                    pendingCount = pendingCount,
                    onClick = {
                        if (!isExecuting) {
                            isExecuting = true
                            showResults = false
                            executionResults = emptyList()
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
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            if (showResults) {
                item {
                    ResultSummaryCard(
                        completedTasks = completedTasks,
                        failedTasks = failedTasks,
                        skippedTasks = skippedTasks,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            if (executionResults.isNotEmpty()) {
                item {
                    Text(
                        "执行详情",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandColors.TextDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    )
                }

                items(executionResults) { result ->
                    TaskResultCard(
                        result = result,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }
            }

            item {
                InfoTipCard(
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun OneTapHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(HeaderColors.OneTap)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            modifier = Modifier.size(22.dp),
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "一键执行",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color(0xFFFDE68A)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "智能自动化执行",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "解放双手，自动完成所有待办",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskOverviewCard(
    pendingCount: Int,
    completedCount: Int,
    lastExecutionTime: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = HeaderColors.OneTap.copy(alpha = 0.12f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BrandColors.CardBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TaskStatCircle(
                    count = pendingCount,
                    label = "待处理",
                    color = Color(0xFFDC2626),
                    bgColor = Color(0xFFFEF2F2)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(60.dp)
                        .background(Color(0xFFB8D4C8))
                )
                TaskStatCircle(
                    count = completedCount,
                    label = "已完成",
                    color = BrandColors.PrimaryDeep,
                    bgColor = Color(0xFFECFDF5)
                )
            }

            if (lastExecutionTime != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "上次执行: $lastExecutionTime",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrandColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun TaskStatCircle(
    count: Int,
    label: String,
    color: Color,
    bgColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = bgColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = count.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = BrandColors.TextSecondary
        )
    }
}

@Composable
private fun ExecuteButton(
    isExecuting: Boolean,
    pendingCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val enabled = !isExecuting && pendingCount > 0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) HeaderColors.OneTap
            else CardColors.QuickAction
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = { if (enabled) onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isExecuting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    "正在执行...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            } else {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "一键执行所有任务",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "点击后自动完成所有待处理任务",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultSummaryCard(
    completedTasks: Int,
    failedTasks: Int,
    skippedTasks: Int,
    modifier: Modifier = Modifier
) {
    val isSuccess = failedTasks == 0
    val isAllFail = completedTasks == 0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSuccess -> Color(0xFFECFDF5)
                isAllFail -> Color(0xFFFEF2F2)
                else -> Color(0xFFFFFBEB)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                when {
                    isSuccess -> Icons.Default.CheckCircle
                    isAllFail -> Icons.Default.Error
                    else -> Icons.Default.Warning
                },
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = when {
                    isSuccess -> Color(0xFF059669)
                    isAllFail -> Color(0xFFDC2626)
                    else -> Color(0xFFD97706)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                when {
                    isSuccess -> "全部完成！"
                    isAllFail -> "执行失败"
                    else -> "部分完成"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = when {
                    isSuccess -> Color(0xFF059669)
                    isAllFail -> Color(0xFFDC2626)
                    else -> Color(0xFFD97706)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "成功: $completedTasks | 失败: $failedTasks | 跳过: $skippedTasks",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = BrandColors.TextSecondary
            )
        }
    }
}

@Composable
private fun TaskResultCard(
    result: TaskExecutionResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColors.TaskItem
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = when (result.status) {
                    "completed" -> Color(0xFFECFDF5)
                    "failed" -> Color(0xFFFEF2F2)
                    else -> Color(0xFFF8FAF9)
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        when (result.status) {
                            "completed" -> Icons.Default.CheckCircle
                            "failed" -> Icons.Default.Error
                            "skipped" -> Icons.Default.SkipNext
                            else -> Icons.Default.HourglassEmpty
                        },
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = when (result.status) {
                            "completed" -> Color(0xFF059669)
                            "failed" -> Color(0xFFDC2626)
                            "skipped" -> Color(0xFF9CA3AF)
                            else -> BrandColors.TextSecondary
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    result.taskName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = BrandColors.TextDark
                )
                Text(
                    result.appName,
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandColors.TextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = when (result.status) {
                    "completed" -> Color(0xFFECFDF5)
                    "failed" -> Color(0xFFFEF2F2)
                    else -> Color(0xFFF3F4F6)
                }
            ) {
                Text(
                    when (result.status) {
                        "completed" -> "成功"
                        "failed" -> "失败"
                        "skipped" -> "跳过"
                        else -> "等待"
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = when (result.status) {
                        "completed" -> Color(0xFF059669)
                        "failed" -> Color(0xFFDC2626)
                        "skipped" -> Color(0xFF6B7280)
                        else -> BrandColors.TextSecondary
                    }
                )
            }
        }
    }
}

@Composable
private fun InfoTipCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F4F2)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color(0xFFD4EDDA)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = BrandColors.PrimaryDeep
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "一键执行会自动完成所有待处理的签到、听课、答题等任务。" +
                        "执行过程中请保持网络连接。",
                style = MaterialTheme.typography.bodySmall,
                color = BrandColors.TextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

private fun simulateExecution(
    onProgress: (List<TaskExecutionResult>) -> Unit,
    onComplete: (total: Int, completed: Int, failed: Int, skipped: Int) -> Unit
) {
    val mockResults = listOf(
        TaskExecutionResult("健康中国", "每日签到", "completed", "签到成功，获得10积分"),
        TaskExecutionResult("健康中国", "观看健康视频", "completed", "视频观看完成"),
        TaskExecutionResult("平安健康", "健康知识答题", "completed", "AI答题成功，答对3题"),
        TaskExecutionResult("平安健康", "阅读健康文章", "failed", "网络超时，请重试"),
        TaskExecutionResult("丁香医生", "每日签到", "skipped", "今日已签到")
    )

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
