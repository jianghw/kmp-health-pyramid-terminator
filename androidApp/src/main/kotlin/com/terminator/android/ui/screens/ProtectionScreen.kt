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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtectionScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("风险保护") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "风险汇总",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            RiskStatItem("🔴", "高风险", "2")
                            RiskStatItem("🟡", "中风险", "5")
                            RiskStatItem("🟢", "低风险", "8")
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "最近风险事件",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(getSampleRiskEvents()) { event ->
                RiskEventItem(event)
            }
        }
    }
}

@Composable
private fun RiskStatItem(icon: String, label: String, count: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, style = MaterialTheme.typography.headlineLarge)
        Text(text = count, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun RiskEventItem(event: SampleRiskEvent) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = event.icon, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
                Text(text = event.time, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = event.severity,
                style = MaterialTheme.typography.titleMedium,
                color = when (event.severity) {
                    "高" -> MaterialTheme.colorScheme.error
                    "中" -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }
            )
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
