package com.terminator.backend

import com.terminator.backend.db.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.terminator.backend.plugins.configureRouting
import com.terminator.backend.plugins.configureSerialization
import com.terminator.backend.plugins.configureSecurity
import com.terminator.backend.plugins.configureStatusPages

fun main() {
    // 初始化数据库连接和表结构
    DatabaseFactory.init()

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureStatusPages()  // 全局异常处理（必须在最前面安装）
        configureSerialization()
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}
