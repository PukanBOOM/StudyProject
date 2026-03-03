package ru.levin.apps.comparator.common.repo

import ru.levin.apps.comparator.common.models.ComparatorProduct

data class DbProductRequest(
    val product: ComparatorProduct,
)