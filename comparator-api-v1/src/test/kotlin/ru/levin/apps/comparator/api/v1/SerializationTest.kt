package ru.levin.apps.comparator.api.v1

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.levin.apps.comparator.api.v1.models.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SerializationTest {

    private val mapper: ObjectMapper = jacksonObjectMapper()

    // ---------- ProductCreateRequest ----------

    @Test
    fun `serialize ProductCreateRequest`() {
        val request = ProductCreateRequest(
            requestId = "req-123",
            product = ProductCreateObject(
                name = "iPhone 15",
                description = "Apple iPhone 15 128GB",
                category = ProductCategory.ELECTRONICS,
            ),
            debug = ProductDebug(
                mode = RequestDebugMode.STUB,
                stub = RequestDebugStubs.SUCCESS,
            ),
        )

        val json = mapper.writeValueAsString(request)

        assertContains(json, "\"requestId\":\"req-123\"")
        assertContains(json, "\"name\":\"iPhone 15\"")
        assertContains(json, "\"category\":\"electronics\"")

        val deserialized = mapper.readValue<ProductCreateRequest>(json)
        assertEquals("req-123", deserialized.requestId)
        assertEquals("iPhone 15", deserialized.product?.name)
        assertEquals(ProductCategory.ELECTRONICS, deserialized.product?.category)
        assertEquals(RequestDebugMode.STUB, deserialized.debug?.mode)
    }

    @Test
    fun `deserialize ProductCreateRequest via IRequest (polymorphic)`() {
        val json = """
        {
            "requestType": "productCreate",
            "requestId": "req-123",
            "product": {
                "name": "Samsung Galaxy S24",
                "description": "Flagship phone",
                "category": "electronics"
            }
        }
        """.trimIndent()

        val request = mapper.readValue<IRequest>(json)
        assertIs<ProductCreateRequest>(request)
        assertEquals("req-123", request.requestId)
        assertEquals("Samsung Galaxy S24", request.product?.name)
        assertEquals(ProductCategory.ELECTRONICS, request.product?.category)
    }

    // ---------- ProductCreateResponse ----------

    @Test
    fun `serialize and deserialize ProductCreateResponse`() {
        val response = ProductCreateResponse(
            requestId = "req-123",
            result = ResponseResult.SUCCESS,
            product = ProductResponseObject(
                id = "prod-1",
                name = "iPhone 15",
                description = "Apple iPhone 15 128GB",
                category = ProductCategory.ELECTRONICS,
                lock = "lock-1",
                offers = listOf(
                    OfferObject(shopName = "DNS", price = 89990.0, url = "https://dns-shop.ru/iphone15"),
                    OfferObject(shopName = "МВидео", price = 92990.0, url = "https://mvideo.ru/iphone15"),
                ),
            ),
        )

        val json = mapper.writeValueAsString(response)
        val deserialized = mapper.readValue<ProductCreateResponse>(json)

        assertEquals("req-123", deserialized.requestId)
        assertEquals(ResponseResult.SUCCESS, deserialized.result)
        assertEquals("prod-1", deserialized.product?.id)
        assertEquals("iPhone 15", deserialized.product?.name)
        assertEquals(2, deserialized.product?.offers?.size)
        assertEquals(89990.0, deserialized.product?.offers?.first()?.price)
        assertEquals("МВидео", deserialized.product?.offers?.get(1)?.shopName)
    }

    // ---------- ProductSearchRequest ----------

    @Test
    fun `deserialize ProductSearchRequest via IRequest`() {
        val json = """
        {
            "requestType": "productSearch",
            "requestId": "req-456",
            "productFilter": {
                "searchString": "iPhone",
                "category": "electronics"
            }
        }
        """.trimIndent()

        val request = mapper.readValue<IRequest>(json)
        assertIs<ProductSearchRequest>(request)
        assertEquals("req-456", request.requestId)
        assertEquals("iPhone", request.productFilter?.searchString)
        assertEquals(ProductCategory.ELECTRONICS, request.productFilter?.category)
    }

    // ---------- Response с ошибками ----------

    @Test
    fun `serialize response with errors`() {
        val response = ProductCreateResponse(
            requestId = "req-err",
            result = ResponseResult.ERROR,
            errors = listOf(
                Error(
                    code = "validation",
                    group = "request",
                    field = "name",
                    message = "Название товара не может быть пустым",
                ),
            ),
        )

        val json = mapper.writeValueAsString(response)
        val deserialized = mapper.readValue<ProductCreateResponse>(json)

        assertEquals(ResponseResult.ERROR, deserialized.result)
        assertEquals(1, deserialized.errors?.size)
        assertEquals("validation", deserialized.errors?.first()?.code)
        assertEquals("name", deserialized.errors?.first()?.field)
    }

    // ---------- ProductSearchResponse (polymorphic) ----------

    @Test
    fun `deserialize ProductSearchResponse via IResponse`() {
        val json = """
        {
            "responseType": "productSearch",
            "requestId": "req-789",
            "result": "success",
            "products": [
                { "id": "p1", "name": "iPhone 15", "category": "electronics" },
                { "id": "p2", "name": "Galaxy S24", "category": "electronics" }
            ]
        }
        """.trimIndent()

        val response = mapper.readValue<IResponse>(json)
        assertIs<ProductSearchResponse>(response)
        assertEquals(ResponseResult.SUCCESS, response.result)
        assertEquals(2, response.products?.size)
        assertEquals("iPhone 15", response.products?.first()?.name)
    }
}