package ru.levin.apps.comparator.mappers.v1

import ru.levin.apps.comparator.api.v1.models.*
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.*
import kotlin.test.Test
import kotlin.test.assertEquals

class MapperDeleteTest {

    @Test
    fun `fromTransport - ProductDeleteRequest`() {
        val request = ProductDeleteRequest(
            requestId = "req-4",
            product = ProductDeleteObject(id = "prod-1", lock = "lock-1"),
            debug = ProductDebug(mode = RequestDebugMode.STUB, stub = RequestDebugStubs.SUCCESS),
        )

        val context = ComparatorContext()
        context.fromTransport(request)

        assertEquals(ComparatorCommand.DELETE, context.command)
        assertEquals("req-4", context.requestId.asString())
        assertEquals("prod-1", context.productRequest.id.asString())
        assertEquals("lock-1", context.productRequest.lock.asString())
    }

    @Test
    fun `toTransport - ProductDeleteResponse`() {
        val context = ComparatorContext(
            command = ComparatorCommand.DELETE,
            state = ComparatorState.FINISHING,
            requestId = ComparatorRequestId("req-4"),
            productResponse = ComparatorProduct(
                id = ComparatorProductId("prod-1"),
                name = "iPhone 15",
            ),
        )

        val response = context.toTransportProductDelete()

        assertEquals("req-4", response.requestId)
        assertEquals(ResponseResult.SUCCESS, response.result)
        assertEquals("prod-1", response.product?.id)
    }
}