package hunternif.voxarch.dom.style

import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Test

class NumValuesTest {
    @Test
    fun `num value arithmetic`() {
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
        assertEquals(3000.vx, 3 * 1000.vx)
        assertEquals(3500.vx, 3.5 * 1000.vx)
    }

    @Test
    fun `num value isPct`() {
        assertFalse((1.vx + 2.vx).isPct)
        assertTrue((1.vx + 25.pct).isPct)
        assertTrue((10.pct - 25.pct).isPct)
        assertTrue((10.pct * 2).isPct)
        assertTrue((10.pct * 2.5).isPct)
        assertTrue((2 * 10.pct).isPct)
        assertTrue((2.5 * 10.pct).isPct)
        assertFalse((100.vx to 1000.vx).isPct)
        assertTrue((100.vx to 200.pct).isPct)
        assertTrue((10.vx.clamp(1.pct, 5.pct)).isPct)
    }

    @Test
    fun `num value clamp`() {
        assertEquals(5.vx, 10.vx.clamp(1.vx, 5.vx))
        assertEquals(1.vx, 1.vx.clamp(1.vx, 10.vx))
        assertEquals(10.vx, 10.vx.clamp(1.vx, 10.vx))
        assertEquals(20.vx, 10.vx.clamp(20.vx, 25.vx))
        assertEquals(200.vx, 100.vx.clamp(20.pct, 75.pct))
    }

    @Test
    fun `extract coordinates from vec3 value`() {
        val vecValue = set(Vec3(1, 2, 3))
        assertEquals(1.0, vecValue.getX().invoke(0.0, 0), 0.0)
        assertEquals(2.0, vecValue.getY().invoke(0.0, 0), 0.0)
        assertEquals(3.0, vecValue.getZ().invoke(0.0, 0), 0.0)
    }

    companion object {
        private const val base = 1000.0
        private const val seed = 0L

        fun assertEquals(expected: Value<Number>, actual: Value<Number>) {
            val expectedVal = expected(base, seed).toDouble()
            val actualVal = actual(base, seed).toDouble()
            assertEquals(expectedVal, actualVal, 0.0)
        }
    }

    @Test
    fun `num value toString`() {
        assertEquals("(1 + 2.5%)", (1.vx + 2.5.pct).toString())
        assertEquals("(1 / 2 - 3 * min(4, 5))", (1.vx / 2 - 3 * min(4.vx, 5.vx)).toString())
        assertEquals("(1.4 ~ 2.0)", (1.4.vx to 2.0.vx).toString())
    }
}