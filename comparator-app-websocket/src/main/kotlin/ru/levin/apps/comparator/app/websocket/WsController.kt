package ru.levin.apps.comparator.app.websocket

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import ru.levin.apps.comparator.api.v1.models.*
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.ComparatorCommand
import ru.levin.apps.comparator.common.models.ComparatorState
import ru.levin.apps.comparator.mappers.v1.fromTransport
import ru.levin.apps.comparator.mappers.v1.toTransportProduct
import ru.levin.apps.comparator.stubs.ComparatorProductStub

private val objectMapper = jacksonObjectMapper().apply {
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}

fun Route.wsProduct() {
    webSocket("/v1/product") {
        for (frame in incoming) {
            if (frame !is Frame.Text) continue

            val text = frame.readText()

            // Ручной разбор requestType — не зависим от аннотаций генератора
            val jsonNode = objectMapper.readTree(text)
            val requestType = jsonNode.get("requestType")?.asText()

            val request: IRequest = when (requestType) {
                "productCreate" -> objectMapper.treeToValue(jsonNode, ProductCreateRequest::class.java)
                "productRead"   -> objectMapper.treeToValue(jsonNode, ProductReadRequest::class.java)
                "productUpdate" -> objectMapper.treeToValue(jsonNode, ProductUpdateRequest::class.java)
                "productDelete" -> objectMapper.treeToValue(jsonNode, ProductDeleteRequest::class.java)
                "productSearch" -> objectMapper.treeToValue(jsonNode, ProductSearchRequest::class.java)
                else -> {
                    val err = """{"error":"Unknown requestType: $requestType"}"""
                    send(Frame.Text(err))
                    continue
                }
            }

            val context = ComparatorContext()
            context.fromTransport(request)

            // Заглушки
            when (context.command) {
                ComparatorCommand.SEARCH -> {
                    context.productsResponse = ComparatorProductStub.prepareSearchList(
                        filter = context.productFilterRequest.searchString,
                        category = context.productFilterRequest.category,
                    ).toMutableList()
                }
                else -> {
                    context.productResponse = ComparatorProductStub.get()
                }
            }
            context.state = ComparatorState.FINISHING

            val response = context.toTransportProduct()
            send(Frame.Text(objectMapper.writeValueAsString(response)))
        }
    }
}