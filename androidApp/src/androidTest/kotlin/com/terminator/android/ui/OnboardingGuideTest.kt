package com.terminator.android.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.terminator.android.ui.accessibility.AccessibilityPreferences
import com.terminator.android.ui.accessibility.OnboardingGuideScreen
import com.terminator.android.ui.theme.TerminatorTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OnboardingGuideTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var preferences: AccessibilityPreferences

    @Before
    fun setup() {
        preferences = AccessibilityPreferences()
    }

    @Test
    fun testOnboardingGuideDisplaysFirstStep() {
        composeTestRule.setContent {
            TerminatorTheme {
                OnboardingGuideScreen(
                    preferences = preferences,
                    onComplete = {}
                )
            }
        }

        composeTestRule.onNodeWithText("欢迎使用薅羊毛终结者").assertIsDisplayed()
        composeTestRule.onNodeWithText("1 / 5").assertIsDisplayed()
    }

    @Test
    fun testOnboardingGuideNavigation() {
        composeTestRule.setContent {
            TerminatorTheme {
                OnboardingGuideScreen(
                    preferences = preferences,
                    onComplete = {}
                )
            }
        }

        composeTestRule.onNodeWithText("下一步").performClick()
        composeTestRule.onNodeWithText("自动任务执行").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 / 5").assertIsDisplayed()
    }

    @Test
    fun testOnboardingGuideNavigateToThirdStep() {
        composeTestRule.setContent {
            TerminatorTheme {
                OnboardingGuideScreen(
                    preferences = preferences,
                    onComplete = {}
                )
            }
        }

        composeTestRule.onNodeWithText("下一步").performClick()
        composeTestRule.onNodeWithText("下一步").performClick()
        composeTestRule.onNodeWithText("风险保护").assertIsDisplayed()
        composeTestRule.onNodeWithText("3 / 5").assertIsDisplayed()
    }

    @Test
    fun testOnboardingGuideBackNavigation() {
        composeTestRule.setContent {
            TerminatorTheme {
                OnboardingGuideScreen(
                    preferences = preferences,
                    onComplete = {}
                )
            }
        }

        composeTestRule.onNodeWithText("下一步").performClick()
        composeTestRule.onNodeWithText("上一步").performClick()
        composeTestRule.onNodeWithText("欢迎使用薅羊毛终结者").assertIsDisplayed()
    }

    @Test
    fun testOnboardingGuideSkipButton() {
        var completed = false

        composeTestRule.setContent {
            TerminatorTheme {
                OnboardingGuideScreen(
                    preferences = preferences,
                    onComplete = { completed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("跳过引导").performClick()
        assert(completed)
        assert(preferences.isGuideCompleted)
    }

    @Test
    fun testOnboardingGuideCompleteAllSteps() {
        var completed = false

        composeTestRule.setContent {
            TerminatorTheme {
                OnboardingGuideScreen(
                    preferences = preferences,
                    onComplete = { completed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("下一步").performClick()
        composeTestRule.onNodeWithText("下一步").performClick()
        composeTestRule.onNodeWithText("下一步").performClick()
        composeTestRule.onNodeWithText("下一步").performClick()

        composeTestRule.onNodeWithText("开始使用").assertIsDisplayed()
        composeTestRule.onNodeWithText("5 / 5").assertIsDisplayed()

        composeTestRule.onNodeWithText("开始使用").performClick()
        assert(completed)
        assert(preferences.isGuideCompleted)
    }

    @Test
    fun testOnboardingGuideLastStepHasNoNextButton() {
        composeTestRule.setContent {
            TerminatorTheme {
                OnboardingGuideScreen(
                    preferences = preferences,
                    onComplete = {}
                )
            }
        }

        repeat(4) {
            composeTestRule.onNodeWithText("下一步").performClick()
        }

        composeTestRule.onNodeWithText("下一步").assertDoesNotExist()
        composeTestRule.onNodeWithText("开始使用").assertIsDisplayed()
    }

    @Test
    fun testOnboardingGuideFirstStepHasNoBackButton() {
        composeTestRule.setContent {
            TerminatorTheme {
                OnboardingGuideScreen(
                    preferences = preferences,
                    onComplete = {}
                )
            }
        }

        composeTestRule.onNodeWithText("上一步").assertDoesNotExist()
    }

    @Test
    fun testOnboardingGuideStepContentDescriptions() {
        composeTestRule.setContent {
            TerminatorTheme {
                OnboardingGuideScreen(
                    preferences = preferences,
                    onComplete = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("新手引导，第1步，共5步。欢迎页：薅羊毛终结者应用介绍").assertExists()
    }

    @Test
    fun testOnboardingGuideDisplaysAllStepTitles() {
        composeTestRule.setContent {
            TerminatorTheme {
                OnboardingGuideScreen(
                    preferences = preferences,
                    onComplete = {}
                )
            }
        }

        val stepTitles = listOf(
            "欢迎使用薅羊毛终结者",
            "自动任务执行",
            "风险保护",
            "智能通知",
            "开始使用"
        )

        stepTitles.forEachIndexed { index, title ->
            composeTestRule.onNodeWithText(title).assertIsDisplayed()
            if (index < stepTitles.size - 1) {
                composeTestRule.onNodeWithText("下一步").performClick()
            }
        }
    }
}
