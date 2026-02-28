package ru.levin.apps.comparator.common.models

@JvmInline
value class ComparatorRequestId(private val id: String) {
    fun asString() = id

    companion object {
        val NONE = ComparatorRequestId("")
    }
}