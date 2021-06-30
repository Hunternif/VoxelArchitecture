package hunternif.voxarch.sandbox.catle.dsl

import hunternif.voxarch.sandbox.castle.dsl.*
import org.junit.Assert.assertEquals
import org.junit.Test

class DimensionTest {
    @Test
    fun `dimension arithmetic`() {
        assertEquals(3.vx, 1.vx + 2.vx)
        assertEquals(8.vx, 10.vx - 2.vx)
        assertEquals(75.pct, 50.pct + 25.pct)
        assertEquals(75.pct, 100.pct - 25.pct)
        assertEquals(75.pct, 1000.vx - 25.pct)
        assertEquals(75.pct, 100.pct - 250.vx)
        assertEquals(1000.vx, 500.vx + 50.pct)
        assertEquals(1000.vx, 1000.vx to 1000.vx)
        assertEquals(500.vx, 1000.vx / 2.0)
        assertEquals(500.vx, 1000.vx / 2)
        assertEquals(3000.vx, 1000.vx * 3)
        assertEquals(3500.vx, 1000.vx * 3.5)
    }

    companion object {
        private const val base = 1000.0
        private const val seed = 0L

        fun assertEquals(expected: Dimension, actual: Dimension) {
            val expectedVal = expected(base, seed)
            val actualVal = actual(base, seed)
            assertEquals(expectedVal, actualVal, 0.0)
        }
    }
}