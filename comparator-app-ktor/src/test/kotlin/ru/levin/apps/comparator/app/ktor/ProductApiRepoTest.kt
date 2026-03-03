package ru.levin.apps.comparator.app.ktor

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import ru.levin.apps.comparator.api.v1.models.*
import ru.levin.apps.comparator.common.ComparatorCorSettings
import ru.levin.apps.comparator.repo.inmemory.ProductRepoInMemory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProductApiRepoTest {

    private fun ApplicationTestBuilder.myClient() = createClient {
        install(ContentNegotiation) {
            jackson {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
    }

    @Test
    fun `full CRUD cycle via REST with repository`() = testApplication {
        application {
            module(ComparatorCorSettings(
                repoTest = ProductRepoInMemory(),
                repoProd = ProductRepoInMemory(),
            ))
        }
        val client = myClient()

        // ===== CREATE =====
        val createResp = client.post("/v1/product/create") {
            contentType(ContentType.Application.Json)
            setBody(ProductCreateRequest(
                requestId = "repo-1",
                product = ProductCreateObject(
                    name = "Gaming Laptop",
                    description = "RTX 4090, 32GB RAM",
                    category = ProductCategory.ELECTRONICS,
                ),
                debug = ProductDebug(mode = RequestDebugMode.TEST),
            ))
        }
        assertEquals(HttpStatusCode.OK, createResp.status)
        val created = createResp.body<ProductCreateResponse>()
        assertEquals(ResponseResult.SUCCESS, created.result)
        assertNotNull(created.product?.id)
        assertNotNull(created.product?.lock)
        assertEquals("Gaming Laptop", created.product?.name)

        val productId = created.product!!.id!!
        val lock1 = created.product!!.lock!!

        // ===== READ =====
        val readResp = client.post("/v1/product/read") {
            contentType(ContentType.Application.Json)
            setBody(ProductReadRequest(
                requestId = "repo-2",
                product = ProductReadObject(id = productId),
                debug = ProductDebug(mode = RequestDebugMode.TEST),
            ))
        }
        val read = readResp.body<ProductReadResponse>()
        assertEquals(ResponseResult.SUCCESS, read.result)
        assertEquals("Gaming Laptop", read.product?.name)
        assertEquals("RTX 4090, 32GB RAM", read.product?.description)

        // ===== UPDATE =====
        val updateResp = client.post("/v1/product/update") {
            contentType(ContentType.Application.Json)
            setBody(ProductUpdateRequest(
                requestId = "repo-3",
                product = ProductUpdateObject(
                    id = productId,
                    name = "Gaming Laptop Pro",
                    description = "RTX 5090, 64GB RAM",
                    category = ProductCategory.ELECTRONICS,
                    lock = lock1,
                ),
                debug = ProductDebug(mode = RequestDebugMode.TEST),
            ))
        }
        val updated = updateResp.body<ProductUpdateResponse>()
        assertEquals(ResponseResult.SUCCESS, updated.result)
        assertEquals("Gaming Laptop Pro", updated.product?.name)
        assertEquals("RTX 5090, 64GB RAM", updated.product?.description)

        val lock2 = updated.product!!.lock!!

        // ===== SEARCH =====
        val searchResp = client.post("/v1/product/search") {
            contentType(ContentType.Application.Json)
            setBody(ProductSearchRequest(
                requestId = "repo-4",
                productFilter = ProductSearchFilter(
                    searchString = "Gaming",
                    category = ProductCategory.ELECTRONICS,
                ),
                debug = ProductDebug(mode = RequestDebugMode.TEST),
            ))
        }
        val searched = searchResp.body<ProductSearchResponse>()
        assertEquals(ResponseResult.SUCCESS, searched.result)
        assertTrue(searched.products?.isNotEmpty() == true)
        assertTrue(searched.products?.any { it.id == productId } == true)

        // ===== DELETE =====
        val deleteResp = client.post("/v1/product/delete") {
            contentType(ContentType.Application.Json)
            setBody(ProductDeleteRequest(
                requestId = "repo-5",
                product = ProductDeleteObject(
                    id = productId,
                    lock = lock2,
                ),
                debug = ProductDebug(mode = RequestDebugMode.TEST),
            ))
        }
        val deleted = deleteResp.body<ProductDeleteResponse>()
        assertEquals(ResponseResult.SUCCESS, deleted.result)
        assertEquals(productId, deleted.product?.id)

        // ===== VERIFY DELETED =====
        val readAgainResp = client.post("/v1/product/read") {
            contentType(ContentType.Application.Json)
            setBody(ProductReadRequest(
                requestId = "repo-6",
                product = ProductReadObject(id = productId),
                debug = ProductDebug(mode = RequestDebugMode.TEST),
            ))
        }
        val readAgain = readAgainResp.body<ProductReadResponse>()
        assertEquals(ResponseResult.ERROR, readAgain.result)
        assertTrue(readAgain.errors?.any { it.code == "repo-not-found" } == true)
    }

    @Test
    fun `update with wrong lock returns concurrency error`() = testApplication {
        application {
            module(ComparatorCorSettings(
                repoTest = ProductRepoInMemory(),
                repoProd = ProductRepoInMemory(),
            ))
        }
        val client = myClient()

        // Create
        val createResp = client.post("/v1/product/create") {
            contentType(ContentType.Application.Json)
            setBody(ProductCreateRequest(
                requestId = "conc-1",
                product = ProductCreateObject(
                    name = "Phone",
                    description = "Smartphone",
                    category = ProductCategory.ELECTRONICS,
                ),
                debug = ProductDebug(mode = RequestDebugMode.TEST),
            ))
        }
        val created = createResp.body<ProductCreateResponse>()
        val productId = created.product!!.id!!

        // Update with wrong lock
        val updateResp = client.post("/v1/product/update") {
            contentType(ContentType.Application.Json)
            setBody(ProductUpdateRequest(
                requestId = "conc-2",
                product = ProductUpdateObject(
                    id = productId,
                    name = "Phone Updated",
                    description = "Updated",
                    category = ProductCategory.ELECTRONICS,
                    lock = "wrong-lock",
                ),
                debug = ProductDebug(mode = RequestDebugMode.TEST),
            ))
        }
        val updated = updateResp.body<ProductUpdateResponse>()
        assertEquals(ResponseResult.ERROR, updated.result)
        assertTrue(updated.errors?.any { it.code == "repo-concurrency" } == true)
    }
}