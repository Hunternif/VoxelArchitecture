package hunternif.voxarch.vector

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Array2DTest {
    @Test
    fun `init & get Int`() {
        val a = Array2D(2, 2) { x, y -> x*10 + y}
        assertEquals(0, a[0, 0])
        assertEquals(10, a[1, 0])
        assertEquals(1, a[0, 1])
        assertEquals(11, a[1, 1])
    }

    @Test
    fun `init & get String`() {
        val a = Array2D(2, 2) { x, y -> "lol ${x*10 + y}" }
        assertEquals("lol 0", a[0, 0])
        assertEquals("lol 10", a[1, 0])
        assertEquals("lol 1", a[0, 1])
        assertEquals("lol 11", a[1, 1])
    }

    @Test
    fun `init & get by IntVec2`() {
        val a = Array2D(2, 2) { x, y -> x*10 + y}
        assertEquals(0, a[IntVec2(0, 0)])
        assertEquals(10, a[IntVec2(1, 0)])
        assertEquals(1, a[IntVec2(0, 1)])
        assertEquals(11, a[IntVec2(1, 1)])
    }

    @Test
    fun `set & get`() {
        val a = Array2D(2, 2, 0)
        a[1, 1] = 4
        assertEquals(4, a[1,1])
    }

    @Test
    fun `in`() {
        val a = Array2D(2, 2, 0)
        assertTrue(IntVec2(0, 0) in a)
        assertTrue(IntVec2(0, 2) !in a)
        assertTrue(IntVec2(-1, -1) !in a)
    }

    @Test
    fun iterate() {
        val a = Array2D(2, 2) { x, y -> x*10 + y}
        val out = mutableListOf<IntVec2>()
        for (p in a) {
            out.add(p)
        }
        assertEquals(IntVec2(0, 0), out[0])
        assertEquals(IntVec2(0, 1), out[1])
        assertEquals(IntVec2(1, 0), out[2])
        assertEquals(IntVec2(1, 1), out[3])
    }

    @Test
    fun `init by list`() {
        val a = Array2D(listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6)
        ))
        assertEquals(1, a[0, 0])
        assertEquals(2, a[1, 0])
        assertEquals(3, a[2, 0])
        assertEquals(4, a[0, 1])
        assertEquals(5, a[1, 1])
        assertEquals(6, a[2, 1])
    }
}