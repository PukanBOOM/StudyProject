package ru.levin.apps.comparator.app.ktor

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import ru.levin.apps.comparator.api.v1.models.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProductApiTest {

    // ===== CREATE =====

    @Test
    fun `create product returns stub`() = testApplication {
        application { module() }
        val client = myClient()

        val response = client.post("/v1/product/create") {
            contentType(ContentType.Application.Json)
            setBody(
                ProductCreateRequest(
                    requestId = "req-create-1",
                    product = ProductCreateObject(
                        name = "Samsung Galaxy S24",
                        description = "Flagship phone",
                        category = ProductCategory.ELECTRONICS,
                    ),
                    debug = ProductDebug(
                        mode = RequestDebugMode.STUB,
                        stub = RequestDebugStubs.SUCCESS,
                    ),
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<ProductCreateResponse>()
        assertEquals("req-create-1", body.requestId)
        assertEquals(ResponseResult.SUCCESS, body.result)
        assertNotNull(body.product)
        assertEquals("prod-666", body.product?.id)
        assertEquals("iPhone 15", body.product?.name)
        assertTrue((body.product?.offers?.size ?: 0) > 0)
    }

    // ===== READ =====

    @Test
    fun `read product returns stub`() = testApplication {
        application { module() }
        val client = myClient()

        val response = client.post("/v1/product/read") {
            contentType(ContentType.Application.Json)
            setBody(
                ProductReadRequest(
                    requestId = "req-read-1",
                    product = ProductReadObject(id = "prod-666"),
                    debug = ProductDebug(
                        mode = RequestDebugMode.STUB,
                        stub = RequestDebugStubs.SUCCESS,
                    ),
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<ProductReadResponse>()
        assertEquals("req-read-1", body.requestId)
        assertEquals(ResponseResult.SUCCESS, body.result)
        assertEquals("prod-666", body.product?.id)
        assertEquals("iPhone 15", body.product?.name)
        assertEquals(ProductCategory.ELECTRONICS, body.product?.category)
    }

    // ===== UPDATE =====

    @Test
    fun `update product returns stub`() = testApplication {
        application { module() }
        val client = myClient()

        val response = client.post("/v1/product/update") {
            contentType(ContentType.Application.Json)
            setBody(
                ProductUpdateRequest(
                    requestId = "req-update-1",
                    product = ProductUpdateObject(
                        id = "prod-666",
                        name = "iPhone 15 Pro",
                        description = "Updated description",
                        category = ProductCategory.ELECTRONICS,
                        lock = "lock-old",
                    ),
                    debug = ProductDebug(
                        mode = RequestDebugMode.STUB,
                        stub = RequestDebugStubs.SUCCESS,
                    ),
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<ProductUpdateResponse>()
        assertEquals("req-update-1", body.requestId)
        assertEquals(ResponseResult.SUCCESS, body.result)
        assertNotNull(body.product)
        assertEquals("prod-666", body.product?.id)
    }

    // ===== DELETE =====

    @Test
    fun `delete product returns stub`() = testApplication {
        application { module() }
        val client = myClient()

        val response = client.post("/v1/product/delete") {
            contentType(ContentType.Application.Json)
            setBody(
                ProductDeleteRequest(
                    requestId = "req-delete-1",
                    product = ProductDeleteObject(
                        id = "prod-666",
                        lock = "lock-123",
                    ),
                    debug = ProductDebug(
                        mode = RequestDebugMode.STUB,
                        stub = RequestDebugStubs.SUCCESS,
                    ),
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<ProductDeleteResponse>()
        assertEquals("req-delete-1", body.requestId)
        assertEquals(ResponseResult.SUCCESS, body.result)
        assertEquals("prod-666", body.product?.id)
    }

    // ===== SEARCH =====

    @Test
    fun `search products returns stub list`() = testApplication {
        application { module() }
        val client = myClient()

        val response = client.post("/v1/product/search") {
            contentType(ContentType.Application.Json)
            setBody(
                ProductSearchRequest(
                    requestId = "req-search-1",
                    productFilter = ProductSearchFilter(
                        searchString = "iPhone",
                        category = ProductCategory.ELECTRONICS,
                    ),
                    debug = ProductDebug(
                        mode = RequestDebugMode.STUB,
                        stub = RequestDebugStubs.SUCCESS,
                    ),
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<ProductSearchResponse>()
        assertEquals("req-search-1", body.requestId)
        assertEquals(ResponseResult.SUCCESS, body.result)
        assertNotNull(body.products)
        assertEquals(2, body.products?.size)
        assertTrue(body.products?.first()?.name?.contains("iPhone") == true)
    }

    // ===== Вспомогательный метод =====

    private fun ApplicationTestBuilder.myClient() = createClient {
        install(ContentNegotiation) {
            jackson {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
    }
}