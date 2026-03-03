package ru.levin.apps.comparator.mappers.v1

import ru.levin.apps.comparator.api.v1.models.*
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.*
import ru.levin.apps.comparator.mappers.v1.exceptions.UnknownCommandException

fun ComparatorContext.toTransportProduct(): IResponse = when (command) {
    ComparatorCommand.CREATE -> toTransportProductCreate()
    ComparatorCommand.READ   -> toTransportProductRead()
    ComparatorCommand.UPDATE -> toTransportProductUpdate()
    ComparatorCommand.DELETE -> toTransportProductDelete()
    ComparatorCommand.SEARCH -> toTransportProductSearch()
    ComparatorCommand.NONE   -> throw UnknownCommandException(command)
}

fun ComparatorContext.toTransportProductCreate() = ProductCreateResponse(
    requestId = requestId.asString().takeIf { it.isNotBlank() },
    result = state.toResult(),
    errors = errors.toTransportErrors(),
    product = productResponse.toTransportProduct(),
)

fun ComparatorContext.toTransportProductRead() = ProductReadResponse(
    requestId = requestId.asString().takeIf { it.isNotBlank() },
    result = state.toResult(),
    errors = errors.toTransportErrors(),
    product = productResponse.toTransportProduct(),
)

fun ComparatorContext.toTransportProductUpdate() = ProductUpdateResponse(
    requestId = requestId.asString().takeIf { it.isNotBlank() },
    result = state.toResult(),
    errors = errors.toTransportErrors(),
    product = productResponse.toTransportProduct(),
)

fun ComparatorContext.toTransportProductDelete() = ProductDeleteResponse(
    requestId = requestId.asString().takeIf { it.isNotBlank() },
    result = state.toResult(),
    errors = errors.toTransportErrors(),
    product = productResponse.toTransportProduct(),
)

fun ComparatorContext.toTransportProductSearch() = ProductSearchResponse(
    requestId = requestId.asString().takeIf { it.isNotBlank() },
    result = state.toResult(),
    errors = errors.toTransportErrors(),
    products = productsResponse.toTransportProducts(),
)

// ===== Вспомогательные маперы =====

private fun ComparatorProduct.toTransportProduct() = ProductResponseObject(
    id = id.asString().takeIf { it.isNotBlank() },
    name = name.takeIf { it.isNotBlank() },
    description = description.takeIf { it.isNotBlank() },
    category = category.toTransport(),
    lock = lock.asString().takeIf { it.isNotBlank() },
    offers = offers.toTransportOffers(),
)

private fun MutableList<ComparatorProduct>.toTransportProducts(): List<ProductResponseObject>? =
    this.map { it.toTransportProduct() }.takeIf { it.isNotEmpty() }

private fun MutableList<ComparatorOffer>.toTransportOffers(): List<OfferObject>? =
    this.map { it.toTransport() }.takeIf { it.isNotEmpty() }

private fun ComparatorOffer.toTransport() = OfferObject(
    shopName = shopName.takeIf { it.isNotBlank() },
    price = price,
    url = url.takeIf { it.isNotBlank() },
)

private fun ComparatorProductCategory.toTransport(): ProductCategory? = when (this) {
    ComparatorProductCategory.ELECTRONICS -> ProductCategory.ELECTRONICS
    ComparatorProductCategory.CLOTHING    -> ProductCategory.CLOTHING
    ComparatorProductCategory.FOOD        -> ProductCategory.FOOD
    ComparatorProductCategory.BOOKS       -> ProductCategory.BOOKS
    ComparatorProductCategory.OTHER       -> ProductCategory.OTHER
    ComparatorProductCategory.NONE        -> null
}

private fun ComparatorState.toResult(): ResponseResult? = when (this) {
    ComparatorState.FINISHING -> ResponseResult.SUCCESS
    ComparatorState.FAILING   -> ResponseResult.ERROR
    ComparatorState.RUNNING,
    ComparatorState.NONE      -> null
}

private fun MutableList<ComparatorError>.toTransportErrors(): List<Error>? =
    this.map { it.toTransport() }.takeIf { it.isNotEmpty() }

private fun ComparatorError.toTransport() = Error(
    code = code.takeIf { it.isNotBlank() },
    group = group.takeIf { it.isNotBlank() },
    field = field.takeIf { it.isNotBlank() },
    message = message.takeIf { it.isNotBlank() },
)