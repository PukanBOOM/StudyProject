package ru.levin.apps.comparator.app.websocket

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import ru.levin.apps.comparator.biz.ComparatorProductProcessor
import ru.levin.apps.comparator.common.ComparatorCorSettings
import ru.levin.apps.comparator.repo.inmemory.ProductRepoInMemory
import ru.levin.apps.comparator.repo.postgres.ProductRepoPostgres
import java.time.Duration

fun main() {
    val dbUrl = System.getenv("DB_URL")
        ?: "jdbc:postgresql://localhost:5432/comparator"
    val dbUser = System.getenv("DB_USER")
        ?: "comparator"
    val dbPassword = System.getenv("DB_PASSWORD")
        ?: "comparator"

    val settings = ComparatorCorSettings(
        repoTest = ProductRepoInMemory(),
        repoProd = ProductRepoPostgres(
            url = dbUrl,
            user = dbUser,
            password = dbPassword,
        ),
    )

    embeddedServer(Netty, port = 8081) {
        wsModule(settings)
    }.start(wait = true)
}

fun Application.wsModule(
    settings: ComparatorCorSettings = ComparatorCorSettings(
        repoTest = ProductRepoInMemory(),
        repoProd = ProductRepoInMemory(),
    ),
) {
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