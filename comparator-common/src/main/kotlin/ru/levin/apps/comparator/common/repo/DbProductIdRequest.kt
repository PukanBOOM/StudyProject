package ru.levin.apps.comparator.common.repo

import ru.levin.apps.comparator.common.models.ComparatorProductId
import ru.levin.apps.comparator.common.models.ComparatorProductLock

data class DbProductIdRequest(
    val id: ComparatorProductId,
    val lock: ComparatorProductLock = ComparatorProductLock.NONE,
)