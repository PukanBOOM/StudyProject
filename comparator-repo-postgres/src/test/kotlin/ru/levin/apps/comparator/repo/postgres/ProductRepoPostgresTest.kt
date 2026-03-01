package ru.levin.apps.comparator.repo.postgres

import RepoProductTest
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.levin.apps.comparator.common.repo.IProductRepository


@Testcontainers
class ProductRepoPostgresTest : RepoProductTest() {

    override val repo: IProductRepository by lazy { pgRepo }

    companion object {
        @Container
        @JvmStatic
        private val pgContainer = PostgreSQLContainer("postgres:15-alpine")

        private val pgRepo: ProductRepoPostgres by lazy {
            ProductRepoPostgres(
                url = pgContainer.jdbcUrl,
                user = pgContainer.username,
                password = pgContainer.password,
                initObjects = initObjects,
            )
        }
    }
}