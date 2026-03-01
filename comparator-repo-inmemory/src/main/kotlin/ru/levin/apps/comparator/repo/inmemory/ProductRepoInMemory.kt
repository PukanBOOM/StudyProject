package ru.levin.apps.comparator.repo.inmemory

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.levin.apps.comparator.common.models.*
import ru.levin.apps.comparator.common.repo.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ProductRepoInMemory(
    initObjects: List<ComparatorProduct> = emptyList(),
) : IProductRepository {

    private val store = ConcurrentHashMap<String, ComparatorProduct>()
    private val mutex = Mutex()

    init {
        initObjects.forEach { product ->
            store[product.id.asString()] = product.deepCopy()
        }
    }

    override suspend fun createProduct(rq: DbProductRequest): DbProductResponse = tryOp {
        mutex.withLock {
            val id = UUID.randomUUID().toString()
            val lock = UUID.randomUUID().toString()
            val product = rq.product.deepCopy().apply {
                this.id = ComparatorProductId(id)
                this.lock = ComparatorProductLock(lock)
            }
            store[id] = product.deepCopy()
            DbProductResponse.success(product)
        }
    }

    override suspend fun readProduct(rq: DbProductIdRequest): DbProductResponse = tryOp {
        val id = rq.id.asString()
        val product = store[id] ?: return@tryOp DbProductResponse.errorNotFound
        DbProductResponse.success(product.deepCopy())
    }

    override suspend fun updateProduct(rq: DbProductRequest): DbProductResponse = tryOp {
        mutex.withLock {
            val id = rq.product.id.asString()
            val existing = store[id] ?: return@withLock DbProductResponse.errorNotFound

            if (existing.lock != rq.product.lock) {
                return@withLock DbProductResponse.errorConcurrency
            }

            val newLock = UUID.randomUUID().toString()
            val updated = rq.product.deepCopy().apply {
                lock = ComparatorProductLock(newLock)
            }
            store[id] = updated.deepCopy()
            DbProductResponse.success(updated)
        }
    }

    override suspend fun deleteProduct(rq: DbProductIdRequest): DbProductResponse = tryOp {
        mutex.withLock {
            val id = rq.id.asString()
            val existing = store[id] ?: return@withLock DbProductResponse.errorNotFound

            if (existing.lock != rq.lock) {
                return@withLock DbProductResponse.errorConcurrency
            }

            store.remove(id)
            DbProductResponse.success(existing.deepCopy())
        }
    }

    override suspend fun searchProduct(rq: DbProductFilterRequest): DbProductsResponse = tryOps {
        val results = store.values.filter { product ->
            (rq.searchString.isBlank() ||
                    product.name.contains(rq.searchString, ignoreCase = true) ||
                    product.description.contains(rq.searchString, ignoreCase = true)) &&
                    (rq.category == ComparatorProductCategory.NONE || product.category == rq.category)
        }
        DbProductsResponse.success(results.map { it.deepCopy() })
    }

    private suspend fun tryOp(block: suspend () -> DbProductResponse): DbProductResponse = try {
        block()
    } catch (e: Exception) {
        DbProductResponse.error(
            ComparatorError(code = "repo-internal", group = "repo", message = e.message ?: "Unknown error")
        )
    }

    private suspend fun tryOps(block: suspend () -> DbProductsResponse): DbProductsResponse = try {
        block()
    } catch (e: Exception) {
        DbProductsResponse.error(
            ComparatorError(code = "repo-internal", group = "repo", message = e.message ?: "Unknown error")
        )
    }
}