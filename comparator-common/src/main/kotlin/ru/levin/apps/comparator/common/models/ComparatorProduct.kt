package ru.levin.apps.comparator.common.models

data class ComparatorProduct(
    var id: ComparatorProductId = ComparatorProductId.NONE,
    var name: String = "",
    var description: String = "",
    var category: ComparatorProductCategory = ComparatorProductCategory.NONE,
    var lock: ComparatorProductLock = ComparatorProductLock.NONE,
    var offers: MutableList<ComparatorOffer> = mutableListOf(),
) {
    fun deepCopy() = copy(
        offers = offers.map { it.copy() }.toMutableList(),
    )
}