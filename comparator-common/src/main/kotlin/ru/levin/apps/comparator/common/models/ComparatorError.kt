package ru.levin.apps.comparator.common.models

data class ComparatorError(
    val code: String = "",
    val group: String = "",
    val field: String = "",
    val message: String = "",
)