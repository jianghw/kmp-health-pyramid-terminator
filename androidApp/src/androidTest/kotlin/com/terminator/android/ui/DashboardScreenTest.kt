package com.terminator.android.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.terminator.android.ui.screens.DashboardScreen
import com.terminator.android.ui.theme.TerminatorTheme
import org.junit.Rule
import org.junit.Test

class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDashboardDisplaysTitle() {
        composeTestRule.setContent {
            TerminatorTheme {
                DashboardScreen(
                    onNavigateToTasks = {},
                    onNavigateToProtection = {},
                    onNavigateToProfile = {}
                )
            }
        }

        composeTestRule.onNodeWithText("薅羊毛终结者").assertIsDisplayed()
    }

    @Test
    fun testDashboardDisplaysTodayProtectionSection() {
        composeTestRule.setContent {
            TerminatorTheme {
                DashboardScreen(
                    onNavigateToTasks = {},
                    onNavigateToProtection = {},
                    onNavigateToProfile = {}
                )
            }
        }

        composeTestRule.onNodeWithText("今日保护").assertIsDisplayed()
        composeTestRule.onNodeWithText("自动完成").assertIsDisplayed()
        composeTestRule.onNodeWithText("节省时间").assertIsDisplayed()
        composeTestRule.onNodeWithText("拦截风险").assertIsDisplayed()
    }

    @Test
    fun testDashboardDisplaysQuickActions() {
        composeTestRule.setContent {
            TerminatorTheme {
                DashboardScreen(
                    onNavigateToTasks = {},
                    onNavigateToProtection = {},
                    onNavigateToProfile = {}
                )
            }
        }

        composeTestRule.onNodeWithText("快捷操作").assertIsDisplayed()
        composeTestRule.onNodeWithText("一键执行").assertIsDisplayed()
        composeTestRule.onNodeWithText("查看报告").assertIsDisplayed()
        composeTestRule.onNodeWithText("消费预警").assertIsDisplayed()
        composeTestRule.onNodeWithText("通知中心").assertIsDisplayed()
    }

    @Test
    fun testDashboardDisplaysTaskList() {
        composeTestRule.setContent {
            TerminatorTheme {
                DashboardScreen(
                    onNavigateToTasks = {},
                    onNavigateToProtection = {},
                    onNavigateToProfile = {}
                )
            }
        }

        composeTestRule.onNodeWithText("应用任务列表").assertIsDisplayed()
        composeTestRule.onNodeWithText("微信健康打卡").assertIsDisplayed()
        composeTestRule.onNodeWithText("养生课堂").assertIsDisplayed()
        composeTestRule.onNodeWithText("健康积分").assertIsDisplayed()
        composeTestRule.onNodeWithText("运动健康").assertIsDisplayed()
    }

    @Test
    fun testDashboardDisplaysNavigationItems() {
        composeTestRule.setContent {
            TerminatorTheme {
                DashboardScreen(
                    onNavigateToTasks = {},
                    onNavigateToProtection = {},
                    onNavigateToProfile = {}
                )
            }
        }

        composeTestRule.onNodeWithText("首页").assertIsDisplayed()
        composeTestRule.onNodeWithText("任务").assertIsDisplayed()
        composeTestRule.onNodeWithText("保护").assertIsDisplayed()
        composeTestRule.onNodeWithText("我的").assertIsDisplayed()
    }

    @Test
    fun testDashboardNavigationItemClick() {
        var tasksClicked = false
        var protectionClicked = false
        var profileClicked = false

        composeTestRule.setContent {
            TerminatorTheme {
                DashboardScreen(
                    onNavigateToTasks = { tasksClicked = true },
                    onNavigateToProtection = { protectionClicked = true },
                    onNavigateToProfile = { profileClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("任务").performClick()
        assert(tasksClicked)

        composeTestRule.onNodeWithText("保护").performClick()
        assert(protectionClicked)

        composeTestRule.onNodeWithText("我的").performClick()
        assert(profileClicked)
    }

    @Test
    fun testDashboardReportsClick() {
        var reportsClicked = false

        composeTestRule.setContent {
            TerminatorTheme {
                DashboardScreen(
                    onNavigateToTasks = {},
                    onNavigateToProtection = {},
                    onNavigateToProfile = {},
                    onNavigateToReports = { reportsClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("查看报告").performClick()
        assert(reportsClicked)
    }

    @Test
    fun testDashboardWarningsClick() {
        var warningsClicked = false

        composeTestRule.setContent {
            TerminatorTheme {
                DashboardScreen(
                    onNavigateToTasks = {},
                    onNavigateToProtection = {},
                    onNavigateToProfile = {},
                    onNavigateToWarnings = { warningsClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("消费预警").performClick()
        assert(warningsClicked)
    }
}
