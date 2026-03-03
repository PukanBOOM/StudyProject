package ru.levin.apps.comparator.cor

class CorChain<T>(
    override val title: String,
    override val description: String = "",
    private val execs: List<ICorExec<T>> = emptyList(),
    private val blockOn: suspend T.() -> Boolean = { true },
    private val blockExcept: suspend T.(Throwable) -> Unit = {},
) : ICorExec<T> {

    override suspend fun exec(context: T) {
        try {
            if (context.blockOn()) {
                execs.forEach { it.exec(context) }
            }
        } catch (e: Throwable) {
            context.blockExcept(e)
        }
    }
}