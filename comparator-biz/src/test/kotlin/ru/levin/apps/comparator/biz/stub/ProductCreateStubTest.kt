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

class ProductCreateStubTest {

    private val processor = ComparatorProductProcessor()
    private val stub = ComparatorProductStub.get()

    @Test
    fun `create success`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.CREATE,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.SUCCESS,
            productRequest = ComparatorProduct(
                name = "Test Product",
                description = "Test Description",
                category = ComparatorProductCategory.ELECTRONICS,
            ),
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FINISHING, ctx.state)
        assertTrue(ctx.errors.isEmpty())
        assertEquals(stub.id, ctx.productResponse.id)
        assertEquals(stub.name, ctx.productResponse.name)
        assertTrue(ctx.productResponse.offers.isNotEmpty())
    }

    @Test
    fun `create bad title`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.CREATE,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.BAD_TITLE,
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertEquals(1, ctx.errors.size)
        assertEquals("name", ctx.errors.first().field)
    }

    @Test
    fun `create bad description`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.CREATE,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.BAD_DESCRIPTION,
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertEquals("description", ctx.errors.first().field)
    }

    @Test
    fun `create bad category`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.CREATE,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.BAD_CATEGORY,
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertEquals("category", ctx.errors.first().field)
    }
}