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

data class ReportItem(
    val id: Long,
    val title: String,
    val date: String,
    val type: String,
    val summary: String,
    val amount: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onBack: () -> Unit
) {
    val sampleReports = remember {
        listOf(
            ReportItem(
                id = 1,
                title = "每日消费报告",
                date = "2026-06-25",
                type = "日报",
                summary = "今日共完成5个任务,拦截2次风险,消费预警3次",
                amount = 280.0
            ),
            ReportItem(
                id = 2,
                title = "每周消费报告",
                date = "2026-06-19 ~ 2026-06-25",
                type = "周报",
                summary = "本周共完成38个任务,拦截8次风险,消费预警12次",
                amount = 1560.0
            ),
            ReportItem(
                id = 3,
                title = "每月消费报告",
                date = "2026-06",
                type = "月报",
                summary = "本月共完成156个任务,拦截23次风险,消费预警45次",
                amount = 6800.0
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
                ReportHeader(onBack = onBack)
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
                        text = "数据报告",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandColors.TextDark
                    )
                }
            }

            items(sampleReports) { report ->
                ReportCard(report = report)
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ReportHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(HeaderColors.Report)
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
                    text = "数据报告",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ReportCard(report: ReportItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BrandColors.CardBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = report.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandColors.TextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = report.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandColors.TextSecondary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BrandColors.PrimaryBg
                ) {
                    Text(
                        text = report.type,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = BrandColors.PrimaryDeep
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = report.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = BrandColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "总消费",
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandColors.TextSecondary
                )
                Text(
                    text = "¥%.2f".format(report.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandColors.PrimaryDeep
                )
            }
        }
    }
}
