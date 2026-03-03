package ru.levin.apps.comparator.biz.validation

import kotlinx.coroutines.runBlocking
import ru.levin.apps.comparator.biz.ComparatorProductProcessor
import ru.levin.apps.comparator.common.ComparatorCorSettings
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.models.*
import ru.levin.apps.comparator.repo.inmemory.ProductRepoInMemory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BizValidationSearchTest {

    private val processor = ComparatorProductProcessor(
        corSettings = ComparatorCorSettings(repoTest = ProductRepoInMemory())
    )

    private fun baseContext(searchString: String = "iPhone search") = ComparatorContext(
        command = ComparatorCommand.SEARCH,
        workMode = ComparatorWorkMode.TEST,
        productFilterRequest = ComparatorProductFilter(
            searchString = searchString,
            category = ComparatorProductCategory.ELECTRONICS,
        ),
    )

    @Test
    fun `valid search passes validation`() = runBlocking {
        val ctx = baseContext()
        processor.exec(ctx)

        assertEquals(ComparatorState.FINISHING, ctx.state)
        assertTrue(ctx.errors.isEmpty())
    }

    @Test
    fun `empty search string passes validation`() = runBlocking {
        val ctx = baseContext(searchString = "")
        processor.exec(ctx)

        assertEquals(ComparatorState.FINISHING, ctx.state)
        assertTrue(ctx.errors.isEmpty())
    }

    @Test
    fun `too short search string fails`() = runBlocking {
        val ctx = baseContext(searchString = "ab")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertTrue(ctx.errors.any { it.field == "searchString" })
    }

    @Test
    fun `search string with 3 chars passes`() = runBlocking {
        val ctx = baseContext(searchString = "abc")
        processor.exec(ctx)

        assertEquals(ComparatorState.FINISHING, ctx.state)
        assertTrue(ctx.errors.isEmpty())
    }
}