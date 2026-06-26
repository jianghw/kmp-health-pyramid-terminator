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
 * 题库项数据类 - 用于在UI中展示题库的基本信息
 */
data class QuestionBankItem(
    val bankId: Long,             // 题库ID
    val bankName: String,         // 题库名称
    val description: String,      // 题库描述
    val questionCount: Int        // 题目数量
)

/**
 * 题目条目数据类 - 用于在UI中展示一道题目的完整信息
 */
data class QuestionEntryItem(
    val entryId: Long,            // 题目ID
    val question: String,         // 题目内容
    val questionType: String,     // 题目类型
    val options: List<String>?,   // 选项列表（选择题时有值）
    val correctAnswer: String,    // 正确答案
    val explanation: String?,     // 答案解析
    val tags: List<String>        // 标签列表
)

/**
 * 题库管理界面 - 用户可以创建题库、添加和管理题目
 *
 * 这个界面分为两个层级：
 * 1. 题库列表层：展示所有题库，可以创建新题库
 * 2. 题目列表层：点击某个题库后，展示该题库中的所有题目
 *
 * 主要功能：
 * - 创建、编辑、删除题库
 * - 向题库中添加各类题目（单选、多选、判断、填空、简答）
 * - 查看题目的答案和解析
 * - 使用标签对题目进行分类
 *
 * @param onNavigateBack 返回上一页的回调函数
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankScreen(
    onNavigateBack: () -> Unit
) {
    // ==========================================
    // 状态变量 - 管理界面的数据和交互状态
    // ==========================================

    // banks: 题库列表
    var banks by remember { mutableStateOf(listOf<QuestionBankItem>()) }
    // selectedBank: 当前选中的题库（null表示在题库列表层，非null表示在题目列表层）
    var selectedBank by remember { mutableStateOf<QuestionBankItem?>(null) }
    // entries: 当前题库中的题目列表
    var entries by remember { mutableStateOf(listOf<QuestionEntryItem>()) }
    // 对话框显示状态
    var showAddBankDialog by remember { mutableStateOf(false) }
    var showAddEntryDialog by remember { mutableStateOf(false) }
    // 题库表单字段
    var bankName by remember { mutableStateOf("") }
    var bankDescription by remember { mutableStateOf("") }
    // 题目表单字段
    var questionText by remember { mutableStateOf("") }
    var questionType by remember { mutableStateOf("single_choice") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    // 支持的题目类型列表（内部标识 -> 中文名称）
    val questionTypes = listOf(
        "single_choice" to "单选题",
        "multiple_choice" to "多选题",
        "judgment" to "判断题",
        "fill_blank" to "填空题",
        "short_answer" to "简答题"
    )

    // ==========================================
    // 页面布局
    // ==========================================
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // 根据当前层级动态显示标题
                    Text(if (selectedBank != null) selectedBank!!.bankName else "题库管理")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedBank != null) {
                            // 在题目列表层：返回到题库列表
                            selectedBank = null
                            entries = emptyList()
                        } else {
                            // 在题库列表层：返回上一页
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (selectedBank != null) {
                            // 在题目列表层：添加题目
                            showAddEntryDialog = true
                        } else {
                            // 在题库列表层：创建题库
                            showAddBankDialog = true
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "添加")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // ==========================================
            // 题库列表层（selectedBank == null 时显示）
            // ==========================================
            if (selectedBank == null) {
                // 题库功能说明卡片
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Quiz,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "题库说明",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "题库用于存储题目和答案，AI答题时会优先从题库中查找匹配的题目。" +
                            "建议将常见题目添加到题库中，提高答题准确率。",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (banks.isEmpty()) {
                    // 没有题库时显示引导界面
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
                                Icons.Default.LibraryBooks,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "暂无题库",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "点击右上角 + 创建题库",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { showAddBankDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("创建题库")
                            }
                        }
                    }
                } else {
                    // 显示题库列表
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(banks) { bank ->
                            QuestionBankCard(
                                bank = bank,
                                onClick = {
                                    selectedBank = bank
                                    // TODO: 调用 AIApi.getQuestionEntries() 加载题目
                                },
                                onDelete = { /* TODO: 调用 AIApi.deleteQuestionBank() 删除题库 */ }
                            )
                        }
                    }
                }
            } else {
                // ==========================================
                // 题目列表层（selectedBank != null 时显示）
                // ==========================================

                // 题库信息概览卡片
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                selectedBank!!.bankName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                selectedBank!!.description,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        // 显示题目数量的徽章
                        Badge(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ) {
                            Text(
                                "${selectedBank!!.questionCount}题",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (entries.isEmpty()) {
                    // 没有题目时显示提示
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
                                Icons.Default.HelpOutline,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "暂无题目",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "点击右上角 + 添加题目",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    // 显示题目列表
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(entries) { entry ->
                            QuestionEntryCard(
                                entry = entry,
                                onDelete = { /* TODO: 调用 AIApi.deleteQuestionEntry() 删除题目 */ }
                            )
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // 创建题库对话框
    // ==========================================
    if (showAddBankDialog) {
        AlertDialog(
            onDismissRequest = { showAddBankDialog = false },
            title = { Text("创建题库") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text("题库名称") },
                        placeholder = { Text("如：健康知识题库") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = bankDescription,
                        onValueChange = { bankDescription = it },
                        label = { Text("题库描述") },
                        placeholder = { Text("简要描述题库内容") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAddBankDialog = false
                        // TODO: 调用 AIApi.createQuestionBank() 保存题库
                    }
                ) {
                    Text("创建")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBankDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // ==========================================
    // 添加题目对话框 - 包含完整的题目编辑表单
    // ==========================================
    if (showAddEntryDialog) {
        AlertDialog(
            onDismissRequest = { showAddEntryDialog = false },
            title = { Text("添加题目") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 题目类型下拉选择框
                    var typeExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = questionTypes.find { it.first == questionType }?.second ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("题目类型") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            questionTypes.forEach { (value, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        questionType = value
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 题目内容输入框
                    OutlinedTextField(
                        value = questionText,
                        onValueChange = { questionText = it },
                        label = { Text("题目内容") },
                        placeholder = { Text("请输入题目") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    // 选择题时显示选项输入框（A/B/C/D）
                    if (questionType == "single_choice" || questionType == "multiple_choice") {
                        OutlinedTextField(
                            value = optionA,
                            onValueChange = { optionA = it },
                            label = { Text("选项A") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = optionB,
                            onValueChange = { optionB = it },
                            label = { Text("选项B") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = optionC,
                            onValueChange = { optionC = it },
                            label = { Text("选项C") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = optionD,
                            onValueChange = { optionD = it },
                            label = { Text("选项D") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // 正确答案输入框（根据题目类型显示不同的提示文字）
                    OutlinedTextField(
                        value = correctAnswer,
                        onValueChange = { correctAnswer = it },
                        label = { Text("正确答案") },
                        placeholder = {
                            when (questionType) {
                                "single_choice" -> Text("如：A")
                                "multiple_choice" -> Text("如：A,C")
                                "judgment" -> Text("正确 或 错误")
                                else -> Text("请输入答案")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // 答案解析输入框（可选）
                    OutlinedTextField(
                        value = explanation,
                        onValueChange = { explanation = it },
                        label = { Text("答案解析（可选）") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    // 标签输入框（可选，用逗号分隔）
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("标签（可选）") },
                        placeholder = { Text("用逗号分隔，如：健康,养生") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAddEntryDialog = false
                        // TODO: 调用 AIApi.createQuestionEntry() 保存题目
                    }
                ) {
                    Text("添加")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEntryDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

/**
 * 题库卡片组件 - 展示单个题库的信息
 *
 * 点击卡片可以进入该题库查看题目。
 * 卡片中包含：题库图标、名称、描述和题目数量。
 *
 * @param bank 题库数据
 * @param onClick 点击卡片的回调（进入题库）
 * @param onDelete 删除题库的回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankCard(
    bank: QuestionBankItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,  // 整张卡片可点击
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 题库图标
                Icon(
                    Icons.Default.Quiz,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    // 题库名称
                    Text(
                        bank.bankName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // 题库描述
                    Text(
                        bank.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    // 题目数量（带颜色高亮）
                    Text(
                        "${bank.questionCount} 道题目",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // 删除按钮
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * 题目卡片组件 - 展示一道题目的完整信息
 *
 * 卡片中包含：
 * - 题目类型标签（单选/多选/判断/填空/简答）
 * - 题目内容
 * - 选项列表（选择题时显示，正确答案高亮）
 * - 正确答案
 * - 答案解析
 * - 标签
 *
 * @param entry 题目数据
 * @param onDelete 删除题目的回调
 */
@Composable
fun QuestionEntryCard(
    entry: QuestionEntryItem,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 顶部：题目类型标签 + 删除按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 根据题目类型显示不同颜色的标签
                Badge(
                    containerColor = when (entry.questionType) {
                        "single_choice" -> MaterialTheme.colorScheme.primary
                        "multiple_choice" -> MaterialTheme.colorScheme.secondary
                        "judgment" -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.outline
                    }
                ) {
                    Text(
                        when (entry.questionType) {
                            "single_choice" -> "单选"
                            "multiple_choice" -> "多选"
                            "judgment" -> "判断"
                            "fill_blank" -> "填空"
                            else -> "简答"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 题目内容
            Text(
                entry.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            // 选项列表（选择题时显示，正确答案用高亮颜色标注）
            if (entry.options != null && entry.options.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                entry.options.forEachIndexed { index, option ->
                    // 将索引转换为字母（0->A, 1->B, 2->C, 3->D）
                    val letter = ('A' + index).toString()
                    val isCorrect = entry.correctAnswer.contains(letter)
                    Text(
                        "$letter. $option",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isCorrect) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 正确答案显示（带勾选图标）
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "答案：${entry.correctAnswer}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // 答案解析（可选）
            if (entry.explanation != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "解析：${entry.explanation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 标签列表（可选）
            if (entry.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    entry.tags.forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
        }
    }
}
