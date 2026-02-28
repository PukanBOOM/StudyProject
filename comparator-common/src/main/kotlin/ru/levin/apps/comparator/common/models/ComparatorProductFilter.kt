package ru.levin.apps.comparator.common.models

data class ComparatorProductFilter(
    var searchString: String = "",
    var category: ComparatorProductCategory = ComparatorProductCategory.NONE,
)