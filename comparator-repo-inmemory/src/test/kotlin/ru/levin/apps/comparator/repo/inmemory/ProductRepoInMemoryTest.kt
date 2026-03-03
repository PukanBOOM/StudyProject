package ru.levin.apps.comparator.repo.inmemory

import RepoProductTest
import ru.levin.apps.comparator.common.repo.IProductRepository


class ProductRepoInMemoryTest : RepoProductTest() {

    override val repo: IProductRepository = ProductRepoInMemory(
        initObjects = initObjects,
    )
}