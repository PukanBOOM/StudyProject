package ru.levin.apps.comparator.app.ktor.v1

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.levin.apps.comparator.api.v1.models.*
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.ComparatorState
import ru.levin.apps.comparator.mappers.v1.*
import ru.levin.apps.comparator.stubs.ComparatorProductStub

fun Route.v1Product() {

    post("/create") {
        val request = call.receive<ProductCreateRequest>()
        val context = ComparatorContext()
        context.fromTransport(request)
        context.productResponse = ComparatorProductStub.get()
        context.state = ComparatorState.FINISHING
        call.respond(context.toTransportProductCreate())
    }

    post("/read") {
        val request = call.receive<ProductReadRequest>()
        val context = ComparatorContext()
        context.fromTransport(request)
        context.productResponse = ComparatorProductStub.get()
        context.state = ComparatorState.FINISHING
        call.respond(context.toTransportProductRead())
    }

    post("/update") {
        val request = call.receive<ProductUpdateRequest>()
        val context = ComparatorContext()
        context.fromTransport(request)
        context.productResponse = ComparatorProductStub.get()
        context.state = ComparatorState.FINISHING
        call.respond(context.toTransportProductUpdate())
    }

    post("/delete") {
        val request = call.receive<ProductDeleteRequest>()
        val context = ComparatorContext()
        context.fromTransport(request)
        context.productResponse = ComparatorProductStub.get()
        context.state = ComparatorState.FINISHING
        call.respond(context.toTransportProductDelete())
    }

    post("/search") {
        val request = call.receive<ProductSearchRequest>()
        val context = ComparatorContext()
        context.fromTransport(request)
        context.productsResponse = ComparatorProductStub.prepareSearchList(
            filter = context.productFilterRequest.searchString,
            category = context.productFilterRequest.category,
        ).toMutableList()
        context.state = ComparatorState.FINISHING
        call.respond(context.toTransportProductSearch())
    }
}