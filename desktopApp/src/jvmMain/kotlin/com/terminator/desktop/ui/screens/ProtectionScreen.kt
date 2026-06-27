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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtectionScreen(
    onNavigateBack: () -> Unit
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
            item {
                ProtectionHeader(onBack = onNavigateBack)
            }

            item {
                RiskSummaryCard(
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "最近风险事件",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandColors.TextDark
                    )
                    TextButton(onClick = { }) {
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

            items(getSampleRiskEvents()) { event ->
                RiskEventCard(
                    event = event,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProtectionHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(HeaderColors.Protection)
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
                    text = "风险保护",
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
                            Icons.Default.Security,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "实时风险监控",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "为您拦截潜在的消费风险",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RiskSummaryCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = HeaderColors.Protection.copy(alpha = 0.12f)
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
            RiskLevelItem(
                count = "2",
                label = "高风险",
                color = Color(0xFFDC2626),
                bgColor = Color(0xFFFEF2F2),
                icon = Icons.Default.Warning
            )
            RiskLevelItem(
                count = "5",
                label = "中风险",
                color = Color(0xFFD97706),
                bgColor = Color(0xFFFFFBEB),
                icon = Icons.Default.Report
            )
            RiskLevelItem(
                count = "8",
                label = "低风险",
                color = Color(0xFF059669),
                bgColor = Color(0xFFECFDF5),
                icon = Icons.Default.Info
            )
        }
    }
}

@Composable
private fun RiskLevelItem(
    count: String,
    label: String,
    color: Color,
    bgColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = bgColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = color
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = count,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = BrandColors.TextSecondary
        )
    }
}

@Composable
private fun RiskEventCard(
    event: SampleRiskEvent,
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
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = when (event.severity) {
                    "高" -> Color(0xFFFEF2F2)
                    "中" -> Color(0xFFFFFBEB)
                    else -> Color(0xFFECFDF5)
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        when (event.severity) {
                            "高" -> Icons.Default.Warning
                            "中" -> Icons.Default.Report
                            else -> Icons.Default.Info
                        },
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = when (event.severity) {
                            "高" -> Color(0xFFDC2626)
                            "中" -> Color(0xFFD97706)
                            else -> Color(0xFF059669)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandColors.TextDark
                )
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandColors.TextSecondary
                )
                Text(
                    text = event.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandColors.TextSecondary.copy(alpha = 0.7f)
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = when (event.severity) {
                    "高" -> Color(0xFFFEF2F2)
                    "中" -> Color(0xFFFFFBEB)
                    else -> Color(0xFFECFDF5)
                }
            ) {
                Text(
                    text = event.severity,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (event.severity) {
                        "高" -> Color(0xFFDC2626)
                        "中" -> Color(0xFFD97706)
                        else -> Color(0xFF059669)
                    }
                )
            }
        }
    }
}

private data class SampleRiskEvent(
    val icon: String,
    val title: String,
    val description: String,
    val time: String,
    val severity: String
)

private fun getSampleRiskEvents() = listOf(
    SampleRiskEvent("⚠️", "营销诱导检测", "检测到课程中的诱导消费内容", "2026-06-25 08:30", "高"),
    SampleRiskEvent("⚠️", "虚假宣传识别", "发现夸大功效的虚假宣传", "2026-06-25 07:15", "中"),
    SampleRiskEvent("ℹ️", "价格异常提醒", "积分兑换商品价格偏高", "2026-06-24 18:45", "低")
)
