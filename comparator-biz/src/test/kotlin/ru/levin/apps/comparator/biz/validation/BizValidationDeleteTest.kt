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

class BizValidationDeleteTest {

    private val initProduct = ComparatorProduct(
        id = ComparatorProductId("valid-id-123"),
        name = "Test",
        description = "Test",
        category = ComparatorProductCategory.ELECTRONICS,
        lock = ComparatorProductLock("valid-lock-123"),
    )

    private val processor = ComparatorProductProcessor(
        corSettings = ComparatorCorSettings(
            repoTest = ProductRepoInMemory(initObjects = listOf(initProduct))
        )
    )

    private fun baseContext(
        id: String = "valid-id-123",
        lock: String = "valid-lock-123",
    ) = ComparatorContext(
        command = ComparatorCommand.DELETE,
        workMode = ComparatorWorkMode.TEST,
        productRequest = ComparatorProduct(
            id = ComparatorProductId(id),
            lock = ComparatorProductLock(lock),
        ),
    )

    @Test
    fun `valid delete passes validation`() = runBlocking {
        val ctx = baseContext()
        processor.exec(ctx)

        assertEquals(ComparatorState.FINISHING, ctx.state)
        assertTrue(ctx.errors.isEmpty())
    }

    @Test
    fun `empty id fails`() = runBlocking {
        val ctx = baseContext(id = "")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertTrue(ctx.errors.any { it.field == "id" })
    }

    @Test
    fun `bad format id fails`() = runBlocking {
        val ctx = baseContext(id = "!@#\$%")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertTrue(ctx.errors.any { it.field == "id" })
    }

    @Test
    fun `empty lock fails`() = runBlocking {
        val ctx = baseContext(lock = "")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertTrue(ctx.errors.any { it.field == "lock" })
    }
}