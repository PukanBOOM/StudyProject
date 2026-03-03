package ru.levin.apps.comparator.cor

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class CorTest {

    data class TestContext(
        var status: String = "",
        var count: Int = 0,
        var history: MutableList<String> = mutableListOf(),
    )

    @Test
    fun `chain executes workers in order`() = runBlocking {
        val chain = rootChain<TestContext> {
            worker {
                title = "First"
                handle { history.add("first"); count++ }
            }
            worker {
                title = "Second"
                handle { history.add("second"); count++ }
            }
        }

        val ctx = TestContext()
        chain.exec(ctx)

        assertEquals(2, ctx.count)
        assertEquals(listOf("first", "second"), ctx.history)
    }

    @Test
    fun `worker with false on-condition is skipped`() = runBlocking {
        val chain = rootChain<TestContext> {
            worker {
                title = "Skip me"
                on { status == "active" }
                handle { count++ }
            }
            worker {
                title = "Run me"
                handle { count++ }
            }
        }

        val ctx = TestContext(status = "inactive")
        chain.exec(ctx)

        assertEquals(1, ctx.count)
    }

    @Test
    fun `nested chains work correctly`() = runBlocking {
        val chain = rootChain<TestContext> {
            chain {
                title = "Inner chain"
                on { status == "go" }
                worker {
                    title = "Inner worker 1"
                    handle { history.add("inner1") }
                }
                worker {
                    title = "Inner worker 2"
                    handle { history.add("inner2") }
                }
            }
            worker {
                title = "Outer worker"
                handle { history.add("outer") }
            }
        }

        val ctx = TestContext(status = "go")
        chain.exec(ctx)

        assertEquals(listOf("inner1", "inner2", "outer"), ctx.history)
    }

    @Test
    fun `nested chain skipped when on is false`() = runBlocking {
        val chain = rootChain<TestContext> {
            chain {
                title = "Skipped chain"
                on { status == "go" }
                worker {
                    title = "Should not run"
                    handle { history.add("skipped") }
                }
            }
            worker {
                title = "Always runs"
                handle { history.add("always") }
            }
        }

        val ctx = TestContext(status = "stop")
        chain.exec(ctx)

        assertEquals(listOf("always"), ctx.history)
    }

    @Test
    fun `exception handling works`() = runBlocking {
        val chain = rootChain<TestContext> {
            worker {
                title = "Failing worker"
                handle { throw RuntimeException("test error") }
                except { e -> status = "error: ${e.message}" }
            }
        }

        val ctx = TestContext()
        chain.exec(ctx)

        assertEquals("error: test error", ctx.status)
    }

    @Test
    fun `multiple chains with conditions simulate CoR`() = runBlocking {
        val chain = rootChain<TestContext> {
            chain {
                title = "Phase 1"
                on { status != "error" }
                worker {
                    title = "Set running"
                    handle { status = "running"; history.add("phase1") }
                }
            }
            chain {
                title = "Phase 2"
                on { status == "running" }
                worker {
                    title = "Process"
                    handle { count = 42; history.add("phase2") }
                }
            }
            chain {
                title = "Phase 3 - skipped"
                on { status == "error" }
                worker {
                    title = "Error handler"
                    handle { history.add("error-handler") }
                }
            }
        }

        val ctx = TestContext()
        chain.exec(ctx)

        assertEquals("running", ctx.status)
        assertEquals(42, ctx.count)
        assertEquals(listOf("phase1", "phase2"), ctx.history)
    }
}