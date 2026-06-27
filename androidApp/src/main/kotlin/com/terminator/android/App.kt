package com.terminator.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.terminator.android.ui.accessibility.AccessibilityPreferences
import com.terminator.android.ui.accessibility.OnboardingGuideScreen
import com.terminator.android.ui.screens.DashboardScreen
import com.terminator.android.ui.screens.OneTapScreen
import com.terminator.android.ui.screens.ProtectionScreen
import com.terminator.android.ui.screens.NotificationScreen
import com.terminator.android.ui.screens.ReportScreen
import com.terminator.android.ui.screens.ProfileScreen
import com.terminator.android.ui.theme.BrandColors
import com.terminator.android.ui.theme.HeaderColors

val LocalAccessibilityPreferences = androidx.compose.runtime.staticCompositionLocalOf {
    AccessibilityPreferences()
}

// 主Tab导航
enum class MainTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("首页", Icons.Default.Home),
    TASKS("任务", Icons.Default.List),
    PROTECTION("保护", Icons.Default.Shield),
    PROFILE("我的", Icons.Default.Person)
}

// 子页面导航（无底部导航栏）
enum class SubScreen {
    NONE,
    ONE_TAP,
    PROTECTION_DETAIL,
    NOTIFICATIONS,
    REPORTS,
    WARNINGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val preferences = remember { AccessibilityPreferences() }
    
    var showMainUI by remember { mutableStateOf(preferences.isGuideCompleted) }
    
    if (!showMainUI) {
        WelcomeScreen(
            onStartProtection = { showMainUI = true }
        )
        if (!preferences.isGuideCompleted) {
            OnboardingGuideScreen(
                preferences = preferences,
                onComplete = { 
                    preferences.markGuideCompleted()
                    showMainUI = true
                }
            )
        }
        return
    }
    
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }
    var subScreen by remember { mutableStateOf(SubScreen.NONE) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        when (subScreen) {
            SubScreen.NONE -> {
                when (selectedTab) {
                    MainTab.HOME -> DashboardScreen(
                        onNavigateToTasks = { subScreen = SubScreen.ONE_TAP },
                        onNavigateToProtection = { subScreen = SubScreen.PROTECTION_DETAIL },
                        onNavigateToProfile = { selectedTab = MainTab.PROFILE },
                        onNavigateToWarnings = { subScreen = SubScreen.WARNINGS },
                        onNavigateToNotifications = { subScreen = SubScreen.NOTIFICATIONS },
                        onNavigateToReports = { subScreen = SubScreen.REPORTS }
                    )
                    MainTab.TASKS -> OneTapScreen(
                        onNavigateBack = { selectedTab = MainTab.HOME }
                    )
                    MainTab.PROTECTION -> ProtectionScreen(
                        onBack = { selectedTab = MainTab.HOME }
                    )
                    MainTab.PROFILE -> ProfileScreen(
                        onBack = { selectedTab = MainTab.HOME }
                    )
                }
            }
            SubScreen.ONE_TAP -> OneTapScreen(
                onNavigateBack = { subScreen = SubScreen.NONE }
            )
            SubScreen.PROTECTION_DETAIL -> ProtectionScreen(
                onBack = { subScreen = SubScreen.NONE }
            )
            SubScreen.NOTIFICATIONS -> NotificationScreen(
                onBack = { subScreen = SubScreen.NONE }
            )
            SubScreen.REPORTS -> ReportScreen(
                onBack = { subScreen = SubScreen.NONE }
            )
            SubScreen.WARNINGS -> WarningScreen(
                onBack = { subScreen = SubScreen.NONE }
            )
        }
        
        if (subScreen == SubScreen.NONE) {
            NavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                containerColor = BrandColors.CardBg,
                tonalElevation = 8.dp
            ) {
                MainTab.values().forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label, style = MaterialTheme.typography.labelMedium) },
                        selected = selectedTab == tab,
                        onClick = { 
                            selectedTab = tab
                            subScreen = SubScreen.NONE
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandColors.PrimaryDeep,
                            selectedTextColor = BrandColors.PrimaryDeep,
                            unselectedIconColor = BrandColors.TextSecondary,
                            unselectedTextColor = BrandColors.TextSecondary,
                            indicatorColor = BrandColors.NavSelectedBg
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(onStartProtection: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HeaderColors.Dashboard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 图标
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🛡️",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 标题
            Text(
                text = "薅羊毛终结者",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = BrandColors.TextDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 副标题
            Text(
                text = "智能保护您的消费安全",
                style = MaterialTheme.typography.bodyLarge,
                color = BrandColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 开始按钮
            Button(
                onClick = onStartProtection,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandColors.PrimaryDeep
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
            ) {
                Text(
                    text = "开始保护",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 功能特点
            Column(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureItem("✅", "自动完成任务")
                FeatureItem("🛡️", "拦截营销风险")
                FeatureItem("🔔", "智能消费预警")
            }
        }
    }
}

@Composable
private fun FeatureItem(icon: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = BrandColors.TextDark
        )
    }
}

// 消费预警页面 - 匹配清新浅绿主题
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarningScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
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
                        .statusBarsPadding()
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "\u26A0\uFE0F",
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
