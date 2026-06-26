package com.terminator.android.ui.accessibility

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class GuideStep(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val contentDescription: String
)

val defaultGuideSteps = listOf(
    GuideStep(
        icon = Icons.Default.Home,
        title = "欢迎使用薅羊毛终结者",
        description = "本应用帮助老年人保护消费安全，自动完成日常任务，拦截营销风险。让我们一起了解主要功能。",
        contentDescription = "欢迎页：薅羊毛终结者应用介绍"
    ),
    GuideStep(
        icon = Icons.Default.List,
        title = "自动任务执行",
        description = "应用会自动帮您完成签到、听课、积分等日常任务，无需手动操作，省时省力。",
        contentDescription = "功能介绍：自动任务执行"
    ),
    GuideStep(
        icon = Icons.Default.Shield,
        title = "风险保护",
        description = "实时检测营销诱导、虚假宣传等消费风险，及时预警并拦截，保护您的财产安全。",
        contentDescription = "功能介绍：风险保护"
    ),
    GuideStep(
        icon = Icons.Default.Notifications,
        title = "智能通知",
        description = "重要消息会通过通知和语音播报提醒您，包括任务完成、风险预警和消费预警。",
        contentDescription = "功能介绍：智能通知"
    ),
    GuideStep(
        icon = Icons.Default.Check,
        title = "开始使用",
        description = "一切准备就绪！您可以在设置中调整字体大小、语音播报等无障碍选项。祝您使用愉快！",
        contentDescription = "引导完成，准备开始使用"
    )
)

@Composable
fun OnboardingGuideScreen(
    preferences: AccessibilityPreferences,
    steps: List<GuideStep> = defaultGuideSteps,
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val step = steps[currentStep]
    val isLastStep = currentStep == steps.size - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .semantics {
                    contentDescription = "新手引导，第${currentStep + 1}步，共${steps.size}步。${step.contentDescription}"
                },
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == currentStep) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == currentStep) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant
                                )
                                .semantics {
                                    contentDescription = if (index == currentStep) "当前步骤${index + 1}" else "步骤${index + 1}"
                                }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Text(
                    text = step.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = step.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )

                Text(
                    text = "${currentStep + 1} / ${steps.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.semantics {
                        contentDescription = "步骤${currentStep + 1}，共${steps.size}步"
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        TextButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.semantics {
                                contentDescription = "上一步"
                            }
                        ) {
                            Text("上一步")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (isLastStep) {
                        Button(
                            onClick = {
                                preferences.markGuideCompleted()
                                onComplete()
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "开始使用应用"
                            }
                        ) {
                            Text("开始使用")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    } else {
                        Button(
                            onClick = { currentStep++ },
                            modifier = Modifier.semantics {
                                contentDescription = "下一步"
                            }
                        ) {
                            Text("下一步")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        }
                    }
                }

                TextButton(
                    onClick = {
                        preferences.markGuideCompleted()
                        onComplete()
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "跳过引导"
                    }
                ) {
                    Text("跳过引导")
                }
            }
        }
    }
}
