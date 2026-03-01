package ru.levin.apps.comparator.common

import ru.levin.apps.comparator.common.models.*
import ru.levin.apps.comparator.common.stubs.ComparatorStubs
import java.time.Instant

data class ComparatorContext(
    var command: ComparatorCommand = ComparatorCommand.NONE,
    var state: ComparatorState = ComparatorState.NONE,
    var errors: MutableList<ComparatorError> = mutableListOf(),

    var workMode: ComparatorWorkMode = ComparatorWorkMode.PROD,
    var stubCase: ComparatorStubs = ComparatorStubs.NONE,

    var requestId: ComparatorRequestId = ComparatorRequestId.NONE,
    var timeStart: Instant = Instant.MIN,

    // Входные данные запроса
    var productRequest: ComparatorProduct = ComparatorProduct(),
    var productFilterRequest: ComparatorProductFilter = ComparatorProductFilter(),

    // Данные после валидации (санитизированные копии)
    var productValidating: ComparatorProduct = ComparatorProduct(),
    var productFilterValidating: ComparatorProductFilter = ComparatorProductFilter(),

    // Результат обработки
    var productResponse: ComparatorProduct = ComparatorProduct(),
    var productsResponse: MutableList<ComparatorProduct> = mutableListOf(),
)