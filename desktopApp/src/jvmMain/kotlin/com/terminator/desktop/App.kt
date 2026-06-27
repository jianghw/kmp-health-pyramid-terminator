package com.terminator.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.terminator.desktop.ui.screens.*
import com.terminator.desktop.ui.theme.BrandColors
import com.terminator.desktop.ui.theme.HeaderColors
import com.terminator.desktop.ui.theme.TerminatorTheme

// 主Tab导航
enum class MainTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("首页", Icons.Default.Home),
    TASKS("任务", Icons.Default.List),
    PROTECTION("保护", Icons.Default.Shield),
    PROFILE("我的", Icons.Default.Person)
}

// 子页面导航
enum class SubScreen {
    NONE,
    ONE_TAP,
    PROTECTION_DETAIL,
    NOTIFICATIONS,
    REPORTS,
    WARNINGS,
    AI_CONFIG,
    BATCH_EXECUTION,
    CREDENTIAL_LIST,
    CREDENTIAL_INPUT,
    TEMPLATE_LIBRARY,
    QUESTION_BANK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    TerminatorTheme {
        var selectedTab by remember { mutableStateOf(MainTab.HOME) }
        var subScreen by remember { mutableStateOf(SubScreen.NONE) }

        Box(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxSize()) {
                // 侧边导航栏
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = BrandColors.CardBg,
                    contentColor = BrandColors.TextDark
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // 应用标题
                    Text(
                        text = "薅羊毛",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandColors.PrimaryDeep,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "终结者",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandColors.PrimaryDeep,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    MainTab.values().forEach { tab ->
                        NavigationRailItem(
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label, style = MaterialTheme.typography.labelMedium) },
                            selected = selectedTab == tab && subScreen == SubScreen.NONE,
                            onClick = {
                                selectedTab = tab
                                subScreen = SubScreen.NONE
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = BrandColors.PrimaryDeep,
                                selectedTextColor = BrandColors.PrimaryDeep,
                                unselectedIconColor = BrandColors.TextSecondary,
                                unselectedTextColor = BrandColors.TextSecondary,
                                indicatorColor = BrandColors.NavSelectedBg
                            )
                        )
                    }
                }

                // 主内容区域
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BrandColors.SurfaceBg)
                ) {
                    when (subScreen) {
                        SubScreen.NONE -> {
                            when (selectedTab) {
                                MainTab.HOME -> DashboardScreen(
                                    onNavigateToTasks = { subScreen = SubScreen.ONE_TAP },
                                    onNavigateToProtection = { subScreen = SubScreen.PROTECTION_DETAIL },
                                    onNavigateToProfile = { selectedTab = MainTab.PROFILE },
                                    onNavigateToNotifications = { subScreen = SubScreen.NOTIFICATIONS },
                                    onNavigateToReports = { subScreen = SubScreen.REPORTS },
                                    onNavigateToWarnings = { subScreen = SubScreen.WARNINGS }
                                )
                                MainTab.TASKS -> OneTapScreen(
                                    onNavigateBack = { selectedTab = MainTab.HOME }
                                )
                                MainTab.PROTECTION -> ProtectionScreen(
                                    onNavigateBack = { selectedTab = MainTab.HOME }
                                )
                                MainTab.PROFILE -> ProfileScreen(
                                    onNavigateBack = { selectedTab = MainTab.HOME }
                                )
                            }
                        }
                        SubScreen.ONE_TAP -> OneTapScreen(
                            onNavigateBack = { subScreen = SubScreen.NONE }
                        )
                        SubScreen.PROTECTION_DETAIL -> ProtectionScreen(
                            onNavigateBack = { subScreen = SubScreen.NONE }
                        )
                        SubScreen.NOTIFICATIONS -> NotificationScreen(
                            onNavigateBack = { subScreen = SubScreen.NONE }
                        )
                        SubScreen.REPORTS -> ReportScreen(
                            onBack = { subScreen = SubScreen.NONE }
                        )
                        SubScreen.WARNINGS -> WarningScreen(
                            onBack = { subScreen = SubScreen.NONE }
                        )
                        SubScreen.AI_CONFIG -> AIConfigScreen(
                            onNavigateBack = { subScreen = SubScreen.NONE }
                        )
                        SubScreen.BATCH_EXECUTION -> BatchExecutionScreen(
                            onNavigateBack = { subScreen = SubScreen.NONE }
                        )
                        SubScreen.CREDENTIAL_LIST -> CredentialListScreen(
                            onBack = { subScreen = SubScreen.NONE },
                            onAddCredential = { subScreen = SubScreen.CREDENTIAL_INPUT },
                            onEditCredential = { subScreen = SubScreen.CREDENTIAL_INPUT }
                        )
                        SubScreen.CREDENTIAL_INPUT -> CredentialInputScreen(
                            credentialId = null,
                            onBack = { subScreen = SubScreen.CREDENTIAL_LIST },
                            onSave = { subScreen = SubScreen.CREDENTIAL_LIST }
                        )
                        SubScreen.TEMPLATE_LIBRARY -> TemplateLibraryScreen(
                            onBack = { subScreen = SubScreen.NONE },
                            onImportTemplate = { },
                            onExportTemplate = { },
                            onUseTemplate = { }
                        )
                        SubScreen.QUESTION_BANK -> QuestionBankScreen(
                            onNavigateBack = { subScreen = SubScreen.NONE }
                        )
                    }
                }
            }
        }
    }
}

// 消费预警页面 - 匹配Android端清新浅绿主题设计
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarningScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = BrandColors.SurfaceBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header - 使用与其他页面一致的圆形返回按钮样式
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(HeaderColors.Warning)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = androidx.compose.foundation.shape.CircleShape,
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
                            text = "消费预警",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BrandColors.TextDark
                        )
                    }
                }
            }

            // 内容区域
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "⚠️",
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "消费预警功能开发中",
                    style = MaterialTheme.typography.titleLarge,
                    color = BrandColors.TextDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "即将上线，敬请期待",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrandColors.TextSecondary
                )
            }
        }
    }
}
