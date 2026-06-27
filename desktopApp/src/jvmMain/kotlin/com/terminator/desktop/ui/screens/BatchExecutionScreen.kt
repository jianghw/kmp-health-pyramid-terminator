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
import androidx.compose.ui.unit.dp
import com.terminator.desktop.ui.theme.BrandColors
import com.terminator.desktop.ui.theme.CardColors
import com.terminator.desktop.ui.theme.HeaderColors

/**
 * 批量执行任务数据类
 */
data class BatchTaskItem(
    val id: Long,
    val appName: String,
    val taskName: String,
    val taskType: String,
    val isSelected: Boolean = false,
    val status: String = "pending" // pending, running, completed, failed
)

/**
 * 批量执行界面 - 桌面端
 * 用户可以选择多个任务并批量执行
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchExecutionScreen(
    onNavigateBack: () -> Unit
) {
    var tasks by remember {
        mutableStateOf(
            listOf(
                BatchTaskItem(1, "微信健康", "每日签到", "签到任务"),
                BatchTaskItem(2, "微信健康", "健康打卡", "打卡任务"),
                BatchTaskItem(3, "养生课堂", "观看视频课程", "学习任务"),
                BatchTaskItem(4, "养生课堂", "完成课后测验", "答题任务"),
                BatchTaskItem(5, "健康积分", "积分兑换", "兑换任务"),
                BatchTaskItem(6, "运动健康", "步数同步", "同步任务"),
                BatchTaskItem(7, "平安健康", "健康知识答题", "答题任务"),
                BatchTaskItem(8, "平安健康", "阅读文章", "阅读任务")
            )
        )
    }
    var isExecuting by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    Scaffold(
        containerColor = BrandColors.SurfaceBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            BatchExecutionHeader(onBack = onNavigateBack)

            // 内容区域
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 统计卡片
                item {
                    BatchStatsCard(
                        totalTasks = tasks.size,
                        selectedTasks = tasks.count { it.isSelected },
                        completedTasks = tasks.count { it.status == "completed" }
                    )
                }

                // 操作按钮区域
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 全选按钮
                        OutlinedButton(
                            onClick = {
                                val allSelected = tasks.all { it.isSelected }
                                tasks = tasks.map { it.copy(isSelected = !allSelected) }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                if (tasks.all { it.isSelected }) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (tasks.all { it.isSelected }) "取消全选" else "全选")
                        }

                        // 批量执行按钮
                        Button(
                            onClick = {
                                if (!isExecuting) {
                                    isExecuting = true
                                    progress = 0f
                                    // 模拟执行过程
                                    val selectedTasks = tasks.filter { it.isSelected }
                                    if (selectedTasks.isNotEmpty()) {
                                        simulateBatchExecution(
                                            selectedTasks.size,
                                            onProgress = { p -> progress = p },
                                            onComplete = {
                                                isExecuting = false
                                                tasks = tasks.map { task ->
                                                    if (task.isSelected) task.copy(status = "completed")
                                                    else task
                                                }
                                            }
                                        )
                                    }
                                }
                            },
                            enabled = !isExecuting && tasks.any { it.isSelected },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandColors.PrimaryDeep,
                                disabledContainerColor = BrandColors.TextMuted
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isExecuting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("执行中...")
                            } else {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("批量执行")
                            }
                        }
                    }
                }

                // 进度条
                if (isExecuting) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = CardColors.StatsMint
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "执行进度",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "${(progress * 100).toInt()}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandColors.PrimaryDeep
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    color = BrandColors.PrimaryDeep,
                                    trackColor = BrandColors.PrimaryLight
                                )
                            }
                        }
                    }
                }

                // 任务列表标题
                item {
                    Text(
                        "任务列表",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandColors.TextDark
                    )
                }

                // 任务列表
                items(tasks) { task ->
                    BatchTaskCard(
                        task = task,
                        onToggleSelect = {
                            tasks = tasks.map {
                                if (it.id == task.id) it.copy(isSelected = !it.isSelected) else it
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BatchExecutionHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(HeaderColors.OneTap)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.3f)
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            modifier = Modifier.size(20.dp),
                            tint = BrandColors.TextDark
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "批量执行",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BrandColors.TextDark
                )
            }
        }
    }
}

@Composable
private fun BatchStatsCard(
    totalTasks: Int,
    selectedTasks: Int,
    completedTasks: Int
) {
    Card(
        modifier = Modifier
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BatchStatItem(
                count = totalTasks,
                label = "总任务",
                color = BrandColors.TextDark,
                bgColor = CardColors.TaskItem
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(60.dp)
                    .background(BrandColors.Divider)
            )
            BatchStatItem(
                count = selectedTasks,
                label = "已选择",
                color = BrandColors.IconBlue,
                bgColor = BrandColors.IconBlueBg
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(60.dp)
                    .background(BrandColors.Divider)
            )
            BatchStatItem(
                count = completedTasks,
                label = "已完成",
                color = BrandColors.PrimaryDeep,
                bgColor = BrandColors.PrimaryBg
            )
        }
    }
}

@Composable
private fun BatchStatItem(
    count: Int,
    label: String,
    color: Color,
    bgColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = bgColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = BrandColors.TextSecondary
        )
    }
}

@Composable
private fun BatchTaskCard(
    task: BatchTaskItem,
    onToggleSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isSelected) BrandColors.PrimaryBg else BrandColors.CardBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onToggleSelect
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 选择框
            Checkbox(
                checked = task.isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(
                    checkedColor = BrandColors.PrimaryDeep,
                    checkmarkColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 任务图标
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(10.dp),
                color = when (task.status) {
                    "completed" -> BrandColors.PrimaryBg
                    "running" -> BrandColors.IconBlueBg
                    "failed" -> Color(0xFFFEF2F2)
                    else -> CardColors.TaskItem
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        when (task.status) {
                            "completed" -> Icons.Default.CheckCircle
                            "running" -> Icons.Default.PlayCircle
                            "failed" -> Icons.Default.Error
                            else -> Icons.Default.Task
                        },
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = when (task.status) {
                            "completed" -> BrandColors.PrimaryDeep
                            "running" -> BrandColors.IconBlue
                            "failed" -> Color(0xFFDC2626)
                            else -> BrandColors.TextSecondary
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 任务信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.taskName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = BrandColors.TextDark
                )
                Text(
                    text = "${task.appName} • ${task.taskType}",
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandColors.TextSecondary
                )
            }

            // 状态标签
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = when (task.status) {
                    "completed" -> BrandColors.PrimaryBg
                    "running" -> BrandColors.IconBlueBg
                    "failed" -> Color(0xFFFEF2F2)
                    else -> CardColors.TaskItem
                }
            ) {
                Text(
                    text = when (task.status) {
                        "completed" -> "已完成"
                        "running" -> "执行中"
                        "failed" -> "失败"
                        else -> "待执行"
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = when (task.status) {
                        "completed" -> BrandColors.PrimaryDeep
                        "running" -> BrandColors.IconBlue
                        "failed" -> Color(0xFFDC2626)
                        else -> BrandColors.TextSecondary
                    }
                )
            }
        }
    }
}

private fun simulateBatchExecution(
    taskCount: Int,
    onProgress: (Float) -> Unit,
    onComplete: () -> Unit
) {
    Thread {
        val steps = taskCount * 2
        for (i in 1..steps) {
            Thread.sleep(500)
            onProgress(i.toFloat() / steps)
        }
        onComplete()
    }.start()
}
