package ru.levin.apps.comparator.common.repo

import ru.levin.apps.comparator.common.models.ComparatorError
import ru.levin.apps.comparator.common.models.ComparatorProduct

data class DbProductsResponse(
    val data: List<ComparatorProduct>? = null,
    val isSuccess: Boolean,
    val errors: List<ComparatorError> = emptyList(),
) {
    companion object {
        fun success(data: List<ComparatorProduct>) = DbProductsResponse(data = data, isSuccess = true)
        fun error(error: ComparatorError) = DbProductsResponse(isSuccess = false, errors = listOf(error))
        fun error(errors: List<ComparatorError>) = DbProductsResponse(isSuccess = false, errors = errors)
    }
}