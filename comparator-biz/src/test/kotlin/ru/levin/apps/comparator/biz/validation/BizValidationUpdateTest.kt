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

class BizValidationUpdateTest {

    private val initProduct = ComparatorProduct(
        id = ComparatorProductId("valid-id-123"),
        name = "Original",
        description = "Original desc",
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
        name: String = "Valid Name",
        description: String = "Valid Description",
        lock: String = "valid-lock-123",
    ) = ComparatorContext(
        command = ComparatorCommand.UPDATE,
        workMode = ComparatorWorkMode.TEST,
        productRequest = ComparatorProduct(
            id = ComparatorProductId(id),
            name = name,
            description = description,
            category = ComparatorProductCategory.ELECTRONICS,
            lock = ComparatorProductLock(lock),
        ),
    )

    @Test
    fun `valid update passes validation`() = runBlocking {
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
        val ctx = baseContext(id = "!@#")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertTrue(ctx.errors.any { it.field == "id" && it.code.contains("format") })
    }

    @Test
    fun `empty name fails`() = runBlocking {
        val ctx = baseContext(name = "")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertTrue(ctx.errors.any { it.field == "name" })
    }

    @Test
    fun `empty description fails`() = runBlocking {
        val ctx = baseContext(description = "")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertTrue(ctx.errors.any { it.field == "description" })
    }

    @Test
    fun `empty lock fails`() = runBlocking {
        val ctx = baseContext(lock = "")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertTrue(ctx.errors.any { it.field == "lock" })
    }

    @Test
    fun `multiple validation errors collected`() = runBlocking {
        val ctx = baseContext(id = "", name = "", description = "", lock = "")
        processor.exec(ctx)

        assertEquals(ComparatorState.FAILING, ctx.state)
        assertTrue(ctx.errors.size >= 4)
    }
}