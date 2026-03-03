package ru.levin.apps.comparator.cor

@DslMarker
annotation class CorDslMarker

@CorDslMarker
class CorWorkerDsl<T> {
    var title: String = ""
    var description: String = ""
    private var blockOn: suspend T.() -> Boolean = { true }
    private var blockHandle: suspend T.() -> Unit = {}
    private var blockExcept: suspend T.(Throwable) -> Unit = {}

    fun on(block: suspend T.() -> Boolean) { blockOn = block }
    fun handle(block: suspend T.() -> Unit) { blockHandle = block }
    fun except(block: suspend T.(Throwable) -> Unit) { blockExcept = block }

    fun build(): ICorExec<T> = CorWorker(
        title = title,
        description = description,
        blockOn = blockOn,
        blockHandle = blockHandle,
        blockExcept = blockExcept,
    )
}

@CorDslMarker
class CorChainDsl<T> {
    var title: String = ""
    var description: String = ""
    private var blockOn: suspend T.() -> Boolean = { true }
    private var blockExcept: suspend T.(Throwable) -> Unit = {}
    private val workers: MutableList<ICorExec<T>> = mutableListOf()

    fun on(block: suspend T.() -> Boolean) { blockOn = block }
    fun except(block: suspend T.(Throwable) -> Unit) { blockExcept = block }

    fun worker(block: CorWorkerDsl<T>.() -> Unit) {
        workers.add(CorWorkerDsl<T>().apply(block).build())
    }

    fun chain(block: CorChainDsl<T>.() -> Unit) {
        workers.add(CorChainDsl<T>().apply(block).build())
    }

    fun build(): ICorExec<T> = CorChain(
        title = title,
        description = description,
        execs = workers.toList(),
        blockOn = blockOn,
        blockExcept = blockExcept,
    )
}

fun <T> rootChain(block: CorChainDsl<T>.() -> Unit): ICorExec<T> =
    CorChainDsl<T>().apply(block).build()