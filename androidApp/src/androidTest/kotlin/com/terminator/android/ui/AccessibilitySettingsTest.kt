package com.terminator.android.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.terminator.android.ui.accessibility.AccessibilityPreferences
import com.terminator.android.ui.accessibility.AccessibilitySettingsScreen
import com.terminator.android.ui.accessibility.FontScalePreset
import com.terminator.android.ui.theme.TerminatorTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AccessibilitySettingsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var preferences: AccessibilityPreferences

    @Before
    fun setup() {
        preferences = AccessibilityPreferences()
    }

    @Test
    fun testAccessibilitySettingsScreenTitle() {
        composeTestRule.setContent {
            TerminatorTheme {
                AccessibilitySettingsScreen(
                    preferences = preferences,
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("无障碍设置").assertIsDisplayed()
    }

    @Test
    fun testFontSizeSectionDisplayed() {
        composeTestRule.setContent {
            TerminatorTheme {
                AccessibilitySettingsScreen(
                    preferences = preferences,
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("字体大小").assertIsDisplayed()
        composeTestRule.onNodeWithText("缩放比例：100%").assertIsDisplayed()
    }

    @Test
    fun testFontScalePresetChips() {
        composeTestRule.setContent {
            TerminatorTheme {
                AccessibilitySettingsScreen(
                    preferences = preferences,
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("小").assertIsDisplayed()
        composeTestRule.onNodeWithText("标准").assertIsDisplayed()
        composeTestRule.onNodeWithText("大").assertIsDisplayed()
        composeTestRule.onNodeWithText("特大").assertIsDisplayed()
        composeTestRule.onNodeWithText("超大").assertIsDisplayed()
    }

    @Test
    fun testFontScalePresetClick() {
        composeTestRule.setContent {
            TerminatorTheme {
                AccessibilitySettingsScreen(
                    preferences = preferences,
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("大").performClick()
        assert(preferences.fontScale == FontScalePreset.LARGE.scale)
    }

    @Test
    fun testVoiceAnnouncementToggle() {
        composeTestRule.setContent {
            TerminatorTheme {
                AccessibilitySettingsScreen(
                    preferences = preferences,
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("语音播报").assertIsDisplayed()
        composeTestRule.onNodeWithText("任务完成、风险预警时自动语音提示").assertIsDisplayed()

        assert(preferences.isVoiceAnnouncementEnabled)

        composeTestRule.onAllNodes(hasText("语音播报") and hasRole(Role.Switch)).onFirst().performClick()
        assert(!preferences.isVoiceAnnouncementEnabled)
    }

    @Test
    fun testHighContrastToggle() {
        composeTestRule.setContent {
            TerminatorTheme {
                AccessibilitySettingsScreen(
                    preferences = preferences,
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("高对比度模式").assertIsDisplayed()
        composeTestRule.onNodeWithText("增强文字与背景的对比度，便于阅读").assertIsDisplayed()

        assert(!preferences.isHighContrastMode)

        composeTestRule.onAllNodes(hasText("高对比度模式") and hasRole(Role.Switch)).onFirst().performClick()
        assert(preferences.isHighContrastMode)
    }

    @Test
    fun testGuideReplayButton() {
        composeTestRule.setContent {
            TerminatorTheme {
                AccessibilitySettingsScreen(
                    preferences = preferences,
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("新手引导").assertIsDisplayed()
        composeTestRule.onNodeWithText("重新播放应用使用引导").assertIsDisplayed()
        composeTestRule.onNodeWithText("重播引导").assertIsDisplayed()

        preferences.markGuideCompleted()
        assert(preferences.isGuideCompleted)

        composeTestRule.onNodeWithText("重播引导").performClick()
        assert(!preferences.isGuideCompleted)
    }

    @Test
    fun testBackButton() {
        var backClicked = false

        composeTestRule.setContent {
            TerminatorTheme {
                AccessibilitySettingsScreen(
                    preferences = preferences,
                    onBack = { backClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("返回上一页").performClick()
        assert(backClicked)
    }

    @Test
    fun testFontPreviewSection() {
        composeTestRule.setContent {
            TerminatorTheme {
                AccessibilitySettingsScreen(
                    preferences = preferences,
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("字体预览：当前缩放比例 100%").assertIsDisplayed()
    }

    @Test
    fun testFontPreviewUpdatesWithScale() {
        composeTestRule.setContent {
            TerminatorTheme {
                AccessibilitySettingsScreen(
                    preferences = preferences,
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("特大").performClick()
        composeTestRule.onNodeWithText("字体预览：当前缩放比例 130%").assertIsDisplayed()
    }
}
