package com.terminator.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.terminator.shared.Greeting

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMP Terminator"
    ) {
        App()
    }
}
