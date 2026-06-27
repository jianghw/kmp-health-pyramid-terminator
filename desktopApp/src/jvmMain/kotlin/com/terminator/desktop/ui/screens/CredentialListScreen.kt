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
fun CredentialListScreen(
    onBack: () -> Unit,
    onAddCredential: () -> Unit,
    onEditCredential: (Long) -> Unit
) {
    var credentials by remember { mutableStateOf(getSampleCredentials()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedCredential by remember { mutableStateOf<SampleCredential?>(null) }

    Scaffold(
        containerColor = BrandColors.SurfaceBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CredentialListHeader(onBack = onBack)

            if (credentials.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = BrandColors.TextSecondary
                        )
                        Text(
                            text = "暂无凭证",
                            style = MaterialTheme.typography.titleLarge,
                            color = BrandColors.TextSecondary
                        )
                        Button(
                            onClick = onAddCredential,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandColors.PrimaryDeep
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("添加凭证")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "已保存的凭证",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = BrandColors.TextDark
                            )
                            Button(
                                onClick = onAddCredential,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandColors.PrimaryDeep
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("添加凭证")
                            }
                        }
                    }

                    items(credentials) { credential ->
                        CredentialCard(
                            credential = credential,
                            onEdit = { onEditCredential(credential.id) },
                            onDelete = {
                                selectedCredential = credential
                                showDeleteDialog = true
                            },
                            onVerify = { }
                        )
                    }
                }
            }
        }

        if (showDeleteDialog && selectedCredential != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("删除凭证") },
                text = { Text("确定要删除 ${selectedCredential!!.appName} 的凭证吗？此操作不可恢复。") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            credentials = credentials.filter { it.id != selectedCredential!!.id }
                            showDeleteDialog = false
                        }
                    ) {
                        Text("删除", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
private fun CredentialListHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(HeaderColors.CredentialList)
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
                    text = "凭证管理",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BrandColors.TextDark
                )
            }
        }
    }
}

@Composable
private fun CredentialCard(
    credential: SampleCredential,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onVerify: () -> Unit
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = BrandColors.PrimaryBg
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when (credential.credentialType) {
                                    "ACCOUNT_PASSWORD" -> Icons.Default.Key
                                    "TOKEN" -> Icons.Default.Token
                                    "API_KEY" -> Icons.Default.Api
                                    "COOKIE" -> Icons.Default.Cookie
                                    else -> Icons.Default.Lock
                                },
                                contentDescription = null,
                                tint = BrandColors.PrimaryDeep,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = credential.appName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandColors.TextDark
                        )
                        Text(
                            text = credential.alias,
                            style = MaterialTheme.typography.bodySmall,
                            color = BrandColors.TextSecondary
                        )
                    }
                }

                CredentialStatusChip(status = credential.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = onVerify,
                    label = { Text("验证") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                AssistChip(
                    onClick = onEdit,
                    label = { Text("编辑") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                AssistChip(
                    onClick = onDelete,
                    label = { Text("删除") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            if (credential.lastUsedAt != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "上次使用: ${credential.lastUsedAt}",
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun CredentialStatusChip(status: String) {
    val (color, text) = when (status) {
        "ACTIVE" -> BrandColors.PrimaryDeep to "有效"
        "EXPIRED" -> BrandColors.IconOrange to "已过期"
        "INVALID" -> MaterialTheme.colorScheme.error to "无效"
        else -> BrandColors.TextSecondary to status
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}

private data class SampleCredential(
    val id: Long,
    val appName: String,
    val credentialType: String,
    val alias: String,
    val status: String,
    val lastUsedAt: String? = null
)

private fun getSampleCredentials() = listOf(
    SampleCredential(1, "微信健康", "TOKEN", "微信token", "ACTIVE", "2026-06-25 08:30"),
    SampleCredential(2, "支付宝运动", "ACCOUNT_PASSWORD", "支付宝账号", "ACTIVE", "2026-06-24 18:00"),
    SampleCredential(3, "京东健康", "COOKIE", "京东cookie", "EXPIRED", "2026-06-20 10:15"),
    SampleCredential(4, "美团健康", "API_KEY", "美团API", "ACTIVE", "2026-06-25 09:00")
)
