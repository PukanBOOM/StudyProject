package ru.levin.apps.comparator.app.ktor

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import ru.levin.apps.comparator.app.ktor.v1.v1Product
import ru.levin.apps.comparator.biz.ComparatorProductProcessor
import ru.levin.apps.comparator.common.ComparatorCorSettings
import ru.levin.apps.comparator.repo.inmemory.ProductRepoInMemory

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val settings = ComparatorCorSettings(
        repoTest = ProductRepoInMemory(),
        repoProd = ProductRepoInMemory(),
    )
    val processor = ComparatorProductProcessor(settings)

    install(ContentNegotiation) {
        jackson {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
    }
    routing {
        route("/v1/product") {
            v1Product(processor)
        }
    }
}