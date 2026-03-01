package ru.levin.apps.comparator.biz.stubs

import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.ComparatorError
import ru.levin.apps.comparator.common.models.ComparatorState
import ru.levin.apps.comparator.common.stubs.ComparatorStubs
import ru.levin.apps.comparator.cor.CorChainDsl
import ru.levin.apps.comparator.stubs.ComparatorProductStub

// ===== SUCCESS =====

fun CorChainDsl<ComparatorContext>.stubCreateSuccess(title: String) = worker {
    this.title = title
    on { stubCase == ComparatorStubs.SUCCESS && state == ComparatorState.RUNNING }
    handle {
        state = ComparatorState.FINISHING
        productResponse = ComparatorProductStub.get()
    }
}

fun CorChainDsl<ComparatorContext>.stubReadSuccess(title: String) = worker {
    this.title = title
    on { stubCase == ComparatorStubs.SUCCESS && state == ComparatorState.RUNNING }
    handle {
        state = ComparatorState.FINISHING
        productResponse = ComparatorProductStub.get()
    }
}

fun CorChainDsl<ComparatorContext>.stubUpdateSuccess(title: String) = worker {
    this.title = title
    on { stubCase == ComparatorStubs.SUCCESS && state == ComparatorState.RUNNING }
    handle {
        state = ComparatorState.FINISHING
        productResponse = ComparatorProductStub.get()
    }
}

fun CorChainDsl<ComparatorContext>.stubDeleteSuccess(title: String) = worker {
    this.title = title
    on { stubCase == ComparatorStubs.SUCCESS && state == ComparatorState.RUNNING }
    handle {
        state = ComparatorState.FINISHING
        productResponse = ComparatorProductStub.get()
    }
}

fun CorChainDsl<ComparatorContext>.stubSearchSuccess(title: String) = worker {
    this.title = title
    on { stubCase == ComparatorStubs.SUCCESS && state == ComparatorState.RUNNING }
    handle {
        state = ComparatorState.FINISHING
        productsResponse = ComparatorProductStub.prepareSearchList(
            filter = productFilterRequest.searchString,
            category = productFilterRequest.category,
        ).toMutableList()
    }
}

// ===== ERRORS =====

fun CorChainDsl<ComparatorContext>.stubNotFound(title: String) = worker {
    this.title = title
    on { stubCase == ComparatorStubs.NOT_FOUND && state == ComparatorState.RUNNING }
    handle {
        state = ComparatorState.FAILING
        errors.add(
            ComparatorError(
                code = "not-found",
                group = "stubs",
                field = "id",
                message = "Product not found",
            )
        )
    }
}

fun CorChainDsl<ComparatorContext>.stubValidationBadId(title: String) = worker {
    this.title = title
    on { stubCase == ComparatorStubs.BAD_ID && state == ComparatorState.RUNNING }
    handle {
        state = ComparatorState.FAILING
        errors.add(
            ComparatorError(
                code = "validation-id",
                group = "validation",
                field = "id",
                message = "Invalid id",
            )
        )
    }
}

fun CorChainDsl<ComparatorContext>.stubValidationBadTitle(title: String) = worker {
    this.title = title
    on { stubCase == ComparatorStubs.BAD_TITLE && state == ComparatorState.RUNNING }
    handle {
        state = ComparatorState.FAILING
        errors.add(
            ComparatorError(
                code = "validation-name",
                group = "validation",
                field = "name",
                message = "Invalid name",
            )
        )
    }
}

fun CorChainDsl<ComparatorContext>.stubValidationBadDescription(title: String) = worker {
    this.title = title
    on { stubCase == ComparatorStubs.BAD_DESCRIPTION && state == ComparatorState.RUNNING }
    handle {
        state = ComparatorState.FAILING
        errors.add(
            ComparatorError(
                code = "validation-description",
                group = "validation",
                field = "description",
                message = "Invalid description",
            )
        )
    }
}

fun CorChainDsl<ComparatorContext>.stubValidationBadCategory(title: String) = worker {
    this.title = title
    on { stubCase == ComparatorStubs.BAD_CATEGORY && state == ComparatorState.RUNNING }
    handle {
        state = ComparatorState.FAILING
        errors.add(
            ComparatorError(
                code = "validation-category",
                group = "validation",
                field = "category",
                message = "Invalid category",
            )
        )
    }
}

fun CorChainDsl<ComparatorContext>.stubCannotDelete(title: String) = worker {
    this.title = title
    on { stubCase == ComparatorStubs.CANNOT_DELETE && state == ComparatorState.RUNNING }
    handle {
        state = ComparatorState.FAILING
        errors.add(
            ComparatorError(
                code = "cannot-delete",
                group = "stubs",
                field = "id",
                message = "Cannot delete product",
            )
        )
    }
}

fun CorChainDsl<ComparatorContext>.stubBadSearchString(title: String) = worker {
    this.title = title
    on { stubCase == ComparatorStubs.BAD_SEARCH_STRING && state == ComparatorState.RUNNING }
    handle {
        state = ComparatorState.FAILING
        errors.add(
            ComparatorError(
                code = "validation-search-string",
                group = "validation",
                field = "searchString",
                message = "Invalid search string",
            )
        )
    }
}