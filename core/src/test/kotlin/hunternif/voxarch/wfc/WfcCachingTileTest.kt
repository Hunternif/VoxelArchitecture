package hunternif.voxarch.wfc

import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.Direction3D.*
import hunternif.voxarch.wfc.Match.*
import org.junit.Assert.*
import org.junit.Test

class WfcCachingTileTest {
    @Test
    fun `memoize matches`() {
        assertTrue(a.matchesSide(b,  EAST))
        assertEquals(YES, a.memo[EAST]!![b])
        assertEquals(null, a.memo[EAST]!![c])
        assertEquals(null, a.memo[WEST])
        assertEquals(YES, b.memo[WEST]!![a])

        assertFalse(a.matchesSide(c,  EAST))
        assertEquals(NO, a.memo[EAST]!![c])
        assertEquals(NO, c.memo[WEST]!![a])
    }

    class PixelTile(private val name: String): WfcCachingTile() {
        override val probability: Double = 1.0
        override fun calculateMatch(other: WfcTile, dir: Direction3D) =
            matchMap[this]?.contains(other) ?: false
        override fun toString() = name
    }

    companion object {
        private val a = PixelTile("a")
        private val b = PixelTile("b")
        private val c = PixelTile("c")

        private val matchMap = mapOf(
            a to listOf(a, b),
            b to listOf(a, b, c),
            c to listOf(b, c)
        )
    }
}