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
import androidx.compose.ui.unit.dp
import com.terminator.android.ui.theme.BrandColors
import com.terminator.android.ui.theme.HeaderColors
import com.terminator.android.ui.theme.CardColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchExecutionScreen(
    onBack: () -> Unit,
    onStartBatch: () -> Unit
) {
    var isRunning by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var currentTask by remember { mutableStateOf<String?>(null) }
    var completedCount by remember { mutableIntStateOf(0) }
    var failedCount by remember { mutableIntStateOf(0) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }

    val tasks = remember { getSampleBatchTasks() }
    val totalTasks = tasks.size

    LaunchedEffect(isRunning) {
        if (isRunning) {
            for ((index, task) in tasks.withIndex()) {
                currentTask = task.name
                kotlinx.coroutines.delay(1500)
                progress = (index + 1).toFloat() / totalTasks
                if (index % 3 == 2) {
                    failedCount++
                } else {
                    completedCount++
                }
            }
            isRunning = false
            currentTask = null
            showResultDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("批量执行") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HeaderColors.BatchExecution,
                    titleContentColor = BrandColors.TextDark
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = CardColors.StatsMint
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "批量任务执行",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandColors.TextDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "共 $totalTasks 个任务待执行",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BrandColors.TextSecondary
                        )

                        if (isRunning) {
                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "进度: ${(progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "已完成: $completedCount | 失败: $failedCount",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (currentTask != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "当前任务: $currentTask",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BrandColors.PrimaryDeep
                                )
                            }
                        }
                    }
                }
            }

            if (!isRunning && completedCount > 0) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (failedCount == 0) CardColors.StatsMint
                            else MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "执行结果",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BrandColors.TextDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ResultStatItem(
                                    icon = Icons.Default.CheckCircle,
                                    label = "成功",
                                    count = completedCount,
                                    color = BrandColors.PrimaryDeep
                                )
                                ResultStatItem(
                                    icon = Icons.Default.Error,
                                    label = "失败",
                                    count = failedCount,
                                    color = MaterialTheme.colorScheme.error
                                )
                                ResultStatItem(
                                    icon = Icons.Default.Percent,
                                    label = "成功率",
                                    count = "${((completedCount.toFloat() / totalTasks) * 100).toInt()}%",
                                    color = BrandColors.IconOrange
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "任务列表",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandColors.TextDark
                )
            }

            items(tasks) { task ->
                BatchTaskItem(
                    task = task,
                    isRunning = isRunning,
                    isCurrentTask = currentTask == task.name,
                    isCompleted = tasks.indexOf(task) < completedCount + failedCount
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isRunning) {
                        Button(
                            onClick = { isRunning = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("停止执行")
                        }
                    } else {
                        Button(
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandColors.PrimaryDeep
                            )
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("一键执行全部")
                        }
                    }
                }
            }
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("确认批量执行") },
                text = {
                    Column {
                        Text("即将执行以下操作:")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• 总任务数: $totalTasks")
                        Text("• 预计耗时: ${totalTasks * 2} 分钟")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("确定要开始执行吗？")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showConfirmDialog = false
                            completedCount = 0
                            failedCount = 0
                            progress = 0f
                            isRunning = true
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = BrandColors.PrimaryDeep
                        )
                    ) {
                        Text("开始执行")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }

        if (showResultDialog) {
            AlertDialog(
                onDismissRequest = { showResultDialog = false },
                title = { Text("执行完成") },
                text = {
                    Column {
                        Text("批量执行已完成!")
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("• 成功: $completedCount 个任务")
                        Text("• 失败: $failedCount 个任务")
                        Text("• 成功率: ${((completedCount.toFloat() / totalTasks) * 100).toInt()}%")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showResultDialog = false }) {
                        Text("确定")
                    }
                }
            )
        }
    }
}

@Composable
private fun ResultStatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    count: Any,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun BatchTaskItem(
    task: SampleBatchTask,
    isRunning: Boolean,
    isCurrentTask: Boolean,
    isCompleted: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCurrentTask -> CardColors.TaskItem
                isCompleted -> CardColors.StatsMint.copy(alpha = 0.5f)
                else -> CardColors.Default
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when {
                    isCompleted -> Icons.Default.CheckCircle
                    isCurrentTask -> Icons.Default.Sync
                    else -> Icons.Default.RadioButtonUnchecked
                },
                contentDescription = null,
                tint = when {
                    isCompleted -> BrandColors.PrimaryDeep
                    isCurrentTask -> BrandColors.IconOrange
                    else -> BrandColors.TextSecondary
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandColors.TextDark
                )
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandColors.TextSecondary
                )
            }
            if (isRunning && isCurrentTask) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = BrandColors.IconOrange
                )
            }
        }
    }
}

private data class SampleBatchTask(
    val id: Long,
    val name: String,
    val description: String,
    val estimatedMinutes: Int
)

private fun getSampleBatchTasks() = listOf(
    SampleBatchTask(1, "微信健康签到", "每日签到领取积分", 2),
    SampleBatchTask(2, "支付宝运动打卡", "记录今日运动步数", 1),
    SampleBatchTask(3, "京东健康问卷", "完成健康状况调查", 5),
    SampleBatchTask(4, "美团健康阅读", "阅读健康文章", 3),
    SampleBatchTask(5, "养生课堂学习", "完成今日课程", 10),
    SampleBatchTask(6, "积分商城兑换", "查看可兑换商品", 2)
)
