package ru.levin.apps.comparator.stubs

import ru.levin.apps.comparator.common.models.*

object ComparatorProductStub {

    fun get(): ComparatorProduct = ComparatorProduct(
        id = ComparatorProductId("prod-666"),
        name = "iPhone 15",
        description = "Apple iPhone 15 128GB",
        category = ComparatorProductCategory.ELECTRONICS,
        lock = ComparatorProductLock("lock-123"),
        offers = mutableListOf(
            ComparatorOffer(
                shopName = "DNS",
                price = 89990.0,
                url = "https://dns-shop.ru/iphone15",
            ),
            ComparatorOffer(
                shopName = "МВидео",
                price = 92990.0,
                url = "https://mvideo.ru/iphone15",
            ),
        ),
    )

    fun prepareSearchList(filter: String, category: ComparatorProductCategory) = listOf(
        makeProduct("prod-1", "$filter 1", category),
        makeProduct("prod-2", "$filter 2", category),
    )

    private fun makeProduct(
        id: String,
        name: String,
        category: ComparatorProductCategory,
    ) = get().copy(
        id = ComparatorProductId(id),
        name = name,
        category = category,
    )
}