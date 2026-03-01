package ru.levin.apps.comparator.biz.general

import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.ComparatorCommand
import ru.levin.apps.comparator.common.models.ComparatorState
import ru.levin.apps.comparator.common.models.ComparatorWorkMode
import ru.levin.apps.comparator.cor.CorChainDsl

fun CorChainDsl<ComparatorContext>.initStatus(title: String) = worker {
    this.title = title
    on { state == ComparatorState.NONE }
    handle { state = ComparatorState.RUNNING }
}

fun CorChainDsl<ComparatorContext>.operation(
    title: String,
    command: ComparatorCommand,
    block: CorChainDsl<ComparatorContext>.() -> Unit,
) = chain {
    this.title = title
    on { this.command == command && state == ComparatorState.RUNNING }
    block()
}

fun CorChainDsl<ComparatorContext>.stubs(
    title: String,
    block: CorChainDsl<ComparatorContext>.() -> Unit,
) = chain {
    this.title = title
    on { workMode == ComparatorWorkMode.STUB && state == ComparatorState.RUNNING }
    block()
}

fun CorChainDsl<ComparatorContext>.validation(
    title: String,
    block: CorChainDsl<ComparatorContext>.() -> Unit,
) = chain {
    this.title = title
    on { state == ComparatorState.RUNNING }
    block()
}

fun CorChainDsl<ComparatorContext>.prepareResult(title: String) = worker {
    this.title = title
    on { state == ComparatorState.RUNNING }
    handle { state = ComparatorState.FINISHING }
}