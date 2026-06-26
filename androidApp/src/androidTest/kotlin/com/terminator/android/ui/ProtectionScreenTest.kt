package com.terminator.android.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.terminator.android.ui.screens.ProtectionScreen
import com.terminator.android.ui.theme.TerminatorTheme
import org.junit.Rule
import org.junit.Test

class ProtectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testProtectionScreenTitle() {
        composeTestRule.setContent {
            TerminatorTheme {
                ProtectionScreen(onBack = {})
            }
        }

        composeTestRule.onNodeWithText("风险保护").assertIsDisplayed()
    }

    @Test
    fun testProtectionScreenDisplaysRiskSummary() {
        composeTestRule.setContent {
            TerminatorTheme {
                ProtectionScreen(onBack = {})
            }
        }

        composeTestRule.onNodeWithText("风险汇总").assertIsDisplayed()
        composeTestRule.onNodeWithText("高风险").assertIsDisplayed()
        composeTestRule.onNodeWithText("中风险").assertIsDisplayed()
        composeTestRule.onNodeWithText("低风险").assertIsDisplayed()
    }

    @Test
    fun testProtectionScreenDisplaysRiskEvents() {
        composeTestRule.setContent {
            TerminatorTheme {
                ProtectionScreen(onBack = {})
            }
        }

        composeTestRule.onNodeWithText("最近风险事件").assertIsDisplayed()
        composeTestRule.onNodeWithText("营销诱导检测").assertIsDisplayed()
        composeTestRule.onNodeWithText("虚假宣传识别").assertIsDisplayed()
        composeTestRule.onNodeWithText("价格异常提醒").assertIsDisplayed()
    }

    @Test
    fun testProtectionScreenBackButton() {
        var backClicked = false

        composeTestRule.setContent {
            TerminatorTheme {
                ProtectionScreen(onBack = { backClicked = true })
            }
        }

        composeTestRule.onNodeWithContentDescription("返回").performClick()
        assert(backClicked)
    }

    @Test
    fun testProtectionScreenRiskSeverityDisplay() {
        composeTestRule.setContent {
            TerminatorTheme {
                ProtectionScreen(onBack = {})
            }
        }

        composeTestRule.onNodeWithText("高").assertIsDisplayed()
        composeTestRule.onNodeWithText("中").assertIsDisplayed()
        composeTestRule.onNodeWithText("低").assertIsDisplayed()
    }

    @Test
    fun testProtectionScreenRiskCounts() {
        composeTestRule.setContent {
            TerminatorTheme {
                ProtectionScreen(onBack = {})
            }
        }

        composeTestRule.onNodeWithText("2").assertIsDisplayed()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
        composeTestRule.onNodeWithText("8").assertIsDisplayed()
    }
}
