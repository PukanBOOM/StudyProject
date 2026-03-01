package ru.levin.apps.comparator.common

import ru.levin.apps.comparator.common.repo.IProductRepository

data class ComparatorCorSettings(
    val repoStub: IProductRepository = IProductRepository.NONE,
    val repoTest: IProductRepository = IProductRepository.NONE,
    val repoProd: IProductRepository = IProductRepository.NONE,
)