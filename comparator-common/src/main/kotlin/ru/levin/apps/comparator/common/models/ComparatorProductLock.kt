package ru.levin.apps.comparator.common.models

@JvmInline
value class ComparatorProductLock(private val id: String) {
    fun asString() = id

    companion object {
        val NONE = ComparatorProductLock("")
    }
}