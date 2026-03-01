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

class BizValidationReadTest {

    private val initProduct = ComparatorProduct(
        id = ComparatorProductId("valid-id-123"),
        name = "Test",
        description = "Test",
        category = ComparatorProductCategory.ELECTRONICS,
        lock = ComparatorProductLock("lock-1"),
    )

    private val processor = ComparatorProductProcessor(
        corSettings = ComparatorCorSettings(
            repoTest = ProductRepoInMemory(initObjects = listOf(initProduct))
        )
    )

    private fun baseContext(id: String = "valid-id-123") = ComparatorContext(
        command = ComparatorCommand.READ,
        workMode = ComparatorWorkMode.TEST,
        productRequest = ComparatorProduct(id = ComparatorProductId(id)),
    )

    @Test
    fun `valid read passes validation`() = runBlocking {
        val ctx = baseContext()
        processor.exec(ctx)

        assertEquals(ComparatorState.FINISHING, ctx.state)
        assertTrue(ctx.errors.isEmpty())
    }

    @Test
    fun `empty id fails validation`() = runBlocking {
        val ctx = baseContext(id = "")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertTrue(ctx.errors.any { it.field == "id" })
    }

    @Test
    fun `bad format id fails validation`() = runBlocking {
        val ctx = baseContext(id = "!@#\$%^&*()")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertTrue(ctx.errors.any { it.field == "id" && it.code.contains("format") })
    }
}