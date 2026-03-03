package ru.levin.apps.comparator.biz.stub

import kotlinx.coroutines.runBlocking
import ru.levin.apps.comparator.biz.ComparatorProductProcessor
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.*
import ru.levin.apps.comparator.common.stubs.ComparatorStubs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductDeleteStubTest {

    private val processor = ComparatorProductProcessor()

    @Test
    fun `delete success`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.DELETE,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.SUCCESS,
            productRequest = ComparatorProduct(
                id = ComparatorProductId("prod-1"),
                lock = ComparatorProductLock("lock-1"),
            ),
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FINISHING, ctx.state)
        assertTrue(ctx.errors.isEmpty())
    }

    @Test
    fun `delete cannot delete`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.DELETE,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.CANNOT_DELETE,
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertEquals("id", ctx.errors.first().field)
    }

    @Test
    fun `delete bad id`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.DELETE,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.BAD_ID,
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertEquals("id", ctx.errors.first().field)
    }
}