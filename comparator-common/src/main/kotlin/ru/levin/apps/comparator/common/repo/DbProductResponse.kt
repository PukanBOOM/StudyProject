package ru.levin.apps.comparator.common.repo

import ru.levin.apps.comparator.common.models.ComparatorError
import ru.levin.apps.comparator.common.models.ComparatorProduct

data class DbProductResponse(
    val data: ComparatorProduct? = null,
    val isSuccess: Boolean,
    val errors: List<ComparatorError> = emptyList(),
) {
    companion object {
        fun success(data: ComparatorProduct) = DbProductResponse(data = data, isSuccess = true)
        fun error(error: ComparatorError) = DbProductResponse(isSuccess = false, errors = listOf(error))
        fun error(errors: List<ComparatorError>) = DbProductResponse(isSuccess = false, errors = errors)

        val errorNotFound = error(ComparatorError(
            code = "repo-not-found", group = "repo", field = "id", message = "Object not found"
        ))
        val errorConcurrency = error(ComparatorError(
            code = "repo-concurrency", group = "repo", field = "lock", message = "Object has been changed concurrently"
        ))
    }
}