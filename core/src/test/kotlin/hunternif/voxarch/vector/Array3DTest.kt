package hunternif.voxarch.vector

import org.junit.Assert.*
import org.junit.Test

class Array3DTest {
    @Test
    fun `init & get Int`() {
        val a = Array3D(2, 2, 2)
            {  x, y, z -> x*100 + y*10 + z }
        assertEquals(0, a[0, 0, 0])
        assertEquals(1, a[0, 0, 1])
        assertEquals(10, a[0, 1, 0])
        assertEquals(100, a[1, 0, 0])
        assertEquals(11, a[0, 1, 1])
        assertEquals(101, a[1, 0, 1])
        assertEquals(110, a[1, 1, 0])
        assertEquals(111, a[1, 1, 1])
    }

    @Test
    fun `init & get by IntVec3`() {
        val a = Array3D(2, 2, 2)
            { x, y, z -> x*100 + y*10 + z }
        assertEquals(0, a[IntVec3(0, 0, 0)])
        assertEquals(1, a[IntVec3(0, 0, 1)])
        assertEquals(10, a[IntVec3(0, 1, 0)])
        assertEquals(100, a[IntVec3(1, 0, 0)])
        assertEquals(11, a[IntVec3(0, 1, 1)])
        assertEquals(101, a[IntVec3(1, 0, 1)])
        assertEquals(110, a[IntVec3(1, 1, 0)])
        assertEquals(111, a[IntVec3(1, 1, 1)])
    }

    @Test
    fun `set & get`() {
        val a = Array3D(2, 2, 2, 0)
        a[1, 1, 1] = 4
        assertEquals(4, a[1, 1, 1])
    }

    @Test
    fun `in`() {
        val a = Array3D(2, 2, 2, 0)
        assertTrue(IntVec3(0, 0, 1) in a)
        assertTrue(IntVec3(0, 2, 0) !in a)
        assertTrue(IntVec3(-1, -1, 0) !in a)
    }

    @Test
    fun iterate() {
        val a = Array3D(2, 2, 2)
            { x, y, z -> x*100 + y*10 + z }
        val out = mutableListOf<IntVec3>()
        for (p in a) {
            out.add(p)
        }
        assertEquals(IntVec3(0, 0, 0), out[0])
        assertEquals(IntVec3(0, 0, 1), out[1])
        assertEquals(IntVec3(0, 1, 0), out[2])
        assertEquals(IntVec3(0, 1, 1), out[3])
        assertEquals(IntVec3(1, 0, 0), out[4])
        assertEquals(IntVec3(1, 0, 1), out[5])
        assertEquals(IntVec3(1, 1, 0), out[6])
        assertEquals(IntVec3(1, 1, 1), out[7])
    }
}