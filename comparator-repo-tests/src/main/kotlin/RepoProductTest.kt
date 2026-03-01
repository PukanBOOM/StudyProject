import kotlinx.coroutines.runBlocking
import ru.levin.apps.comparator.common.models.*
import ru.levin.apps.comparator.common.repo.*
import kotlin.test.*

abstract class RepoProductTest {

    abstract val repo: IProductRepository

    companion object {
        val initProductRead = ComparatorProduct(
            id = ComparatorProductId("read-1"),
            name = "Read Product",
            description = "Product for reading",
            category = ComparatorProductCategory.ELECTRONICS,
            lock = ComparatorProductLock("read-lock-1"),
            offers = mutableListOf(
                ComparatorOffer(shopName = "Shop A", price = 100.0, url = "https://shop-a.com/p1"),
                ComparatorOffer(shopName = "Shop B", price = 120.0, url = "https://shop-b.com/p1"),
            ),
        )

        val initProductUpdate = ComparatorProduct(
            id = ComparatorProductId("update-1"),
            name = "Update Product",
            description = "Product for updating",
            category = ComparatorProductCategory.ELECTRONICS,
            lock = ComparatorProductLock("update-lock-1"),
        )

        val initProductDelete = ComparatorProduct(
            id = ComparatorProductId("delete-1"),
            name = "Delete Product",
            description = "Product for deleting",
            category = ComparatorProductCategory.FOOD,
            lock = ComparatorProductLock("delete-lock-1"),
        )

        val initProductDeleteConcurrency = ComparatorProduct(
            id = ComparatorProductId("delete-conc-1"),
            name = "Delete Concurrency Product",
            description = "Product for concurrency delete test",
            category = ComparatorProductCategory.FOOD,
            lock = ComparatorProductLock("delete-conc-lock-1"),
        )

        val initProductSearch1 = ComparatorProduct(
            id = ComparatorProductId("search-1"),
            name = "iPhone 15 Pro",
            description = "Apple smartphone",
            category = ComparatorProductCategory.ELECTRONICS,
            lock = ComparatorProductLock("search-lock-1"),
        )

        val initProductSearch2 = ComparatorProduct(
            id = ComparatorProductId("search-2"),
            name = "Cotton T-shirt",
            description = "Branded clothing item",
            category = ComparatorProductCategory.CLOTHING,
            lock = ComparatorProductLock("search-lock-2"),
        )

        val initObjects = listOf(
            initProductRead,
            initProductUpdate,
            initProductDelete,
            initProductDeleteConcurrency,
            initProductSearch1,
            initProductSearch2,
        )
    }

    // ===== CREATE =====

    @Test
    fun createSuccess() = runBlocking {
        val product = ComparatorProduct(
            name = "New Product",
            description = "New Description",
            category = ComparatorProductCategory.BOOKS,
            offers = mutableListOf(
                ComparatorOffer(shopName = "TestShop", price = 99.0, url = "https://test.com"),
            ),
        )
        val result = repo.createProduct(DbProductRequest(product))

        assertTrue(result.isSuccess)
        assertNotNull(result.data)
        assertTrue(result.data!!.id.asString().isNotBlank())
        assertTrue(result.data!!.lock.asString().isNotBlank())
        assertEquals("New Product", result.data!!.name)
        assertEquals("New Description", result.data!!.description)
        assertEquals(ComparatorProductCategory.BOOKS, result.data!!.category)
        assertEquals(1, result.data!!.offers.size)
        assertEquals("TestShop", result.data!!.offers.first().shopName)
    }

    // ===== READ =====

    @Test
    fun readSuccess() = runBlocking {
        val result = repo.readProduct(DbProductIdRequest(initProductRead.id))

        assertTrue(result.isSuccess)
        assertNotNull(result.data)
        assertEquals(initProductRead.id, result.data!!.id)
        assertEquals(initProductRead.name, result.data!!.name)
        assertEquals(initProductRead.description, result.data!!.description)
        assertEquals(initProductRead.category, result.data!!.category)
        assertEquals(2, result.data!!.offers.size)
    }

    @Test
    fun readNotFound() = runBlocking {
        val result = repo.readProduct(DbProductIdRequest(ComparatorProductId("nonexistent")))

        assertFalse(result.isSuccess)
        assertNull(result.data)
        assertTrue(result.errors.any { it.code == "repo-not-found" })
    }

    // ===== UPDATE =====

    @Test
    fun updateSuccess() = runBlocking {
        val updated = initProductUpdate.copy(
            name = "Updated Name",
            description = "Updated Description",
            category = ComparatorProductCategory.CLOTHING,
        )
        val result = repo.updateProduct(DbProductRequest(updated))

        assertTrue(result.isSuccess)
        assertNotNull(result.data)
        assertEquals(initProductUpdate.id, result.data!!.id)
        assertEquals("Updated Name", result.data!!.name)
        assertEquals("Updated Description", result.data!!.description)
        assertEquals(ComparatorProductCategory.CLOTHING, result.data!!.category)
        assertNotEquals(initProductUpdate.lock, result.data!!.lock)
    }

    @Test
    fun updateNotFound() = runBlocking {
        val product = ComparatorProduct(
            id = ComparatorProductId("nonexistent"),
            name = "Test",
            description = "Test",
            lock = ComparatorProductLock("some-lock"),
        )
        val result = repo.updateProduct(DbProductRequest(product))

        assertFalse(result.isSuccess)
        assertTrue(result.errors.any { it.code == "repo-not-found" })
    }

    @Test
    fun updateConcurrencyError() = runBlocking {
        val updated = initProductUpdate.copy(
            lock = ComparatorProductLock("wrong-lock"),
            name = "Should Not Update",
        )
        val result = repo.updateProduct(DbProductRequest(updated))

        assertFalse(result.isSuccess)
        assertTrue(result.errors.any { it.code == "repo-concurrency" })
    }

    // ===== DELETE =====

    @Test
    fun deleteSuccess() = runBlocking {
        val result = repo.deleteProduct(
            DbProductIdRequest(
                id = initProductDelete.id,
                lock = initProductDelete.lock,
            )
        )

        assertTrue(result.isSuccess)
        assertNotNull(result.data)
        assertEquals(initProductDelete.id, result.data!!.id)
    }

    @Test
    fun deleteNotFound() = runBlocking {
        val result = repo.deleteProduct(
            DbProductIdRequest(
                id = ComparatorProductId("nonexistent"),
                lock = ComparatorProductLock("some-lock"),
            )
        )

        assertFalse(result.isSuccess)
        assertTrue(result.errors.any { it.code == "repo-not-found" })
    }

    @Test
    fun deleteConcurrencyError() = runBlocking {
        val result = repo.deleteProduct(
            DbProductIdRequest(
                id = initProductDeleteConcurrency.id,
                lock = ComparatorProductLock("wrong-lock"),
            )
        )

        assertFalse(result.isSuccess)
        assertTrue(result.errors.any { it.code == "repo-concurrency" })
    }

    // ===== SEARCH =====

    @Test
    fun searchByString() = runBlocking {
        val result = repo.searchProduct(DbProductFilterRequest(searchString = "iPhone"))

        assertTrue(result.isSuccess)
        assertNotNull(result.data)
        assertTrue(result.data!!.any { it.id == initProductSearch1.id })
        assertTrue(result.data!!.none { it.id == initProductSearch2.id })
    }

    @Test
    fun searchByCategory() = runBlocking {
        val result = repo.searchProduct(
            DbProductFilterRequest(category = ComparatorProductCategory.CLOTHING)
        )

        assertTrue(result.isSuccess)
        assertNotNull(result.data)
        assertTrue(result.data!!.any { it.id == initProductSearch2.id })
        assertTrue(result.data!!.none { it.category != ComparatorProductCategory.CLOTHING })
    }
}