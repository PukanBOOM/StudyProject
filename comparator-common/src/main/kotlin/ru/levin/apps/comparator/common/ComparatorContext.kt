package ru.levin.apps.comparator.common

import ru.levin.apps.comparator.common.models.*
import ru.levin.apps.comparator.common.repo.IProductRepository
import ru.levin.apps.comparator.common.stubs.ComparatorStubs
import java.time.Instant

data class ComparatorContext(
    var command: ComparatorCommand = ComparatorCommand.NONE,
    var state: ComparatorState = ComparatorState.NONE,
    var errors: MutableList<ComparatorError> = mutableListOf(),

    var corSettings: ComparatorCorSettings = ComparatorCorSettings(),
    var workMode: ComparatorWorkMode = ComparatorWorkMode.PROD,
    var stubCase: ComparatorStubs = ComparatorStubs.NONE,

    var productRepo: IProductRepository = IProductRepository.NONE,

    var requestId: ComparatorRequestId = ComparatorRequestId.NONE,
    var timeStart: Instant = Instant.MIN,

    var productRequest: ComparatorProduct = ComparatorProduct(),
    var productFilterRequest: ComparatorProductFilter = ComparatorProductFilter(),

    var productValidating: ComparatorProduct = ComparatorProduct(),
    var productFilterValidating: ComparatorProductFilter = ComparatorProductFilter(),

    var productResponse: ComparatorProduct = ComparatorProduct(),
    var productsResponse: MutableList<ComparatorProduct> = mutableListOf(),
)