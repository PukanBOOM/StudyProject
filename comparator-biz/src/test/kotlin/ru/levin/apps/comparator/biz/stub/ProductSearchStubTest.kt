package ru.levin.apps.comparator.biz.stub

import kotlinx.coroutines.runBlocking
import ru.levin.apps.comparator.biz.ComparatorProductProcessor
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.*
import ru.levin.apps.comparator.common.stubs.ComparatorStubs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductSearchStubTest {

    private val processor = ComparatorProductProcessor()

    @Test
    fun `search success`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.SEARCH,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.SUCCESS,
            productFilterRequest = ComparatorProductFilter(
                searchString = "iPhone",
                category = ComparatorProductCategory.ELECTRONICS,
            ),
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FINISHING, ctx.state)
        assertTrue(ctx.errors.isEmpty())
        assertEquals(2, ctx.productsResponse.size)
        assertTrue(ctx.productsResponse.first().name.contains("iPhone"))
    }

    @Test
    fun `search bad search string`() = runBlocking {
        val ctx = ComparatorContext(
            command = ComparatorCommand.SEARCH,
            workMode = ComparatorWorkMode.STUB,
            stubCase = ComparatorStubs.BAD_SEARCH_STRING,
        )
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertEquals("searchString", ctx.errors.first().field)
    }
}