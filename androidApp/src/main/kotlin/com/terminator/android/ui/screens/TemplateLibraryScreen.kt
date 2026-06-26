package com.terminator.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
fun TemplateLibraryScreen(
    onBack: () -> Unit,
    onImportTemplate: () -> Unit,
    onExportTemplate: () -> Unit,
    onUseTemplate: (Long) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showImportDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    val templates = remember { getSampleTemplateLibrary() }
    val categories = listOf("全部", "签到", "课程", "问卷", "阅读", "兑换")

    val filteredTemplates = templates.filter { template ->
        (selectedCategory == null || selectedCategory == "全部" || template.category == selectedCategory) &&
            (searchQuery.isBlank() || template.name.contains(searchQuery, ignoreCase = true) ||
                template.description.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("模板库") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showImportDialog = true }) {
                        Icon(Icons.Default.FileUpload, contentDescription = "导入")
                    }
                    IconButton(onClick = { showExportDialog = true }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "导出")
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("搜索模板...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "清除")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category || (selectedCategory == null && category == "全部"),
                            onClick = {
                                selectedCategory = if (category == "全部") null else category
                            },
                            label = { Text(category) }
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "共 ${filteredTemplates.size} 个模板",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(filteredTemplates) { template ->
                TemplateLibraryCard(
                    template = template,
                    onUse = { onUseTemplate(template.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        if (showImportDialog) {
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                title = { Text("导入模板") },
                text = {
                    Column {
                        Text("请选择导入方式:")
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { showImportDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.FileOpen, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("从文件导入")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { showImportDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("从剪贴板导入")
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showImportDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }

        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = { Text("导出模板") },
                text = {
                    Column {
                        Text("选择导出范围:")
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { showExportDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.SelectAll, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("导出全部模板")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { showExportDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Checklist, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("选择性导出")
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showExportDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
private fun TemplateLibraryCard(
    template: SampleTemplateLibrary,
    onUse: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = when (template.category) {
                            "签到" -> Icons.Default.CalendarToday
                            "课程" -> Icons.Default.School
                            "问卷" -> Icons.Default.Quiz
                            "阅读" -> Icons.Default.MenuBook
                            "兑换" -> Icons.Default.CardGiftcard
                            else -> Icons.Default.Description
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = template.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = template.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (template.isPreset) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "预置",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = template.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${template.estimatedMinutes}分钟",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "${template.rewardPoints}积分",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Loop,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${template.steps}步骤",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                template.tags.take(3).forEach { tag ->
                    SuggestionChip(
                        onClick = { },
                        label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onUse,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("使用此模板")
            }
        }
    }
}

private data class SampleTemplateLibrary(
    val id: Long,
    val name: String,
    val category: String,
    val description: String,
    val estimatedMinutes: Int,
    val rewardPoints: Int,
    val steps: Int,
    val tags: List<String>,
    val isPreset: Boolean = true
)

private fun getSampleTemplateLibrary() = listOf(
    SampleTemplateLibrary(1, "每日签到", "签到", "每日签到领取积分奖励", 2, 10, 4, listOf("签到", "每日", "积分")),
    SampleTemplateLibrary(2, "课程学习", "课程", "完成健康课程学习任务", 10, 30, 6, listOf("课程", "学习", "视频")),
    SampleTemplateLibrary(3, "健康问卷", "问卷", "完成健康状况问卷调查", 5, 20, 6, listOf("问卷", "调查", "健康")),
    SampleTemplateLibrary(4, "阅读打卡", "阅读", "完成健康文章阅读任务", 3, 15, 5, listOf("阅读", "文章", "打卡")),
    SampleTemplateLibrary(5, "积分兑换", "兑换", "使用积分兑换健康礼品", 3, 0, 6, listOf("兑换", "积分", "礼品")),
    SampleTemplateLibrary(6, "早起打卡", "签到", "早起打卡获取额外奖励", 1, 20, 3, listOf("早起", "打卡", "奖励")),
    SampleTemplateLibrary(7, "运动记录", "签到", "记录每日运动步数", 2, 15, 4, listOf("运动", "步数", "健康")),
    SampleTemplateLibrary(8, "养生知识", "课程", "学习养生健康知识", 8, 25, 5, listOf("养生", "知识", "健康"))
)
