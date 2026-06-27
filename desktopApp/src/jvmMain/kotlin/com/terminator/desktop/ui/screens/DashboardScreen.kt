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
import androidx.compose.ui.unit.sp
import com.terminator.desktop.ui.theme.BrandColors
import com.terminator.desktop.ui.theme.CardColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToProtection: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToWarnings: () -> Unit
) {
    Scaffold(
        containerColor = BrandColors.SurfaceBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 顶部 Header
            item {
                DashboardHeader()
            }

            // 今日保护数据卡片
            item {
                TodayStatsCard(
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            // 快捷操作区域
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "快捷操作",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandColors.TextDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.PlayArrow,
                            label = "一键执行",
                            subtitle = "自动完成任务",
                            color = BrandColors.PrimaryDeep,
                            bgColor = BrandColors.PrimaryBg,
                            onClick = onNavigateToTasks
                        )
                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.BarChart,
                            label = "查看报告",
                            subtitle = "数据分析",
                            color = BrandColors.IconBlue,
                            bgColor = BrandColors.IconBlueBg,
                            onClick = onNavigateToReports
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Warning,
                            label = "消费预警",
                            subtitle = "风险提醒",
                            color = BrandColors.IconOrange,
                            bgColor = BrandColors.IconOrangeBg,
                            onClick = onNavigateToWarnings
                        )
                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Notifications,
                            label = "通知中心",
                            subtitle = "消息推送",
                            color = BrandColors.IconPurple,
                            bgColor = BrandColors.IconPurpleBg,
                            onClick = onNavigateToNotifications
                        )
                    }
                }
            }

            // 应用任务列表
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "应用任务列表",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandColors.TextDark
                        )
                        TextButton(onClick = onNavigateToTasks) {
                            Text(
                                "查看全部",
                                style = MaterialTheme.typography.labelMedium,
                                color = BrandColors.PrimaryDeep
                            )
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = BrandColors.PrimaryDeep
                            )
                        }
                    }
                }
            }

            items(getSampleTasks()) { task ->
                ModernTaskItem(
                    task = task,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }

            // 底部间距
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DashboardHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(BrandColors.PrimaryDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "薅羊毛终结者",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "智能任务管家，守护每一天",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "通知",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "今日保护状态",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun TodayStatsCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = BrandColors.PrimaryDeep.copy(alpha = 0.15f)
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
            StatCircleItem(
                icon = Icons.Default.CheckCircle,
                value = "5",
                label = "自动完成",
                iconColor = BrandColors.Primary,
                bgColor = BrandColors.PrimaryBg
            )
            StatCircleItem(
                icon = Icons.Default.Schedule,
                value = "2h",
                label = "节省时间",
                iconColor = BrandColors.IconBlue,
                bgColor = BrandColors.IconBlueBg
            )
            StatCircleItem(
                icon = Icons.Default.Security,
                value = "3",
                label = "拦截风险",
                iconColor = BrandColors.IconRed,
                bgColor = BrandColors.IconRedBg
            )
        }
    }
}

@Composable
private fun StatCircleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    iconColor: Color,
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
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = iconColor
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = BrandColors.TextDark
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = BrandColors.TextSecondary
        )
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    subtitle: String,
    color: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BrandColors.CardBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = bgColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = color
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandColors.TextDark
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun ModernTaskItem(
    task: SampleTask,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = BrandColors.CardBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = CardColors.StatsMint
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = task.icon,
                        fontSize = 24.sp
                    )
                }
            }

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

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = when (task.status) {
                    "已完成" -> BrandColors.PrimaryBg
                    "执行中" -> BrandColors.IconBlueBg
                    else -> CardColors.TaskItem
                }
            ) {
                Text(
                    text = task.status,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = when (task.status) {
                        "已完成" -> BrandColors.Primary
                        "执行中" -> BrandColors.IconBlue
                        else -> BrandColors.TextSecondary
                    }
                )
            }
        }
    }
}

private data class SampleTask(
    val icon: String,
    val name: String,
    val description: String,
    val status: String
)

private fun getSampleTasks() = listOf(
    SampleTask("📱", "微信健康打卡", "每日签到任务", "已完成"),
    SampleTask("📱", "养生课堂", "今日课程学习", "执行中"),
    SampleTask("📱", "健康积分", "积分兑换任务", "待执行"),
    SampleTask("📱", "运动健康", "步数打卡任务", "待执行")
)
