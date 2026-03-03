package ru.levin.apps.comparator.mappers.v1

import ru.levin.apps.comparator.api.v1.models.*
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.*
import ru.levin.apps.comparator.common.stubs.ComparatorStubs
import kotlin.test.Test
import kotlin.test.assertEquals

class MapperCreateTest {

    @Test
    fun `fromTransport - ProductCreateRequest`() {
        val request = ProductCreateRequest(
            requestId = "req-1",
            debug = ProductDebug(mode = RequestDebugMode.STUB, stub = RequestDebugStubs.SUCCESS),
            product = ProductCreateObject(
                name = "iPhone 15",
                description = "Apple iPhone 15 128GB",
                category = ProductCategory.ELECTRONICS,
            ),
        )

        val context = ComparatorContext()
        context.fromTransport(request)

        assertEquals(ComparatorCommand.CREATE, context.command)
        assertEquals("req-1", context.requestId.asString())
        assertEquals(ComparatorWorkMode.STUB, context.workMode)
        assertEquals(ComparatorStubs.SUCCESS, context.stubCase)
        assertEquals("iPhone 15", context.productRequest.name)
        assertEquals("Apple iPhone 15 128GB", context.productRequest.description)
        assertEquals(ComparatorProductCategory.ELECTRONICS, context.productRequest.category)
    }

    @Test
    fun `toTransport - ProductCreateResponse`() {
        val context = ComparatorContext(
            command = ComparatorCommand.CREATE,
            state = ComparatorState.FINISHING,
            requestId = ComparatorRequestId("req-1"),
            productResponse = ComparatorProduct(
                id = ComparatorProductId("prod-1"),
                name = "iPhone 15",
                description = "Apple iPhone 15 128GB",
                category = ComparatorProductCategory.ELECTRONICS,
                lock = ComparatorProductLock("lock-1"),
                offers = mutableListOf(
                    ComparatorOffer(shopName = "DNS", price = 89990.0, url = "https://dns-shop.ru/iphone15"),
                ),
            ),
        )

        val response = context.toTransportProductCreate()

        assertEquals("req-1", response.requestId)
        assertEquals(ResponseResult.SUCCESS, response.result)
        assertEquals("prod-1", response.product?.id)
        assertEquals("iPhone 15", response.product?.name)
        assertEquals(ProductCategory.ELECTRONICS, response.product?.category)
        assertEquals("lock-1", response.product?.lock)
        assertEquals(1, response.product?.offers?.size)
        assertEquals("DNS", response.product?.offers?.first()?.shopName)
        assertEquals(89990.0, response.product?.offers?.first()?.price)
    }
}