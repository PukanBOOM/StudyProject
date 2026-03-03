package ru.levin.apps.comparator.biz.stub

import kotlinx.coroutines.runBlocking
import ru.levin.apps.comparator.biz.ComparatorProductProcessor
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.*
import ru.levin.apps.comparator.common.stubs.ComparatorStubs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductUpdateStubTest {

    private val processor = ComparatorProductProcessor()

    @Test
    fun `update success`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.UPDATE,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.SUCCESS,
            productRequest = ComparatorProduct(
                id = ComparatorProductId("prod-1"),
                name = "Updated",
                description = "Updated desc",
                category = ComparatorProductCategory.ELECTRONICS,
                lock = ComparatorProductLock("lock-1"),
            ),
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FINISHING, ctx.state)
        assertTrue(ctx.errors.isEmpty())
    }

    @Test
    fun `update bad id`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.UPDATE,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.BAD_ID,
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertEquals("id", ctx.errors.first().field)
    }

    @Test
    fun `update bad title`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.UPDATE,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.BAD_TITLE,
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertEquals("name", ctx.errors.first().field)
    }

    @Test
    fun `update bad description`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.UPDATE,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.BAD_DESCRIPTION,
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertEquals("description", ctx.errors.first().field)
    }

    @Test
    fun `update bad category`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.UPDATE,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.BAD_CATEGORY,
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertEquals("category", ctx.errors.first().field)
    }
}