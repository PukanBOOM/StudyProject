package ru.levin.apps.comparator.biz.stub

import kotlinx.coroutines.runBlocking
import ru.levin.apps.comparator.biz.ComparatorProductProcessor
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.*
import ru.levin.apps.comparator.common.stubs.ComparatorStubs
import ru.levin.apps.comparator.stubs.ComparatorProductStub
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductReadStubTest {

    private val processor = ComparatorProductProcessor()

    @Test
    fun `read success`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.READ,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.SUCCESS,
            productRequest = ComparatorProduct(id = ComparatorProductId("prod-1")),
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FINISHING, ctx.state)
        assertTrue(ctx.errors.isEmpty())
        assertEquals(ComparatorProductStub.get().id, ctx.productResponse.id)
    }

    @Test
    fun `read not found`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.READ,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.NOT_FOUND,
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertEquals("id", ctx.errors.first().field)
    }

    @Test
    fun `read bad id`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.READ,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.BAD_ID,
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertEquals("id", ctx.errors.first().field)
    }
}