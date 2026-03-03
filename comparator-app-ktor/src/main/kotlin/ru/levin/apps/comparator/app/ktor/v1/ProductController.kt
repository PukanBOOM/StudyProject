package ru.levin.apps.comparator.app.ktor.v1

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.levin.apps.comparator.api.v1.models.*
import ru.levin.apps.comparator.biz.ComparatorProductProcessor
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.mappers.v1.*

fun Route.v1Product(processor: ComparatorProductProcessor) {

    post("/create") {
        val request = call.receive<ProductCreateRequest>()
        val context = ComparatorContext()
        context.fromTransport(request)
        processor.exec(context)
        call.respond(context.toTransportProductCreate())
    }

    post("/read") {
        val request = call.receive<ProductReadRequest>()
        val context = ComparatorContext()
        context.fromTransport(request)
        processor.exec(context)
        call.respond(context.toTransportProductRead())
    }

    post("/update") {
        val request = call.receive<ProductUpdateRequest>()
        val context = ComparatorContext()
        context.fromTransport(request)
        processor.exec(context)
        call.respond(context.toTransportProductUpdate())
    }

    post("/delete") {
        val request = call.receive<ProductDeleteRequest>()
        val context = ComparatorContext()
        context.fromTransport(request)
        processor.exec(context)
        call.respond(context.toTransportProductDelete())
    }

    post("/search") {
        val request = call.receive<ProductSearchRequest>()
        val context = ComparatorContext()
        context.fromTransport(request)
        processor.exec(context)
        call.respond(context.toTransportProductSearch())
    }
}