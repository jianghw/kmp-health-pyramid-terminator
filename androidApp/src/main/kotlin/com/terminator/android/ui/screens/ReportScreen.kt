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

enum class ReportPeriod(val label: String) {
    DAILY("日报"),
    WEEKLY("周报"),
    MONTHLY("月报")
}

data class ReportSummary(
    val date: String,
    val period: ReportPeriod,
    val totalConsumption: Double,
    val totalWarnings: Int,
    val totalTasks: Int,
    val completedTasks: Int,
    val riskEvents: Int,
    val taskCompletionRate: Float,
    val appBreakdown: List<AppConsumptionData>
)

data class AppConsumptionData(
    val appName: String,
    val amount: Double,
    val percentage: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onBack: () -> Unit
) {
    var selectedPeriod by remember { mutableStateOf(ReportPeriod.DAILY) }

    val sampleReports = remember(selectedPeriod) {
        when (selectedPeriod) {
            ReportPeriod.DAILY -> listOf(
                ReportSummary(
                    date = "2026-06-25",
                    period = ReportPeriod.DAILY,
                    totalConsumption = 280.0,
                    totalWarnings = 3,
                    totalTasks = 8,
                    completedTasks = 6,
                    riskEvents = 2,
                    taskCompletionRate = 0.75f,
                    appBreakdown = listOf(
                        AppConsumptionData("微信健康", 120.0, 0.43f),
                        AppConsumptionData("养生课堂", 80.0, 0.29f),
                        AppConsumptionData("健康积分", 50.0, 0.18f),
                        AppConsumptionData("运动健康", 30.0, 0.1f)
                    )
                )
            )
            ReportPeriod.WEEKLY -> listOf(
                ReportSummary(
                    date = "2026-06-19 ~ 2026-06-25",
                    period = ReportPeriod.WEEKLY,
                    totalConsumption = 1560.0,
                    totalWarnings = 12,
                    totalTasks = 45,
                    completedTasks = 38,
                    riskEvents = 8,
                    taskCompletionRate = 0.84f,
                    appBreakdown = listOf(
                        AppConsumptionData("微信健康", 560.0, 0.36f),
                        AppConsumptionData("养生课堂", 420.0, 0.27f),
                        AppConsumptionData("健康积分", 340.0, 0.22f),
                        AppConsumptionData("运动健康", 240.0, 0.15f)
                    )
                )
            )
            ReportPeriod.MONTHLY -> listOf(
                ReportSummary(
                    date = "2026-06",
                    period = ReportPeriod.MONTHLY,
                    totalConsumption = 6800.0,
                    totalWarnings = 45,
                    totalTasks = 180,
                    completedTasks = 156,
                    riskEvents = 23,
                    taskCompletionRate = 0.87f,
                    appBreakdown = listOf(
                        AppConsumptionData("微信健康", 2400.0, 0.35f),
                        AppConsumptionData("养生课堂", 1800.0, 0.26f),
                        AppConsumptionData("健康积分", 1500.0, 0.22f),
                        AppConsumptionData("运动健康", 1100.0, 0.16f)
                    )
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("数据报告") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Share, contentDescription = "分享")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReportPeriod.values().forEach { period ->
                        FilterChip(
                            selected = selectedPeriod == period,
                            onClick = { selectedPeriod = period },
                            label = { Text(period.label) }
                        )
                    }
                }
            }

            items(sampleReports) { report ->
                ReportCard(report)
            }
        }
    }
}

@Composable
private fun ReportCard(report: ReportSummary) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = report.date,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "保护概览",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ReportStatItem("🛡️", "拦截风险", "${report.riskEvents} 次")
                    ReportStatItem("⚠️", "消费预警", "${report.totalWarnings} 次")
                    ReportStatItem("✅", "完成任务", "${report.completedTasks}/${report.totalTasks}")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "消费统计",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "¥%.2f".format(report.totalConsumption),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                report.appBreakdown.forEach { app ->
                    AppConsumptionRow(app)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "任务完成率",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { report.taskCompletionRate },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = when {
                        report.taskCompletionRate >= 0.8f -> Color(0xFF388E3C)
                        report.taskCompletionRate >= 0.6f -> Color(0xFFFFA000)
                        else -> Color(0xFFD32F2F)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "%.0f%% 完成 (%d/%d)".format(
                        report.taskCompletionRate * 100,
                        report.completedTasks,
                        report.totalTasks
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider()
    }
}

@Composable
private fun ReportStatItem(icon: String, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, style = MaterialTheme.typography.headlineMedium)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun AppConsumptionRow(app: AppConsumptionData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = app.appName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        LinearProgressIndicator(
            progress = { app.percentage },
            modifier = Modifier
                .width(100.dp)
                .height(8.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "¥%.2f".format(app.amount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
