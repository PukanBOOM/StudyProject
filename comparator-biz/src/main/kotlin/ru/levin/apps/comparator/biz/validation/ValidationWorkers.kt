package ru.levin.apps.comparator.biz.validation

import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.*
import ru.levin.apps.comparator.cor.CorChainDsl

// ===== SANITIZE (trim) =====

fun CorChainDsl<ComparatorContext>.trimFieldsProduct(title: String) = worker {
    this.title = title
    handle {
        productValidating = productRequest.copy(
            id = ComparatorProductId(productRequest.id.asString().trim()),
            name = productRequest.name.trim(),
            description = productRequest.description.trim(),
            lock = ComparatorProductLock(productRequest.lock.asString().trim()),
        )
    }
}

fun CorChainDsl<ComparatorContext>.trimFieldsSearch(title: String) = worker {
    this.title = title
    handle {
        productFilterValidating = productFilterRequest.copy(
            searchString = productFilterRequest.searchString.trim(),
        )
    }
}

// ===== VALIDATORS =====

fun CorChainDsl<ComparatorContext>.validateNameNotEmpty(title: String) = worker {
    this.title = title
    on { productValidating.name.isEmpty() }
    handle {
        errors.add(
            ComparatorError(
                code = "validation-name-empty",
                group = "validation",
                field = "name",
                message = "Name must not be empty",
            )
        )
    }
}

fun CorChainDsl<ComparatorContext>.validateDescriptionNotEmpty(title: String) = worker {
    this.title = title
    on { productValidating.description.isEmpty() }
    handle {
        errors.add(
            ComparatorError(
                code = "validation-description-empty",
                group = "validation",
                field = "description",
                message = "Description must not be empty",
            )
        )
    }
}

fun CorChainDsl<ComparatorContext>.validateIdNotEmpty(title: String) = worker {
    this.title = title
    on { productValidating.id.asString().isEmpty() }
    handle {
        errors.add(
            ComparatorError(
                code = "validation-id-empty",
                group = "validation",
                field = "id",
                message = "Id must not be empty",
            )
        )
    }
}

fun CorChainDsl<ComparatorContext>.validateIdProperFormat(title: String) = worker {
    this.title = title
    on {
        val id = productValidating.id.asString()
        id.isNotEmpty() && !id.matches(Regex("^[a-zA-Z0-9-]+$"))
    }
    handle {
        errors.add(
            ComparatorError(
                code = "validation-id-format",
                group = "validation",
                field = "id",
                message = "Id must contain only alphanumeric characters and dashes",
            )
        )
    }
}

fun CorChainDsl<ComparatorContext>.validateLockNotEmpty(title: String) = worker {
    this.title = title
    on { productValidating.lock.asString().isEmpty() }
    handle {
        errors.add(
            ComparatorError(
                code = "validation-lock-empty",
                group = "validation",
                field = "lock",
                message = "Lock must not be empty",
            )
        )
    }
}

fun CorChainDsl<ComparatorContext>.validateLockProperFormat(title: String) = worker {
    this.title = title
    on {
        val lock = productValidating.lock.asString()
        lock.isNotEmpty() && !lock.matches(Regex("^[a-zA-Z0-9-]+$"))
    }
    handle {
        errors.add(
            ComparatorError(
                code = "validation-lock-format",
                group = "validation",
                field = "lock",
                message = "Lock must contain only alphanumeric characters and dashes",
            )
        )
    }
}

fun CorChainDsl<ComparatorContext>.validateSearchStringLength(title: String) = worker {
    this.title = title
    on {
        val s = productFilterValidating.searchString
        s.isNotEmpty() && s.length < 3
    }
    handle {
        errors.add(
            ComparatorError(
                code = "validation-search-string-length",
                group = "validation",
                field = "searchString",
                message = "Search string must be at least 3 characters",
            )
        )
    }
}

// ===== FINISH =====

fun CorChainDsl<ComparatorContext>.finishValidation(title: String) = worker {
    this.title = title
    on { errors.isNotEmpty() && state == ComparatorState.RUNNING }
    handle { state = ComparatorState.FAILING }
}