package hunternif.voxarch.vector

import hunternif.voxarch.world.expandBox
import org.junit.Assert.assertEquals
import org.junit.Test

class KnapsackTest {
    @Test
    fun `expand simple square`() {
        val area = hashSetOf(
            IntVec2(0, 0),
            IntVec2(1, 0),
            IntVec2(0, 1),
            IntVec2(1, 1)
        )
        val box = area.expandBox()
        assertEquals(IntVec2(0, 0), box.start)
        assertEquals(IntVec2(1, 1), box.end)
    }

    @Test
    fun `expand rectangle`() {
        val area = hashSetOf(
            IntVec2(0, 0),
            IntVec2(1, 0),
            IntVec2(0, 1),
            IntVec2(1, 1),
            IntVec2(0, 2),
            IntVec2(1, 2),
            IntVec2(1, 3)
        )
        val box = area.expandBox()
        assertEquals(IntVec2(0, 0), box.start)
        assertEquals(IntVec2(1, 2), box.end)
    }

    @Test
    fun `expand cross`() {
        val area = hashSetOf(
            IntVec2(-1, -1),
            IntVec2(-1, 0),
            IntVec2(-1, 1),
            IntVec2(0, -1),
            IntVec2(0, 0),
            IntVec2(0, 1),
            IntVec2(1, -1),
            IntVec2(1, 0),
            IntVec2(1, 1),
            IntVec2(0, 2),
            IntVec2(0, -2),
            IntVec2(2, 0),
            IntVec2(-2, 0)
        )
        val box = area.expandBox()
        assertEquals(IntVec2(-1, -1), box.start)
        assertEquals(IntVec2(1, 1), box.end)
    }
}