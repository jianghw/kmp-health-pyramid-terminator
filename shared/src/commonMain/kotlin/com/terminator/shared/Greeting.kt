package com.terminator.shared

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello from KMP Terminator! Running on ${platform.name}"
    }
}
