package com.terminator.desktop.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ==========================================
// 品牌色系统 — 薅羊毛终结者（桌面版）
// ==========================================
// 设计理念：深翠绿主色 → 专业信任感
// 强调色：琥珀橙 → 温暖引导注意力
// 各页面 Header 独立色彩 → 视觉区分

object BrandColors {
    val PrimaryDeep = Color(0xFF0D7C5F)       // 深翠绿 - 品牌主色
    val Primary = Color(0xFF059669)             // 亮翠绿
    val PrimaryLight = Color(0xFFB8F0D8)        // 薄荷绿
    val PrimaryBg = Color(0xFFECFDF5)           // 极浅绿背景

    val Secondary = Color(0xFF1A6B5A)           // 深青绿
    val SecondaryLight = Color(0xFFA4F1D8)       // 浅翡翠

    val IconOrange = Color(0xFFD97706)           // 琥珀橙 - 强调色
    val IconOrangeBg = Color(0xFFFFFBEB)        // 浅琥珀背景

    val IconBlue = Color(0xFF6366F1)            // 靛蓝 - 辅助色
    val IconBlueBg = Color(0xFFEEF2FF)          // 浅靛蓝背景

    val IconPurple = Color(0xFF8B5CF6)          // 紫色 - 辅助色
    val IconPurpleBg = Color(0xFFF5F3FF)        // 浅紫背景

    val IconRed = Color(0xFFDC2626)             // 警示红
    val IconRedBg = Color(0xFFFEF2F2)           // 浅红背景

    val TextDark = Color(0xFF1A2E25)            // 主文字色
    val TextSecondary = Color(0xFF6F7F76)        // 次要文字色
    val TextMuted = Color(0xFF9CA3AF)            // 极浅文字色

    val NavSelectedBg = Color(0xFFD1FAE5)       // 导航选中背景
    val SurfaceBg = Color(0xFFF8FAF9)           // 页面背景
    val CardBg = Color.White                     // 卡片背景
    val Divider = Color(0xFFE6ECE8)             // 分割线
}

object HeaderColors {
    val Dashboard = Color(0xFF059669)            // 首页 - 翠绿
    val OneTap = Color(0xFF1E40AF)              // 一键执行 - 靛蓝
    val Protection = Color(0xFFDC2626)           // 风险保护 - 警示红
    val Profile = Color(0xFF0D9488)              // 个人中心 - 翡翠
    val Notification = Color(0xFF6366F1)          // 通知中心 - 靛蓝紫
    val Report = Color(0xFF059669)               // 数据报告 - 翠绿
    val Warning = Color(0xFFD97706)              // 消费预警 - 琥珀橙
    val AIConfig = Color(0xFF7C3AED)              // AI配置 - 紫色
    val BatchExecution = Color(0xFF1E40AF)        // 批量执行 - 靛蓝
    val CredentialList = Color(0xFF0D7C5F)        // 凭证管理 - 深翠绿
    val CredentialInput = Color(0xFF0D7C5F)       // 凭证输入 - 深翠绿
    val TemplateLibrary = Color(0xFF059669)       // 模板库 - 翠绿
    val QuestionBank = Color(0xFF6366F1)          // 题库管理 - 靛蓝紫
}

object CardColors {
    val Default = Color.White
    val StatsMint = Color(0xFFECFDF5)            // 统计薄荷色
    val TaskItem = Color(0xFFF8FAF9)             // 任务列表项背景
    val QuickAction = Color(0xFFF3F4F6)          // 快捷操作背景
}

// ==========================================
// Material 3 配色方案
// ==========================================

private val LightColorScheme = lightColorScheme(
    primary = BrandColors.PrimaryDeep,
    onPrimary = Color.White,
    primaryContainer = BrandColors.PrimaryLight,
    onPrimaryContainer = Color(0xFF002114),
    secondary = BrandColors.Secondary,
    onSecondary = Color.White,
    secondaryContainer = BrandColors.SecondaryLight,
    onSecondaryContainer = Color(0xFF002114),
    tertiary = BrandColors.IconOrange,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFDE68A),
    onTertiaryContainer = Color(0xFF422006),
    error = BrandColors.IconRed,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF450A0A),
    background = BrandColors.SurfaceBg,
    onBackground = BrandColors.TextDark,
    surface = BrandColors.CardBg,
    onSurface = BrandColors.TextDark,
    surfaceVariant = Color(0xFFE6ECE8),
    onSurfaceVariant = Color(0xFF3F4F47),
    outline = BrandColors.TextSecondary,
    outlineVariant = Color(0xFFBFCCB6),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6EE7B7),
    onPrimary = Color(0xFF003826),
    primaryContainer = BrandColors.PrimaryDeep,
    onPrimaryContainer = BrandColors.PrimaryLight,
    secondary = Color(0xFF88D4B5),
    onSecondary = Color(0xFF003826),
    secondaryContainer = BrandColors.Secondary,
    onSecondaryContainer = BrandColors.SecondaryLight,
    tertiary = Color(0xFFFBBF24),
    onTertiary = Color(0xFF422006),
    tertiaryContainer = BrandColors.IconOrange,
    onTertiaryContainer = Color(0xFFFDE68A),
    error = Color(0xFFFCA5A5),
    onError = Color(0xFF450A0A),
    errorContainer = BrandColors.IconRed,
    onErrorContainer = Color(0xFFFEE2E2),
    background = Color(0xFF0F1F18),
    onBackground = Color(0xFFDCE5DE),
    surface = Color(0xFF0F1F18),
    onSurface = Color(0xFFDCE5DE),
    surfaceVariant = Color(0xFF3F4F47),
    onSurfaceVariant = Color(0xFFBFCCB6),
    outline = Color(0xFF8A9A90),
    outlineVariant = Color(0xFF3F4F47),
)

// ==========================================
// 字体排版
// ==========================================

val DesktopTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun TerminatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DesktopTypography,
        content = content
    )
}
