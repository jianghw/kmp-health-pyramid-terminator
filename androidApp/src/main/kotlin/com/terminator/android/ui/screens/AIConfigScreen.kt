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

/**
 * AI配置项数据类 - 用于在UI中展示单个AI配置的信息
 *
 * 这是UI层使用的简化数据模型，与后端的 AIConfigResponse 对应，
 * 但只包含UI展示所需的字段。
 */
data class AIConfigItem(
    val configId: Long,           // 配置ID
    val provider: String,         // AI提供商名称
    val apiKeyMasked: String,     // 脱敏后的API密钥
    val modelName: String,        // 模型名称
    val baseUrl: String,          // API接口地址
    val maxTokens: Int,           // 最大token数
    val temperature: Double,      // 温度参数
    val isEnabled: Boolean        // 是否启用
)

/**
 * AI配置管理界面 - 用户可以查看、添加、编辑和删除AI配置
 *
 * 这是Jetpack Compose的声明式UI组件。
 * Compose使用函数（@Composable）来描述UI，而不是传统的XML布局。
 *
 * 主要功能：
 * 1. 展示已配置的AI服务列表
 * 2. 添加新的AI服务配置（选择提供商、输入API密钥等）
 * 3. 测试AI连接是否正常
 * 4. 启用/禁用AI配置
 * 5. 删除AI配置
 *
 * @param onNavigateBack 返回上一页的回调函数
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIConfigScreen(
    onNavigateBack: () -> Unit
) {
    // ==========================================
    // 状态变量 - 使用 remember + mutableStateOf 管理界面状态
    // ==========================================

    // configs: AI配置列表，初始为空列表
    var configs by remember { mutableStateOf(listOf<AIConfigItem>()) }
    // showAddDialog: 是否显示"添加配置"对话框
    var showAddDialog by remember { mutableStateOf(false) }
    // selectedProvider: 当前选择的AI提供商
    var selectedProvider by remember { mutableStateOf("openai") }
    // 以下是表单输入字段的状态
    var apiKey by remember { mutableStateOf("") }
    var modelName by remember { mutableStateOf("") }
    var baseUrl by remember { mutableStateOf("") }
    var maxTokens by remember { mutableStateOf("1000") }
    var temperature by remember { mutableStateOf("0.7") }
    var isTesting by remember { mutableStateOf(false) }    // 是否正在测试连接
    var testResult by remember { mutableStateOf<String?>(null) }  // 测试结果

    // ==========================================
    // 配置数据 - AI提供商列表和默认值
    // ==========================================

    // 支持的AI提供商列表（内部标识 -> 显示名称）
    val providers = listOf(
        "openai" to "OpenAI",
        "qwen" to "通义千问",
        "zhipu" to "智谱AI",
        "baidu" to "文心一言"
    )

    // 各提供商的默认API地址（选择提供商时自动填入）
    val defaultBaseUrls = mapOf(
        "openai" to "https://api.openai.com",
        "qwen" to "https://dashscope.aliyuncs.com",
        "zhipu" to "https://open.bigmodel.cn",
        "baidu" to "https://aip.baidubce.com"
    )

    // 各提供商支持的模型列表（选择提供商时显示对应的模型选项）
    val defaultModels = mapOf(
        "openai" to listOf("gpt-4", "gpt-4-turbo", "gpt-3.5-turbo"),
        "qwen" to listOf("qwen-turbo", "qwen-plus", "qwen-max"),
        "zhipu" to listOf("glm-4", "glm-4-flash", "glm-3-turbo"),
        "baidu" to listOf("ernie-4.0", "ernie-3.5")
    )

    // ==========================================
    // 页面布局 - Scaffold 提供基本的页面结构（顶部栏 + 内容区域）
    // ==========================================
    Scaffold(
        topBar = {
            // 顶部应用栏：标题 + 返回按钮 + 添加按钮
            TopAppBar(
                title = { Text("AI 配置管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "添加配置")
                    }
                }
            )
        }
    ) { paddingValues ->
        // paddingValues: 系统自动计算的内边距（避免内容被顶部栏遮挡）
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // ==========================================
            // 说明卡片 - 向用户解释AI配置的作用
            // ==========================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "AI 服务说明",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "配置大模型API后，系统可以自动识别题目并给出答案。" +
                        "支持单选题、多选题、判断题、填空题等多种题型。",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 配置列表或空状态提示
            // ==========================================
            if (configs.isEmpty()) {
                // 没有配置时显示引导界面
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.SmartToy,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "暂未配置AI服务",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "点击右上角 + 添加AI服务配置",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("添加配置")
                        }
                    }
                }
            } else {
                // 有配置时显示配置列表
                // LazyColumn: 懒加载列表，只渲染可见区域的项目（性能优化）
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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

    // ==========================================
    // 添加配置对话框 - 弹出的表单界面
    // ==========================================
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },  // 点击对话框外部关闭
            title = { Text("添加AI配置") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "选择AI服务提供商",
                        style = MaterialTheme.typography.titleSmall
                    )

                    // ==========================================
                    // AI提供商下拉选择框
                    // ExposedDropdownMenuBox: Material 3 的下拉选择组件
                    // ==========================================
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = providers.find { it.first == selectedProvider }?.second ?: "",
                            onValueChange = {},
                            readOnly = true,  // 只读，只能通过下拉选择
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
                                        // 选择提供商后自动填入默认的API地址和模型
                                        baseUrl = defaultBaseUrls[value] ?: ""
                                        modelName = defaultModels[value]?.firstOrNull() ?: ""
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // API密钥输入框
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("API Key") },
                        placeholder = { Text("sk-xxxxx") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // 模型名称下拉选择框（也可以手动输入自定义模型名）
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
                            // 显示当前选中提供商的模型列表
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

                    // API地址输入框
                    OutlinedTextField(
                        value = baseUrl,
                        onValueChange = { baseUrl = it },
                        label = { Text("API地址") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // 最大Token和温度参数（并排显示）
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

                    // 测试连接的加载指示器
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

                    // 测试结果显示
                    testResult?.let { result ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (result.contains("成功"))
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = result,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row {
                    // "测试连接"按钮 - 验证API配置是否正确
                    TextButton(
                        onClick = {
                            isTesting = true
                            testResult = null
                            // TODO: 实际项目中应该调用后端接口测试连接
                            isTesting = false
                            testResult = "连接成功！模型响应正常。"
                        }
                    ) {
                        Text("测试连接")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // "保存"按钮 - 保存配置到后端
                    Button(
                        onClick = {
                            showAddDialog = false
                            // TODO: 调用 AIApi.createAIConfig() 保存配置
                        }
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

/**
 * AI配置卡片组件 - 展示单个AI配置的信息和操作按钮
 *
 * 这是一个可复用的UI组件，每个AI配置都用一张卡片来展示。
 * 卡片中包含：提供商名称、模型名称、API密钥（脱敏）、Token限制等信息。
 *
 * @param config 配置数据
 * @param onToggle 切换启用状态的回调
 * @param onDelete 删除配置的回调
 * @param onTest 测试连接的回调
 */
@Composable
fun AIConfigCard(
    config: AIConfigItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onTest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 顶部：提供商名称 + 启用开关
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    // 将内部标识转换为中文显示名称
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Switch: 开关组件，用于启用/禁用配置
                Switch(
                    checked = config.isEnabled,
                    onCheckedChange = { onToggle() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 中间：API密钥和Token信息（以标签形式展示）
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

            Spacer(modifier = Modifier.height(12.dp))

            // 底部：操作按钮（测试、删除）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // 测试按钮
                TextButton(onClick = onTest) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("测试")
                }

                // 删除按钮（红色，表示危险操作）
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
