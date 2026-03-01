package ru.levin.apps.comparator.common.repo

import ru.levin.apps.comparator.common.models.ComparatorProductCategory

data class DbProductFilterRequest(
    val searchString: String = "",
    val category: ComparatorProductCategory = ComparatorProductCategory.NONE,
)