package ru.levin.apps.comparator.mappers.v1

import ru.levin.apps.comparator.api.v1.models.*
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.*
import ru.levin.apps.comparator.common.stubs.ComparatorStubs
import ru.levin.apps.comparator.mappers.v1.exceptions.UnknownRequestException

fun ComparatorContext.fromTransport(request: IRequest) = when (request) {
    is ProductCreateRequest -> fromTransport(request)
    is ProductReadRequest   -> fromTransport(request)
    is ProductUpdateRequest -> fromTransport(request)
    is ProductDeleteRequest -> fromTransport(request)
    is ProductSearchRequest -> fromTransport(request)
    else -> throw UnknownRequestException(request::class)
}

private fun ComparatorContext.fromTransport(request: ProductCreateRequest) {
    command = ComparatorCommand.CREATE
    requestId = request.requestId.toRequestId()
    productRequest = request.product?.toInternal() ?: ComparatorProduct()
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

private fun ComparatorContext.fromTransport(request: ProductReadRequest) {
    command = ComparatorCommand.READ
    requestId = request.requestId.toRequestId()
    productRequest = request.product?.toInternal() ?: ComparatorProduct()
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

private fun ComparatorContext.fromTransport(request: ProductUpdateRequest) {
    command = ComparatorCommand.UPDATE
    requestId = request.requestId.toRequestId()
    productRequest = request.product?.toInternal() ?: ComparatorProduct()
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

private fun ComparatorContext.fromTransport(request: ProductDeleteRequest) {
    command = ComparatorCommand.DELETE
    requestId = request.requestId.toRequestId()
    productRequest = request.product?.toInternal() ?: ComparatorProduct()
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

private fun ComparatorContext.fromTransport(request: ProductSearchRequest) {
    command = ComparatorCommand.SEARCH
    requestId = request.requestId.toRequestId()
    productFilterRequest = request.productFilter?.toInternal() ?: ComparatorProductFilter()
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

// ===== Вспомогательные маперы =====

private fun String?.toRequestId() =
    this?.let { ComparatorRequestId(it) } ?: ComparatorRequestId.NONE

private fun String?.toProductId() =
    this?.let { ComparatorProductId(it) } ?: ComparatorProductId.NONE

private fun String?.toProductLock() =
    this?.let { ComparatorProductLock(it) } ?: ComparatorProductLock.NONE

private fun ProductCreateObject.toInternal() = ComparatorProduct(
    name = this.name ?: "",
    description = this.description ?: "",
    category = this.category.fromTransport(),
)

private fun ProductReadObject.toInternal() = ComparatorProduct(
    id = this.id.toProductId(),
)

private fun ProductUpdateObject.toInternal() = ComparatorProduct(
    id = this.id.toProductId(),
    name = this.name ?: "",
    description = this.description ?: "",
    category = this.category.fromTransport(),
    lock = this.lock.toProductLock(),
)

private fun ProductDeleteObject.toInternal() = ComparatorProduct(
    id = this.id.toProductId(),
    lock = this.lock.toProductLock(),
)

private fun ProductSearchFilter.toInternal() = ComparatorProductFilter(
    searchString = this.searchString ?: "",
    category = this.category.fromTransport(),
)

private fun ProductCategory?.fromTransport(): ComparatorProductCategory = when (this) {
    ProductCategory.ELECTRONICS -> ComparatorProductCategory.ELECTRONICS
    ProductCategory.CLOTHING    -> ComparatorProductCategory.CLOTHING
    ProductCategory.FOOD        -> ComparatorProductCategory.FOOD
    ProductCategory.BOOKS       -> ComparatorProductCategory.BOOKS
    ProductCategory.OTHER       -> ComparatorProductCategory.OTHER
    null                        -> ComparatorProductCategory.NONE
}

private fun ProductDebug?.transportToWorkMode(): ComparatorWorkMode = when (this?.mode) {
    RequestDebugMode.PROD -> ComparatorWorkMode.PROD
    RequestDebugMode.TEST -> ComparatorWorkMode.TEST
    RequestDebugMode.STUB -> ComparatorWorkMode.STUB
    null                  -> ComparatorWorkMode.PROD
}

private fun ProductDebug?.transportToStubCase(): ComparatorStubs = when (this?.stub) {
    RequestDebugStubs.SUCCESS           -> ComparatorStubs.SUCCESS
    RequestDebugStubs.NOT_FOUND         -> ComparatorStubs.NOT_FOUND
    RequestDebugStubs.BAD_ID            -> ComparatorStubs.BAD_ID
    RequestDebugStubs.BAD_TITLE         -> ComparatorStubs.BAD_TITLE
    RequestDebugStubs.BAD_DESCRIPTION   -> ComparatorStubs.BAD_DESCRIPTION
    RequestDebugStubs.BAD_CATEGORY      -> ComparatorStubs.BAD_CATEGORY
    RequestDebugStubs.CANNOT_DELETE     -> ComparatorStubs.CANNOT_DELETE
    RequestDebugStubs.BAD_SEARCH_STRING -> ComparatorStubs.BAD_SEARCH_STRING
    null                                -> ComparatorStubs.NONE
}