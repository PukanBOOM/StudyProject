package ru.levin.apps.comparator.biz.repo

import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.ComparatorState
import ru.levin.apps.comparator.common.models.ComparatorWorkMode
import ru.levin.apps.comparator.common.repo.DbProductFilterRequest
import ru.levin.apps.comparator.common.repo.DbProductIdRequest
import ru.levin.apps.comparator.common.repo.DbProductRequest
import ru.levin.apps.comparator.cor.CorChainDsl

fun CorChainDsl<ComparatorContext>.initRepo(title: String) = worker {
    this.title = title
    on { state == ComparatorState.RUNNING }
    handle {
        productRepo = when (workMode) {
            ComparatorWorkMode.TEST -> corSettings.repoTest
            ComparatorWorkMode.STUB -> corSettings.repoStub
            ComparatorWorkMode.PROD -> corSettings.repoProd
        }
    }
}

fun CorChainDsl<ComparatorContext>.repoOps(
    title: String,
    block: CorChainDsl<ComparatorContext>.() -> Unit,
) = chain {
    this.title = title
    on { state == ComparatorState.RUNNING && workMode != ComparatorWorkMode.STUB }
    block()
}

fun CorChainDsl<ComparatorContext>.repoCreate(title: String) = worker {
    this.title = title
    on { state == ComparatorState.RUNNING }
    handle {
        val result = productRepo.createProduct(DbProductRequest(productValidating))
        val data = result.data
        if (result.isSuccess && data != null) {
            productResponse = data
        } else {
            state = ComparatorState.FAILING
            errors.addAll(result.errors)
        }
    }
}

fun CorChainDsl<ComparatorContext>.repoRead(title: String) = worker {
    this.title = title
    on { state == ComparatorState.RUNNING }
    handle {
        val result = productRepo.readProduct(DbProductIdRequest(productValidating.id))
        val data = result.data
        if (result.isSuccess && data != null) {
            productResponse = data
        } else {
            state = ComparatorState.FAILING
            errors.addAll(result.errors)
        }
    }
}

fun CorChainDsl<ComparatorContext>.repoUpdate(title: String) = worker {
    this.title = title
    on { state == ComparatorState.RUNNING }
    handle {
        val result = productRepo.updateProduct(DbProductRequest(productValidating))
        val data = result.data
        if (result.isSuccess && data != null) {
            productResponse = data
        } else {
            state = ComparatorState.FAILING
            errors.addAll(result.errors)
        }
    }
}

fun CorChainDsl<ComparatorContext>.repoDelete(title: String) = worker {
    this.title = title
    on { state == ComparatorState.RUNNING }
    handle {
        val result = productRepo.deleteProduct(
            DbProductIdRequest(
                id = productValidating.id,
                lock = productValidating.lock,
            )
        )
        val data = result.data
        if (result.isSuccess && data != null) {
            productResponse = data
        } else {
            state = ComparatorState.FAILING
            errors.addAll(result.errors)
        }
    }
}

fun CorChainDsl<ComparatorContext>.repoSearch(title: String) = worker {
    this.title = title
    on { state == ComparatorState.RUNNING }
    handle {
        val result = productRepo.searchProduct(
            DbProductFilterRequest(
                searchString = productFilterValidating.searchString,
                category = productFilterValidating.category,
            )
        )
        val data = result.data
        if (result.isSuccess && data != null) {
            productsResponse = data.toMutableList()
        } else {
            state = ComparatorState.FAILING
            errors.addAll(result.errors)
        }
    }
}