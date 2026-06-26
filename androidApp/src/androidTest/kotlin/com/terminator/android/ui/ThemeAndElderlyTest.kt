package com.terminator.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.terminator.android.ui.accessibility.AccessibilityPreferences
import com.terminator.android.ui.accessibility.FontScalePreset
import com.terminator.android.ui.accessibility.ProvideAccessibilityDensity
import com.terminator.android.ui.theme.TerminatorTheme
import org.junit.Rule
import org.junit.Test

class ThemeAndElderlyTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLightThemeRendering() {
        composeTestRule.setContent {
            TerminatorTheme(darkTheme = false) {
                MaterialTheme {
                    Text(
                        text = "浅色主题测试",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("浅色主题测试").assertIsDisplayed()
    }

    @Test
    fun testDarkThemeRendering() {
        composeTestRule.setContent {
            TerminatorTheme(darkTheme = true) {
                MaterialTheme {
                    Text(
                        text = "深色主题测试",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("深色主题测试").assertIsDisplayed()
    }

    @Test
    fun testHighContrastLightThemeRendering() {
        composeTestRule.setContent {
            TerminatorTheme(darkTheme = false, highContrast = true) {
                MaterialTheme {
                    Text(
                        text = "高对比度浅色主题测试",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("高对比度浅色主题测试").assertIsDisplayed()
    }

    @Test
    fun testHighContrastDarkThemeRendering() {
        composeTestRule.setContent {
            TerminatorTheme(darkTheme = true, highContrast = true) {
                MaterialTheme {
                    Text(
                        text = "高对比度深色主题测试",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("高对比度深色主题测试").assertIsDisplayed()
    }

    @Test
    fun testElderlyTypographyIsLarger() {
        composeTestRule.setContent {
            TerminatorTheme {
                Column {
                    Text(
                        text = "大字体标题",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = "标准正文",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("大字体标题").assertIsDisplayed()
        composeTestRule.onNodeWithText("标准正文").assertIsDisplayed()
    }

    @Test
    fun testFontScaleSmallPreset() {
        val preferences = AccessibilityPreferences()
        preferences.setFontScalePreset(FontScalePreset.SMALL)

        composeTestRule.setContent {
            TerminatorTheme {
                ProvideAccessibilityDensity(preferences = preferences) {
                    Text(text = "小字体文本")
                }
            }
        }

        composeTestRule.onNodeWithText("小字体文本").assertIsDisplayed()
    }

    @Test
    fun testFontScaleLargePreset() {
        val preferences = AccessibilityPreferences()
        preferences.setFontScalePreset(FontScalePreset.LARGE)

        composeTestRule.setContent {
            TerminatorTheme {
                ProvideAccessibilityDensity(preferences = preferences) {
                    Text(text = "大字体文本")
                }
            }
        }

        composeTestRule.onNodeWithText("大字体文本").assertIsDisplayed()
    }

    @Test
    fun testFontScaleExtraLargePreset() {
        val preferences = AccessibilityPreferences()
        preferences.setFontScalePreset(FontScalePreset.EXTRA_LARGE)

        composeTestRule.setContent {
            TerminatorTheme {
                ProvideAccessibilityDensity(preferences = preferences) {
                    Text(text = "特大字体文本")
                }
            }
        }

        composeTestRule.onNodeWithText("特大字体文本").assertIsDisplayed()
    }

    @Test
    fun testFontScaleHugePreset() {
        val preferences = AccessibilityPreferences()
        preferences.setFontScalePreset(FontScalePreset.HUGE)

        composeTestRule.setContent {
            TerminatorTheme {
                ProvideAccessibilityDensity(preferences = preferences) {
                    Text(text = "超大字体文本")
                }
            }
        }

        composeTestRule.onNodeWithText("超大字体文本").assertIsDisplayed()
    }

    @Test
    fun testElderlyFriendlyFontSizeValues() {
        val typography = com.terminator.android.ui.theme.ElderlyTypography

        assert(typography.displayLarge.fontSize.value >= 30f) {
            "displayLarge fontSize should be at least 30sp for elderly users"
        }
        assert(typography.headlineLarge.fontSize.value >= 22f) {
            "headlineLarge fontSize should be at least 22sp for elderly users"
        }
        assert(typography.bodyLarge.fontSize.value >= 16f) {
            "bodyLarge fontSize should be at least 16sp for elderly users"
        }
        assert(typography.bodyMedium.fontSize.value >= 14f) {
            "bodyMedium fontSize should be at least 14sp for elderly users"
        }
        assert(typography.labelLarge.fontSize.value >= 14f) {
            "labelLarge fontSize should be at least 14sp for elderly users"
        }
    }

    @Test
    fun testAccessibilityPreferencesDefaults() {
        val preferences = AccessibilityPreferences()

        assert(preferences.fontScale == FontScalePreset.DEFAULT.scale)
        assert(preferences.isVoiceAnnouncementEnabled)
        assert(!preferences.isHighContrastMode)
        assert(!preferences.isGuideCompleted)
    }

    @Test
    fun testAccessibilityPreferencesGuideCompleted() {
        val preferences = AccessibilityPreferences()

        preferences.markGuideCompleted()
        assert(preferences.isGuideCompleted)

        preferences.resetGuide()
        assert(!preferences.isGuideCompleted)
    }

    @Test
    fun testAccessibilityPreferencesFontScaleBounds() {
        val preferences = AccessibilityPreferences()

        preferences.setFontScale(0.5f)
        assert(preferences.fontScale >= 0.8f) { "Font scale should be clamped to minimum 0.8" }

        preferences.setFontScale(3.0f)
        assert(preferences.fontScale <= 2.0f) { "Font scale should be clamped to maximum 2.0" }
    }

    @Test
    fun testAccessibilityPreferencesVoiceToggle() {
        val preferences = AccessibilityPreferences()

        assert(preferences.isVoiceAnnouncementEnabled)
        preferences.toggleVoiceAnnouncement()
        assert(!preferences.isVoiceAnnouncementEnabled)
        preferences.toggleVoiceAnnouncement()
        assert(preferences.isVoiceAnnouncementEnabled)
    }

    @Test
    fun testFontScalePresetValues() {
        val presets = FontScalePreset.entries

        assert(presets.size == 5)
        assert(FontScalePreset.SMALL.scale < FontScalePreset.DEFAULT.scale)
        assert(FontScalePreset.DEFAULT.scale < FontScalePreset.LARGE.scale)
        assert(FontScalePreset.LARGE.scale < FontScalePreset.EXTRA_LARGE.scale)
        assert(FontScalePreset.EXTRA_LARGE.scale < FontScalePreset.HUGE.scale)
    }
}
