package ru.levin.apps.comparator.mappers.v1

import ru.levin.apps.comparator.api.v1.models.*
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.*
import kotlin.test.Test
import kotlin.test.assertEquals

class MapperUpdateTest {

    @Test
    fun `fromTransport - ProductUpdateRequest`() {
        val request = ProductUpdateRequest(
            requestId = "req-3",
            product = ProductUpdateObject(
                id = "prod-1",
                name = "iPhone 15 Pro",
                description = "Apple iPhone 15 Pro 256GB",
                category = ProductCategory.ELECTRONICS,
                lock = "lock-1",
            ),
            debug = ProductDebug(mode = RequestDebugMode.STUB, stub = RequestDebugStubs.SUCCESS),
        )

        val context = ComparatorContext()
        context.fromTransport(request)

        assertEquals(ComparatorCommand.UPDATE, context.command)
        assertEquals("req-3", context.requestId.asString())
        assertEquals("prod-1", context.productRequest.id.asString())
        assertEquals("iPhone 15 Pro", context.productRequest.name)
        assertEquals("Apple iPhone 15 Pro 256GB", context.productRequest.description)
        assertEquals(ComparatorProductCategory.ELECTRONICS, context.productRequest.category)
        assertEquals("lock-1", context.productRequest.lock.asString())
    }

    @Test
    fun `toTransport - ProductUpdateResponse`() {
        val context = ComparatorContext(
            command = ComparatorCommand.UPDATE,
            state = ComparatorState.FINISHING,
            requestId = ComparatorRequestId("req-3"),
            productResponse = ComparatorProduct(
                id = ComparatorProductId("prod-1"),
                name = "iPhone 15 Pro",
                description = "Apple iPhone 15 Pro 256GB",
                category = ComparatorProductCategory.ELECTRONICS,
                lock = ComparatorProductLock("lock-2"),
            ),
        )

        val response = context.toTransportProductUpdate()

        assertEquals("req-3", response.requestId)
        assertEquals(ResponseResult.SUCCESS, response.result)
        assertEquals("prod-1", response.product?.id)
        assertEquals("iPhone 15 Pro", response.product?.name)
        assertEquals("lock-2", response.product?.lock)
    }
}