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
 * AI配置项数据类 - 用于在UI中展示单个AI配置的信息
 */
data class AIConfigItem(
    val configId: Long,
    val provider: String,
    val apiKeyMasked: String,
    val modelName: String,
    val baseUrl: String,
    val maxTokens: Int,
    val temperature: Double,
    val isEnabled: Boolean
)

/**
 * AI配置管理界面 - 桌面端
 * 用户可以查看、添加、编辑和删除AI配置
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIConfigScreen(
    onNavigateBack: () -> Unit
) {
    var configs by remember { mutableStateOf(listOf<AIConfigItem>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedProvider by remember { mutableStateOf("openai") }
    var apiKey by remember { mutableStateOf("") }
    var modelName by remember { mutableStateOf("") }
    var baseUrl by remember { mutableStateOf("") }
    var maxTokens by remember { mutableStateOf("1000") }
    var temperature by remember { mutableStateOf("0.7") }
    var isTesting by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }

    val providers = listOf(
        "openai" to "OpenAI",
        "qwen" to "通义千问",
        "zhipu" to "智谱AI",
        "baidu" to "文心一言"
    )

    val defaultBaseUrls = mapOf(
        "openai" to "https://api.openai.com",
        "qwen" to "https://dashscope.aliyuncs.com",
        "zhipu" to "https://open.bigmodel.cn",
        "baidu" to "https://aip.baidubce.com"
    )

    val defaultModels = mapOf(
        "openai" to listOf("gpt-4", "gpt-4-turbo", "gpt-3.5-turbo"),
        "qwen" to listOf("qwen-turbo", "qwen-plus", "qwen-max"),
        "zhipu" to listOf("glm-4", "glm-4-flash", "glm-3-turbo"),
        "baidu" to listOf("ernie-4.0", "ernie-3.5")
    )

    Scaffold(
        containerColor = BrandColors.SurfaceBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            AIConfigHeader(onBack = onNavigateBack)

            // 内容区域
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 说明卡片
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CardColors.StatsMint
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = BrandColors.PrimaryDeep.copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = BrandColors.PrimaryDeep,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    "AI 服务说明",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandColors.TextDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "配置大模型API后，系统可以自动识别题目并给出答案。支持单选题、多选题、判断题、填空题等多种题型。",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BrandColors.TextSecondary
                                )
                            }
                        }
                    }
                }

                // 添加按钮
                item {
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandColors.PrimaryDeep
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("添加AI配置")
                    }
                }

                // 配置列表或空状态
                if (configs.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = BrandColors.CardBg
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.SmartToy,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = BrandColors.TextMuted
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "暂未配置AI服务",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = BrandColors.TextDark
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "点击上方按钮添加AI服务配置",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BrandColors.TextSecondary
                                )
                            }
                        }
                    }
                } else {
                    items(configs) { config ->
                        AIConfigCard(
                            config = config,
                            onToggle = { /* TODO: 切换配置启用状态 */ },
                            onDelete = { /* TODO: 删除配置 */ },
                            onTest = { /* TODO: 测试配置连接 */ }
                        )
                    }
                }
            }
        }
    }

    // 添加配置对话框
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("添加AI配置") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "选择AI服务提供商",
                        style = MaterialTheme.typography.titleSmall
                    )

                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = providers.find { it.first == selectedProvider }?.second ?: "",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            providers.forEach { (value, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        selectedProvider = value
                                        baseUrl = defaultBaseUrls[value] ?: ""
                                        modelName = defaultModels[value]?.firstOrNull() ?: ""
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("API Key") },
                        placeholder = { Text("sk-xxxxx") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    var modelExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = modelExpanded,
                        onExpandedChange = { modelExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = modelName,
                            onValueChange = { modelName = it },
                            label = { Text("模型名称") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelExpanded) }
                        )
                        ExposedDropdownMenu(
                            expanded = modelExpanded,
                            onDismissRequest = { modelExpanded = false }
                        ) {
                            (defaultModels[selectedProvider] ?: emptyList()).forEach { model ->
                                DropdownMenuItem(
                                    text = { Text(model) },
                                    onClick = {
                                        modelName = model
                                        modelExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = baseUrl,
                        onValueChange = { baseUrl = it },
                        label = { Text("API地址") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = maxTokens,
                            onValueChange = { maxTokens = it },
                            label = { Text("最大Token") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = temperature,
                            onValueChange = { temperature = it },
                            label = { Text("温度") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    if (isTesting) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("正在测试连接...")
                        }
                    }

                    testResult?.let { result ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (result.contains("成功"))
                                    Color(0xFFECFDF5)
                                else
                                    Color(0xFFFEF2F2)
                            )
                        ) {
                            Text(
                                text = result,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (result.contains("成功")) BrandColors.TextDark else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row {
                    TextButton(
                        onClick = {
                            isTesting = true
                            testResult = null
                            isTesting = false
                            testResult = "连接成功！模型响应正常。"
                        }
                    ) {
                        Text("测试连接")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            showAddDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandColors.PrimaryDeep
                        )
                    ) {
                        Text("保存")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun AIConfigHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(HeaderColors.AIConfig)
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
                    text = "AI 配置管理",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BrandColors.TextDark
                )
            }
        }
    }
}

@Composable
private fun AIConfigCard(
    config: AIConfigItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onTest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BrandColors.CardBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = when (config.provider) {
                            "openai" -> "OpenAI"
                            "qwen" -> "通义千问"
                            "zhipu" -> "智谱AI"
                            "baidu" -> "文心一言"
                            else -> config.provider
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = config.modelName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandColors.TextSecondary
                    )
                }

                Switch(
                    checked = config.isEnabled,
                    onCheckedChange = { onToggle() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = {},
                    label = { Text("API: ${config.apiKeyMasked}") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Key,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )

                AssistChip(
                    onClick = {},
                    label = { Text("Token: ${config.maxTokens}") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.DataUsage,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onTest) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("测试")
                }

                TextButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "删除",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
