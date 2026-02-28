package ru.levin.apps.comparator.mappers.v1

import ru.levin.apps.comparator.api.v1.models.*
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.*
import kotlin.test.Test
import kotlin.test.assertEquals

class MapperReadTest {

    @Test
    fun `fromTransport - ProductReadRequest`() {
        val request = ProductReadRequest(
            requestId = "req-2",
            product = ProductReadObject(id = "prod-1"),
            debug = ProductDebug(mode = RequestDebugMode.STUB, stub = RequestDebugStubs.SUCCESS),
        )

        val context = ComparatorContext()
        context.fromTransport(request)

        assertEquals(ComparatorCommand.READ, context.command)
        assertEquals("req-2", context.requestId.asString())
        assertEquals("prod-1", context.productRequest.id.asString())
    }

    @Test
    fun `toTransport - ProductReadResponse`() {
        val context = ComparatorContext(
            command = ComparatorCommand.READ,
            state = ComparatorState.FINISHING,
            requestId = ComparatorRequestId("req-2"),
            productResponse = ComparatorProduct(
                id = ComparatorProductId("prod-1"),
                name = "iPhone 15",
                description = "Apple iPhone 15 128GB",
                category = ComparatorProductCategory.ELECTRONICS,
            ),
        )

        val response = context.toTransportProductRead()

        assertEquals("req-2", response.requestId)
        assertEquals(ResponseResult.SUCCESS, response.result)
        assertEquals("prod-1", response.product?.id)
        assertEquals("iPhone 15", response.product?.name)
    }
}