package ru.levin.apps.comparator.common.repo

import ru.levin.apps.comparator.common.models.ComparatorError

interface IProductRepository {
    suspend fun createProduct(rq: DbProductRequest): DbProductResponse
    suspend fun readProduct(rq: DbProductIdRequest): DbProductResponse
    suspend fun updateProduct(rq: DbProductRequest): DbProductResponse
    suspend fun deleteProduct(rq: DbProductIdRequest): DbProductResponse
    suspend fun searchProduct(rq: DbProductFilterRequest): DbProductsResponse

    companion object {
        val NONE = object : IProductRepository {
            override suspend fun createProduct(rq: DbProductRequest) = ERR
            override suspend fun readProduct(rq: DbProductIdRequest) = ERR
            override suspend fun updateProduct(rq: DbProductRequest) = ERR
            override suspend fun deleteProduct(rq: DbProductIdRequest) = ERR
            override suspend fun searchProduct(rq: DbProductFilterRequest) = ERRS
        }

        private val ERR = DbProductResponse.error(
            ComparatorError(code = "repo-not-configured", group = "repo", message = "Repository is not configured")
        )
        private val ERRS = DbProductsResponse.error(
            ComparatorError(code = "repo-not-configured", group = "repo", message = "Repository is not configured")
        )
    }
}