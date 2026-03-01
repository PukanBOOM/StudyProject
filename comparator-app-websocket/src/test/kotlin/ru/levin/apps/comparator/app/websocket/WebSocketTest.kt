package ru.levin.apps.comparator.app.websocket

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.plugins.websocket.WebSockets as ClientWebSockets
import io.ktor.client.plugins.websocket.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import ru.levin.apps.comparator.api.v1.models.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WebSocketTest {

    private val mapper = jacksonObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    // ===== CREATE =====

    @Test
    fun `create product via websocket`() = testApplication {
        application { wsModule() }
        val client = createClient { install(ClientWebSockets) }

        client.webSocket("/v1/product") {
            val request = ProductCreateRequest(
                requestType = "productCreate",
                requestId = "ws-create-1",
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
            send(Frame.Text(mapper.writeValueAsString(request)))

            val responseFrame = incoming.receive() as Frame.Text
            val body = mapper.readValue<ProductCreateResponse>(responseFrame.readText())

            assertEquals("ws-create-1", body.requestId)
            assertEquals(ResponseResult.SUCCESS, body.result)
            assertNotNull(body.product)
            assertEquals("prod-666", body.product?.id)
            assertEquals("iPhone 15", body.product?.name)
            assertTrue((body.product?.offers?.size ?: 0) > 0)
        }
    }

    // ===== READ =====

    @Test
    fun `read product via websocket`() = testApplication {
        application { wsModule() }
        val client = createClient { install(ClientWebSockets) }

        client.webSocket("/v1/product") {
            val request = ProductReadRequest(
                requestType = "productRead",
                requestId = "ws-read-1",
                product = ProductReadObject(id = "prod-666"),
                debug = ProductDebug(
                    mode = RequestDebugMode.STUB,
                    stub = RequestDebugStubs.SUCCESS,
                ),
            )
            send(Frame.Text(mapper.writeValueAsString(request)))

            val responseFrame = incoming.receive() as Frame.Text
            val body = mapper.readValue<ProductReadResponse>(responseFrame.readText())

            assertEquals("ws-read-1", body.requestId)
            assertEquals(ResponseResult.SUCCESS, body.result)
            assertEquals("prod-666", body.product?.id)
            assertEquals("iPhone 15", body.product?.name)
            assertEquals(ProductCategory.ELECTRONICS, body.product?.category)
        }
    }

    // ===== UPDATE =====

    @Test
    fun `update product via websocket`() = testApplication {
        application { wsModule() }
        val client = createClient { install(ClientWebSockets) }

        client.webSocket("/v1/product") {
            val request = ProductUpdateRequest(
                requestType = "productUpdate",
                requestId = "ws-update-1",
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
            send(Frame.Text(mapper.writeValueAsString(request)))

            val responseFrame = incoming.receive() as Frame.Text
            val body = mapper.readValue<ProductUpdateResponse>(responseFrame.readText())

            assertEquals("ws-update-1", body.requestId)
            assertEquals(ResponseResult.SUCCESS, body.result)
            assertNotNull(body.product)
            assertEquals("prod-666", body.product?.id)
        }
    }

    // ===== DELETE =====

    @Test
    fun `delete product via websocket`() = testApplication {
        application { wsModule() }
        val client = createClient { install(ClientWebSockets) }

        client.webSocket("/v1/product") {
            val request = ProductDeleteRequest(
                requestType = "productDelete",
                requestId = "ws-delete-1",
                product = ProductDeleteObject(
                    id = "prod-666",
                    lock = "lock-123",
                ),
                debug = ProductDebug(
                    mode = RequestDebugMode.STUB,
                    stub = RequestDebugStubs.SUCCESS,
                ),
            )
            send(Frame.Text(mapper.writeValueAsString(request)))

            val responseFrame = incoming.receive() as Frame.Text
            val body = mapper.readValue<ProductDeleteResponse>(responseFrame.readText())

            assertEquals("ws-delete-1", body.requestId)
            assertEquals(ResponseResult.SUCCESS, body.result)
            assertEquals("prod-666", body.product?.id)
        }
    }

    // ===== SEARCH =====

    @Test
    fun `search products via websocket`() = testApplication {
        application { wsModule() }
        val client = createClient { install(ClientWebSockets) }

        client.webSocket("/v1/product") {
            val request = ProductSearchRequest(
                requestType = "productSearch",
                requestId = "ws-search-1",
                productFilter = ProductSearchFilter(
                    searchString = "iPhone",
                    category = ProductCategory.ELECTRONICS,
                ),
                debug = ProductDebug(
                    mode = RequestDebugMode.STUB,
                    stub = RequestDebugStubs.SUCCESS,
                ),
            )
            send(Frame.Text(mapper.writeValueAsString(request)))

            val responseFrame = incoming.receive() as Frame.Text
            val body = mapper.readValue<ProductSearchResponse>(responseFrame.readText())

            assertEquals("ws-search-1", body.requestId)
            assertEquals(ResponseResult.SUCCESS, body.result)
            assertNotNull(body.products)
            assertEquals(2, body.products?.size)
            assertTrue(body.products?.first()?.name?.contains("iPhone") == true)
        }
    }

    // ===== НЕСКОЛЬКО СООБЩЕНИЙ В ОДНОМ СОЕДИНЕНИИ =====

    @Test
    fun `multiple messages in single connection`() = testApplication {
        application { wsModule() }
        val client = createClient { install(ClientWebSockets) }

        client.webSocket("/v1/product") {
            // 1-й запрос — create
            send(Frame.Text(mapper.writeValueAsString(
                ProductCreateRequest(
                    requestType = "productCreate",
                    requestId = "ws-multi-1",
                    product = ProductCreateObject(name = "Test", category = ProductCategory.OTHER),
                )
            )))
            val frame1 = incoming.receive() as Frame.Text
            val resp1 = mapper.readValue<ProductCreateResponse>(frame1.readText())
            assertEquals("ws-multi-1", resp1.requestId)
            assertEquals(ResponseResult.SUCCESS, resp1.result)

            // 2-й запрос — read
            send(Frame.Text(mapper.writeValueAsString(
                ProductReadRequest(
                    requestType = "productRead",
                    requestId = "ws-multi-2",
                    product = ProductReadObject(id = "prod-666"),
                )
            )))
            val frame2 = incoming.receive() as Frame.Text
            val resp2 = mapper.readValue<ProductReadResponse>(frame2.readText())
            assertEquals("ws-multi-2", resp2.requestId)
            assertEquals(ResponseResult.SUCCESS, resp2.result)
        }
    }
}