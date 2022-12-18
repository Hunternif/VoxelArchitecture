package hunternif.voxarch.util

import org.junit.Assert.*
import org.junit.Test

class CycleCounterTest {
    @Test
    fun `guard against recursion`() {
        var num = 0
        val counter = CycleCounter(20)

        val a = TestNode()
        val b = TestNode()
        a.addChild(b)
        b.addChild(a)

        fun recursiveCode(node: TestNode) {
            counter.guard(node) {
                num++
                node.children.forEach { recursiveCode(it) }
            }
        }

        recursiveCode(a)

        assertEquals(40, num)
        assertEquals(21, counter.cycles[a])
        assertEquals(20, counter.cycles[b])

        counter.clear()

        assertEquals(null, counter.cycles[a])
        assertEquals(null, counter.cycles[b])
    }

    private class TestNode : INested<TestNode> {
        override var parent: TestNode? = null
        override val children = linkedSetOf<TestNode>()
    }
}