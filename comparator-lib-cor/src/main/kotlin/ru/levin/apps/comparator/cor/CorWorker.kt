package ru.levin.apps.comparator.cor

class CorWorker<T>(
    override val title: String,
    override val description: String = "",
    private val blockOn: suspend T.() -> Boolean = { true },
    private val blockHandle: suspend T.() -> Unit = {},
    private val blockExcept: suspend T.(Throwable) -> Unit = {},
) : ICorExec<T> {

    override suspend fun exec(context: T) {
        try {
            if (context.blockOn()) {
                context.blockHandle()
            }
        } catch (e: Throwable) {
            context.blockExcept(e)
        }
    }
}