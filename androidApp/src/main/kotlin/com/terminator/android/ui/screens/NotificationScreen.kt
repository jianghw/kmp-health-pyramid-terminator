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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class NotificationItem(
    val id: Long,
    val title: String,
    val body: String,
    val type: NotificationItemType,
    val isRead: Boolean,
    val time: String
)

enum class NotificationItemType(val icon: String, val color: Color) {
    WARNING("⚠️", Color(0xFFF57C00)),
    TASK("✅", Color(0xFF388E3C)),
    RISK("🔴", Color(0xFFD32F2F)),
    REPORT("📊", Color(0xFF1976D2)),
    SYSTEM("ℹ️", Color(0xFF757575))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBack: () -> Unit
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
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("通知中心")
                        if (unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge {
                                Text("$unreadCount")
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(onClick = { unreadCount = 0 }) {
                        Text("全部已读")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (sampleNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.NotificationsNone,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "暂无通知",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(sampleNotifications) { notification ->
                    NotificationListItem(
                        notification = notification,
                        onClick = { },
                        onDismiss = { }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationListItem(
    notification: NotificationItem,
    onClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        Surface(
            onClick = onClick,
            color = if (notification.isRead)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant
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

                Spacer(modifier = Modifier.width(12.dp))

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
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold
                        )
                        if (!notification.isRead) {
                            Badge(
                                containerColor = notification.type.color,
                                modifier = Modifier.size(8.dp)
                            ) {}
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = notification.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = notification.time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
