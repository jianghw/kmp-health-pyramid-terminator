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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.terminator.desktop.ui.theme.BrandColors
import com.terminator.desktop.ui.theme.CardColors
import com.terminator.desktop.ui.theme.HeaderColors

data class NotificationItem(
    val id: Long,
    val title: String,
    val body: String,
    val type: NotificationItemType,
    val isRead: Boolean,
    val time: String
)

enum class NotificationItemType(val icon: String, val color: Color) {
    WARNING("️", Color(0xFFF57C00)),
    TASK("✅", Color(0xFF388E3C)),
    RISK("🔴", Color(0xFFD32F2F)),
    REPORT("📊", Color(0xFF1976D2)),
    SYSTEM("ℹ️", Color(0xFF757575))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit
) {
    var unreadCount by remember { mutableIntStateOf(3) }

    val sampleNotifications = remember {
        listOf(
            NotificationItem(
                id = 1,
                title = "消费预警通知",
                body = "您在微信健康的消费已超过每日限额 ¥200.00，请注意控制消费。",
                type = NotificationItemType.WARNING,
                isRead = false,
                time = "5 分钟前"
            ),
            NotificationItem(
                id = 2,
                title = "风险事件提醒",
                body = "检测到养生课堂中存在营销诱导内容，已自动拦截。",
                type = NotificationItemType.RISK,
                isRead = false,
                time = "30 分钟前"
            ),
            NotificationItem(
                id = 3,
                title = "任务完成通知",
                body = "恭喜！您已完成今日微信健康打卡任务，获得 10 积分。",
                type = NotificationItemType.TASK,
                isRead = false,
                time = "1 小时前"
            ),
            NotificationItem(
                id = 4,
                title = "每日报告生成",
                body = "您的 2026-06-25 日报已生成，点击查看详细数据。",
                type = NotificationItemType.REPORT,
                isRead = true,
                time = "2 小时前"
            ),
            NotificationItem(
                id = 5,
                title = "系统更新通知",
                body = "薅羊毛终结者 v2.0 已发布，新增消费预警和报告功能。",
                type = NotificationItemType.SYSTEM,
                isRead = true,
                time = "昨天"
            ),
            NotificationItem(
                id = 6,
                title = "任务提醒",
                body = "今日还有 2 个任务待执行，记得及时完成。",
                type = NotificationItemType.TASK,
                isRead = true,
                time = "昨天"
            )
        )
    }

    Scaffold(
        containerColor = BrandColors.SurfaceBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                NotificationHeader(onBack = onNavigateBack, unreadCount = unreadCount)
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (unreadCount > 0) "未读消息 ($unreadCount)" else "全部通知",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandColors.TextDark
                    )
                    if (unreadCount > 0) {
                        TextButton(onClick = { unreadCount = 0 }) {
                            Text(
                                "全部已读",
                                color = BrandColors.PrimaryDeep,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }

            items(sampleNotifications) { notification ->
                NotificationListItem(
                    notification = notification,
                    onClick = { },
                    onDismiss = { }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun NotificationHeader(onBack: () -> Unit, unreadCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(HeaderColors.Notification)
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
                    text = "通知中心",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (unreadCount > 0) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFDC2626)
                    ) {
                        Text(
                            text = "$unreadCount",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationListItem(
    notification: NotificationItem,
    onClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else CardColors.TaskItem
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = notification.type.icon,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        color = BrandColors.TextDark
                    )
                    if (!notification.isRead) {
                        Surface(
                            modifier = Modifier.size(10.dp),
                            shape = CircleShape,
                            color = notification.type.color
                        ) {}
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrandColors.TextSecondary,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notification.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandColors.TextSecondary.copy(alpha = 0.7f)
                )
            }
        }
    }
}
