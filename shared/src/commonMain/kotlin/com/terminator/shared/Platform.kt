package com.terminator.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
