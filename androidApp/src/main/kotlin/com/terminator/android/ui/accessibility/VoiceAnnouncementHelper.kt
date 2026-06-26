package com.terminator.android.ui.accessibility

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.terminator.shared.model.RiskSeverity
import java.util.Locale

sealed class AnnouncementEvent {
    data class TaskCompleted(val taskName: String) : AnnouncementEvent()
    data class TaskFailed(val taskName: String, val reason: String) : AnnouncementEvent()
    data class RiskWarning(val severity: RiskSeverity, val description: String) : AnnouncementEvent()
    data class ConsumptionAlert(val level: String, val title: String, val message: String) : AnnouncementEvent()
    data class NotificationReceived(val title: String, val body: String) : AnnouncementEvent()
    data class Custom(val message: String) : AnnouncementEvent()
}

class VoiceAnnouncementHelper(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    var isEnabled = true

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.CHINESE)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setLanguage(Locale.CHINA)
                }
                engine.setSpeechRate(0.9f)
                engine.setPitch(1.0f)
                isInitialized = true
            }
        }
    }

    fun announce(event: AnnouncementEvent) {
        if (!isEnabled || !isInitialized) return
        val message = when (event) {
            is AnnouncementEvent.TaskCompleted ->
                "任务完成：${event.taskName}已成功执行"

            is AnnouncementEvent.TaskFailed ->
                "任务失败：${event.taskName}执行失败，原因：${event.reason}"

            is AnnouncementEvent.RiskWarning -> {
                val severityLabel = when (event.severity) {
                    RiskSeverity.LOW -> "低"
                    RiskSeverity.MEDIUM -> "中"
                    RiskSeverity.HIGH -> "高"
                    RiskSeverity.CRITICAL -> "严重"
                }
                "风险预警：${severityLabel}等级风险，${event.description}"
            }

            is AnnouncementEvent.ConsumptionAlert -> {
                "消费预警：${event.title}，${event.message}"
            }

            is AnnouncementEvent.NotificationReceived ->
                "收到通知：${event.title}，${event.body}"

            is AnnouncementEvent.Custom -> event.message
        }
        speak(message)
    }

    fun speak(text: String) {
        if (!isEnabled || !isInitialized) return
        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, System.currentTimeMillis().toString())
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }

    fun isReady(): Boolean = isInitialized
}

@Composable
fun rememberVoiceAnnouncementHelper(
    preferences: AccessibilityPreferences
): VoiceAnnouncementHelper {
    val context = LocalContext.current
    val helper = remember { VoiceAnnouncementHelper(context) }

    DisposableEffect(preferences.isVoiceAnnouncementEnabled) {
        helper.isEnabled = preferences.isVoiceAnnouncementEnabled
        onDispose { }
    }

    DisposableEffect(Unit) {
        onDispose { helper.shutdown() }
    }

    return helper
}
