package hunternif.voxarch.world

import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.world.AroundDirection.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class KnapsackTest {
    @Test
    fun `around cw from NW`() {
        val seq = IntVec2(0, 0).aroundCW(NW).iterator()
        assertEquals(IntVec2(-1, -1), seq.next())
        assertEquals(IntVec2(0, -1), seq.next())
        assertEquals(IntVec2(1, -1), seq.next())
        assertEquals(IntVec2(1, 0), seq.next())
        assertEquals(IntVec2(1, 1), seq.next())
        assertEquals(IntVec2(0, 1), seq.next())
        assertEquals(IntVec2(-1, 1), seq.next())
        assertEquals(IntVec2(-1, 0), seq.next())
        assertFalse(seq.hasNext())
    }

    @Test
    fun `around cw from SE`() {
        val seq = IntVec2(0, 0).aroundCW(SE).iterator()
        assertEquals(IntVec2(1, 1), seq.next())
        assertEquals(IntVec2(0, 1), seq.next())
        assertEquals(IntVec2(-1, 1), seq.next())
        assertEquals(IntVec2(-1, 0), seq.next())
        assertEquals(IntVec2(-1, -1), seq.next())
        assertEquals(IntVec2(0, -1), seq.next())
        assertEquals(IntVec2(1, -1), seq.next())
        assertEquals(IntVec2(1, 0), seq.next())
        assertFalse(seq.hasNext())
    }

    @Test
    fun `around ccw from NW`() {
        val seq = IntVec2(0, 0).aroundCCW(NW).iterator()
        assertEquals(IntVec2(-1, -1), seq.next())
        assertEquals(IntVec2(-1, 0), seq.next())
        assertEquals(IntVec2(-1, 1), seq.next())
        assertEquals(IntVec2(0, 1), seq.next())
        assertEquals(IntVec2(1, 1), seq.next())
        assertEquals(IntVec2(1, 0), seq.next())
        assertEquals(IntVec2(1, -1), seq.next())
        assertEquals(IntVec2(0, -1), seq.next())
        assertFalse(seq.hasNext())
    }

    @Test
    fun `around ccw from SW`() {
        val seq = IntVec2(0, 0).aroundCCW(SW).iterator()
        assertEquals(IntVec2(-1, 1), seq.next())
        assertEquals(IntVec2(0, 1), seq.next())
        assertEquals(IntVec2(1, 1), seq.next())
        assertEquals(IntVec2(1, 0), seq.next())
        assertEquals(IntVec2(1, -1), seq.next())
        assertEquals(IntVec2(0, -1), seq.next())
        assertEquals(IntVec2(-1, -1), seq.next())
        assertEquals(IntVec2(-1, 0), seq.next())
        assertFalse(seq.hasNext())
    }

    @Test
    fun `spin cw from NW`() {
        val seq = spinCW(NW).iterator()
        assertEquals(NW, seq.next())
        assertEquals(N, seq.next())
        assertEquals(NE, seq.next())
        assertEquals(E, seq.next())
        assertEquals(SE, seq.next())
        assertEquals(S, seq.next())
        assertEquals(SW, seq.next())
        assertEquals(W, seq.next())
        assertFalse(seq.hasNext())
    }

    @Test
    fun `spin cw from SE`() {
        val seq = spinCW(SE).iterator()
        assertEquals(SE, seq.next())
        assertEquals(S, seq.next())
        assertEquals(SW, seq.next())
        assertEquals(W, seq.next())
        assertEquals(NW, seq.next())
        assertEquals(N, seq.next())
        assertEquals(NE, seq.next())
        assertEquals(E, seq.next())
        assertFalse(seq.hasNext())
    }

    @Test
    fun `direction back-back points to itself`() {
        for (dir in AroundDirection.values()) {
            assertEquals(dir, dir.back().back())
        }
    }
}