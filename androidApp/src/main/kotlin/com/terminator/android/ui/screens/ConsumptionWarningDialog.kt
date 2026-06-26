package com.terminator.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

enum class WarningDialogLevel { LOW, MEDIUM, HIGH, CRITICAL }

data class WarningDialogData(
    val title: String,
    val message: String,
    val currentAmount: Double,
    val thresholdAmount: Double,
    val level: WarningDialogLevel
)

@Composable
fun ConsumptionWarningDialog(
    warningData: WarningDialogData?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (warningData != null) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = when (warningData.level) {
                            WarningDialogLevel.CRITICAL -> Icons.Default.Error
                            WarningDialogLevel.HIGH -> Icons.Default.Warning
                            WarningDialogLevel.MEDIUM -> Icons.Default.Info
                            WarningDialogLevel.LOW -> Icons.Default.Notifications
                        },
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = when (warningData.level) {
                            WarningDialogLevel.CRITICAL -> Color(0xFFD32F2F)
                            WarningDialogLevel.HIGH -> Color(0xFFF57C00)
                            WarningDialogLevel.MEDIUM -> Color(0xFFFFA000)
                            WarningDialogLevel.LOW -> Color(0xFF388E3C)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = warningData.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = warningData.message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "当前金额",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "¥%.2f".format(warningData.currentAmount),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "预警阈值",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "¥%.2f".format(warningData.thresholdAmount),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("忽略")
                        }

                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (warningData.level) {
                                    WarningDialogLevel.CRITICAL -> Color(0xFFD32F2F)
                                    WarningDialogLevel.HIGH -> Color(0xFFF57C00)
                                    WarningDialogLevel.MEDIUM -> Color(0xFFFFA000)
                                    WarningDialogLevel.LOW -> MaterialTheme.colorScheme.primary
                                }
                            )
                        ) {
                            Text("确认取消消费")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarningListScreen(
    onBack: () -> Unit
) {
    val sampleWarnings = listOf(
        WarningDialogData(
            title = "每日消费限额预警",
            message = "今日累计消费 ¥280.00，已超过每日限额 ¥200.00，请注意控制消费。",
            currentAmount = 280.0,
            thresholdAmount = 200.0,
            level = WarningDialogLevel.HIGH
        ),
        WarningDialogData(
            title = "单笔消费限额预警",
            message = "本次消费 ¥150.00，超过单笔限额 ¥100.00，建议取消或减少消费。",
            currentAmount = 150.0,
            thresholdAmount = 100.0,
            level = WarningDialogLevel.MEDIUM
        ),
        WarningDialogData(
            title = "消费频率预警",
            message = "近期消费频率过高，已达 6 次，超过限制 5 次/小时。",
            currentAmount = 6.0,
            thresholdAmount = 5.0,
            level = WarningDialogLevel.LOW
        )
    )

    var selectedWarning by remember { mutableStateOf<WarningDialogData?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("消费预警") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "最近预警",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            sampleWarnings.forEach { warning ->
                WarningListItem(
                    warning = warning,
                    onClick = { selectedWarning = warning }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    ConsumptionWarningDialog(
        warningData = selectedWarning,
        onDismiss = { selectedWarning = null },
        onConfirm = { selectedWarning = null }
    )
}

@Composable
private fun WarningListItem(
    warning: WarningDialogData,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (warning.level) {
                    WarningDialogLevel.CRITICAL -> Icons.Default.Error
                    WarningDialogLevel.HIGH -> Icons.Default.Warning
                    WarningDialogLevel.MEDIUM -> Icons.Default.Info
                    WarningDialogLevel.LOW -> Icons.Default.Notifications
                },
                contentDescription = null,
                tint = when (warning.level) {
                    WarningDialogLevel.CRITICAL -> Color(0xFFD32F2F)
                    WarningDialogLevel.HIGH -> Color(0xFFF57C00)
                    WarningDialogLevel.MEDIUM -> Color(0xFFFFA000)
                    WarningDialogLevel.LOW -> Color(0xFF388E3C)
                },
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = warning.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "当前 ¥%.2f / 阈值 ¥%.2f".format(warning.currentAmount, warning.thresholdAmount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = when (warning.level) {
                    WarningDialogLevel.CRITICAL -> "严重"
                    WarningDialogLevel.HIGH -> "高"
                    WarningDialogLevel.MEDIUM -> "中"
                    WarningDialogLevel.LOW -> "低"
                },
                style = MaterialTheme.typography.labelLarge,
                color = when (warning.level) {
                    WarningDialogLevel.CRITICAL -> Color(0xFFD32F2F)
                    WarningDialogLevel.HIGH -> Color(0xFFF57C00)
                    WarningDialogLevel.MEDIUM -> Color(0xFFFFA000)
                    WarningDialogLevel.LOW -> Color(0xFF388E3C)
                }
            )
        }
    }
}
