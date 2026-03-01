package ru.levin.apps.comparator.app.websocket

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import ru.levin.apps.comparator.biz.ComparatorProductProcessor
import ru.levin.apps.comparator.common.ComparatorCorSettings
import ru.levin.apps.comparator.repo.inmemory.ProductRepoInMemory
import java.time.Duration

fun main() {
    embeddedServer(Netty, port = 8081, module = Application::wsModule)
        .start(wait = true)
}

fun Application.wsModule() {
    val settings = ComparatorCorSettings(
        repoTest = ProductRepoInMemory(),
        repoProd = ProductRepoInMemory(),
    )
    val processor = ComparatorProductProcessor(settings)

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        wsProduct(processor)
    }
}