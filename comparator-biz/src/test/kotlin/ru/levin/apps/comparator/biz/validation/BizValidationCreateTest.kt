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

class BizValidationCreateTest {

    private val processor = ComparatorProductProcessor(
        corSettings = ComparatorCorSettings(repoTest = ProductRepoInMemory())
    )

    private fun baseContext(
        name: String = "Valid Name",
        description: String = "Valid Description",
    ) = ComparatorContext(
        command = ComparatorCommand.CREATE,
        workMode = ComparatorWorkMode.TEST,
        productRequest = ComparatorProduct(
            name = name,
            description = description,
            category = ComparatorProductCategory.ELECTRONICS,
        ),
    )

    @Test
    fun `valid create passes validation`() = runBlocking {
        val ctx = baseContext()
        processor.exec(ctx)

        assertEquals(ComparatorState.FINISHING, ctx.state)
        assertTrue(ctx.errors.isEmpty())
    }

    @Test
    fun `empty name fails validation`() = runBlocking {
        val ctx = baseContext(name = "")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertTrue(ctx.errors.any { it.field == "name" })
    }

    @Test
    fun `blank name fails validation`() = runBlocking {
        val ctx = baseContext(name = "   ")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertTrue(ctx.errors.any { it.field == "name" })
    }

    @Test
    fun `empty description fails validation`() = runBlocking {
        val ctx = baseContext(description = "")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertTrue(ctx.errors.any { it.field == "description" })
    }

    @Test
    fun `empty name and description collects both errors`() = runBlocking {
        val ctx = baseContext(name = "", description = "")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertEquals(2, ctx.errors.size)
        assertTrue(ctx.errors.any { it.field == "name" })
        assertTrue(ctx.errors.any { it.field == "description" })
    }
}