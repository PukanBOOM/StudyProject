package ru.levin.apps.comparator.app.websocket

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import ru.levin.apps.comparator.api.v1.models.*
import ru.levin.apps.comparator.biz.ComparatorProductProcessor
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.mappers.v1.fromTransport
import ru.levin.apps.comparator.mappers.v1.toTransportProduct

private val objectMapper = jacksonObjectMapper().apply {
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}

fun Route.wsProduct(processor: ComparatorProductProcessor) {
    webSocket("/v1/product") {
        for (frame in incoming) {
            if (frame !is Frame.Text) continue

            val text = frame.readText()
            val jsonNode = objectMapper.readTree(text)
            val requestType = jsonNode.get("requestType")?.asText()

            val request: IRequest = when (requestType) {
                "productCreate" -> objectMapper.treeToValue(jsonNode, ProductCreateRequest::class.java)
                "productRead"   -> objectMapper.treeToValue(jsonNode, ProductReadRequest::class.java)
                "productUpdate" -> objectMapper.treeToValue(jsonNode, ProductUpdateRequest::class.java)
                "productDelete" -> objectMapper.treeToValue(jsonNode, ProductDeleteRequest::class.java)
                "productSearch" -> objectMapper.treeToValue(jsonNode, ProductSearchRequest::class.java)
                else -> {
                    send(Frame.Text("""{"error":"Unknown requestType: $requestType"}"""))
                    continue
                }
            }

            val context = ComparatorContext()
            context.fromTransport(request)
            processor.exec(context)

            val response = context.toTransportProduct()
            send(Frame.Text(objectMapper.writeValueAsString(response)))
        }
    }
}