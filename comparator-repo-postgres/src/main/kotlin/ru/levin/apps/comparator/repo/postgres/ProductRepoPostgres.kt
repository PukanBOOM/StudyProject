package ru.levin.apps.comparator.repo.postgres

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.levin.apps.comparator.common.models.*
import ru.levin.apps.comparator.common.repo.*
import java.util.*

class ProductRepoPostgres(
    url: String,
    user: String,
    password: String,
    initObjects: List<ComparatorProduct> = emptyList(),
) : IProductRepository {

    private val db: Database

    init {
        // 1. HikariCP DataSource (нужен и для Flyway, и для Exposed)
        val hikariDs = HikariDataSource(HikariConfig().apply {
            jdbcUrl = url
            username = user
            this.password = password
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 5
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })

        // 2. Flyway миграции — передаём DataSource напрямую
        Flyway.configure()
            .dataSource(hikariDs)
            .locations("classpath:db/migration")
            .load()
            .migrate()

        // 3. Exposed
        db = Database.connect(hikariDs)

        // 4. Инициализация тестовых данных
        if (initObjects.isNotEmpty()) {
            transaction(db) {
                initObjects.forEach { product -> insertProduct(product) }
            }
        }
    }

    override suspend fun createProduct(rq: DbProductRequest): DbProductResponse = tryOp {
        dbQuery {
            val id = UUID.randomUUID().toString()
            val lock = UUID.randomUUID().toString()
            val product = rq.product.deepCopy().apply {
                this.id = ComparatorProductId(id)
                this.lock = ComparatorProductLock(lock)
            }
            insertProduct(product)
            DbProductResponse.success(fetchProduct(id)!!)
        }
    }

    override suspend fun readProduct(rq: DbProductIdRequest): DbProductResponse = tryOp {
        dbQuery {
            val product = fetchProduct(rq.id.asString())
                ?: return@dbQuery DbProductResponse.errorNotFound
            DbProductResponse.success(product)
        }
    }

    override suspend fun updateProduct(rq: DbProductRequest): DbProductResponse = tryOp {
        dbQuery {
            val id = rq.product.id.asString()
            val existing = fetchProduct(id)
                ?: return@dbQuery DbProductResponse.errorNotFound

            if (existing.lock != rq.product.lock) {
                return@dbQuery DbProductResponse.errorConcurrency
            }

            val newLock = UUID.randomUUID().toString()

            ProductsTable.update({ ProductsTable.id eq id }) {
                it[name] = rq.product.name
                it[description] = rq.product.description
                it[category] = rq.product.category.name
                it[lock] = newLock
            }

            OffersTable.deleteWhere { productId eq id }
            rq.product.offers.forEach { offer ->
                OffersTable.insert {
                    it[OffersTable.id] = UUID.randomUUID().toString()
                    it[productId] = id
                    it[shopName] = offer.shopName
                    it[price] = offer.price
                    it[url] = offer.url
                }
            }

            DbProductResponse.success(fetchProduct(id)!!)
        }
    }

    override suspend fun deleteProduct(rq: DbProductIdRequest): DbProductResponse = tryOp {
        dbQuery {
            val id = rq.id.asString()
            val existing = fetchProduct(id)
                ?: return@dbQuery DbProductResponse.errorNotFound

            if (existing.lock != rq.lock) {
                return@dbQuery DbProductResponse.errorConcurrency
            }

            OffersTable.deleteWhere { productId eq id }
            ProductsTable.deleteWhere { ProductsTable.id eq id }

            DbProductResponse.success(existing)
        }
    }

    override suspend fun searchProduct(rq: DbProductFilterRequest): DbProductsResponse = tryOps {
        dbQuery {
            val query = ProductsTable.selectAll()

            if (rq.searchString.isNotBlank()) {
                query.andWhere {
                    (ProductsTable.name.lowerCase() like "%${rq.searchString.lowercase()}%") or
                            (ProductsTable.description.lowerCase() like "%${rq.searchString.lowercase()}%")
                }
            }

            if (rq.category != ComparatorProductCategory.NONE) {
                query.andWhere {
                    ProductsTable.category eq rq.category.name
                }
            }

            val products = query.map { row ->
                fetchProduct(row[ProductsTable.id])!!
            }

            DbProductsResponse.success(products)
        }
    }

    private fun insertProduct(product: ComparatorProduct) {
        ProductsTable.insert {
            it[id] = product.id.asString()
            it[name] = product.name
            it[description] = product.description
            it[category] = product.category.name
            it[lock] = product.lock.asString()
        }
        product.offers.forEach { offer ->
            OffersTable.insert {
                it[id] = UUID.randomUUID().toString()
                it[productId] = product.id.asString()
                it[shopName] = offer.shopName
                it[price] = offer.price
                it[url] = offer.url
            }
        }
    }

    private fun fetchProduct(id: String): ComparatorProduct? {
        val row = ProductsTable.selectAll()
            .where { ProductsTable.id eq id }
            .singleOrNull() ?: return null

        val offers = OffersTable.selectAll()
            .where { OffersTable.productId eq id }
            .map { offerRow ->
                ComparatorOffer(
                    shopName = offerRow[OffersTable.shopName],
                    price = offerRow[OffersTable.price],
                    url = offerRow[OffersTable.url],
                )
            }

        return ComparatorProduct(
            id = ComparatorProductId(row[ProductsTable.id]),
            name = row[ProductsTable.name],
            description = row[ProductsTable.description],
            category = categoryFromDb(row[ProductsTable.category]),
            lock = ComparatorProductLock(row[ProductsTable.lock]),
            offers = offers.toMutableList(),
        )
    }

    private fun categoryFromDb(value: String): ComparatorProductCategory = try {
        ComparatorProductCategory.valueOf(value)
    } catch (e: IllegalArgumentException) {
        ComparatorProductCategory.NONE
    }

    private suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) { transaction(db) { block() } }

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