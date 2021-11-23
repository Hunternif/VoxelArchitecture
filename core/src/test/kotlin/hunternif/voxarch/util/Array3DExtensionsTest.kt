package hunternif.voxarch.util

import hunternif.voxarch.vector.Array3D
import org.junit.Assert.*
import org.junit.Test

class Array3DExtensionsTest {
    @Test
    fun symmetry() {
        val a = Array3D(4, 2, 3, 0)
        assertTrue(a.isSymmetricX())
        assertTrue(a.isSymmetricZ())

        a[0, 0, 0] = 1
        assertFalse(a.isSymmetricX())
        assertFalse(a.isSymmetricZ())

        a[3, 0, 0] = 1
        assertTrue(a.isSymmetricX())
        assertFalse(a.isSymmetricZ())

        a[0, 0, 2] = 1
        a[3, 0, 2] = 1
        assertTrue(a.isSymmetricX())
        assertTrue(a.isSymmetricZ())
    }

    @Test
    fun mirror() {
        val a = Array3D(3, 4, 5) {
            x, y, z -> x*100 + y*10 + z
        }

        val mx = a.mirrorX()
        assertEquals(a[0, 0, 0], mx[2, 0, 0])
        assertEquals(a[0, 3, 4], mx[2, 3, 4])
        assertEquals(a[1, 2, 3], mx[1, 2, 3])

        val my = a.mirrorY()
        assertEquals(a[0, 0, 0], my[0, 3, 0])
        assertEquals(a[2, 0, 4], my[2, 3, 4])
        assertEquals(a[1, 2, 3], my[1, 1, 3])

        val mz = a.mirrorZ()
        assertEquals(a[0, 0, 0], mz[0, 0, 4])
        assertEquals(a[2, 3, 0], mz[2, 3, 4])
        assertEquals(a[1, 2, 3], mz[1, 2, 1])
    }

    @Test
    fun `copy up & down X`() {
        val a = Array3D(3, 2, 2, "")
        a[1, 0, 0] = "A"
        a[1, 0, 1] = "B"
        a[1, 1, 0] = "C"
        a[1, 1, 1] = "D"

        a.copyUpXLocal(1)
        assertEquals("", a[0, 0, 0])
        assertEquals("", a[0, 0, 1])
        assertEquals("", a[0, 1, 0])
        assertEquals("", a[0, 1, 1])
        assertEquals("A", a[2, 0, 0])
        assertEquals("B", a[2, 0, 1])
        assertEquals("C", a[2, 1, 0])
        assertEquals("D", a[2, 1, 1])

        a[2, 0, 0] = "Z"
        a.copyDownXLocal(2)
        assertEquals("Z", a[0, 0, 0])
        assertEquals("B", a[0, 0, 1])
        assertEquals("C", a[0, 1, 0])
        assertEquals("D", a[0, 1, 1])
        assertEquals("Z", a[1, 0, 0])
        assertEquals("B", a[1, 0, 1])
        assertEquals("C", a[1, 1, 0])
        assertEquals("D", a[1, 1, 1])
    }

    @Test
    fun `copy up & down Y`() {
        val a = Array3D(2, 3, 2, "")
        a[0, 1, 0] = "A"
        a[0, 1, 1] = "B"
        a[1, 1, 0] = "C"
        a[1, 1, 1] = "D"

        a.copyUpYLocal(1)
        assertEquals("", a[0, 0, 0])
        assertEquals("", a[0, 0, 1])
        assertEquals("", a[1, 0, 0])
        assertEquals("", a[1, 0, 1])
        assertEquals("A", a[0, 2, 0])
        assertEquals("B", a[0, 2, 1])
        assertEquals("C", a[1, 2, 0])
        assertEquals("D", a[1, 2, 1])

        a[0, 2, 0] = "Z"
        a.copyDownYLocal(2)
        assertEquals("Z", a[0, 0, 0])
        assertEquals("B", a[0, 0, 1])
        assertEquals("C", a[1, 0, 0])
        assertEquals("D", a[1, 0, 1])
        assertEquals("Z", a[0, 1, 0])
        assertEquals("B", a[0, 1, 1])
        assertEquals("C", a[1, 1, 0])
        assertEquals("D", a[1, 1, 1])
    }

    @Test
    fun `copy up & down Z`() {
        val a = Array3D(2, 2, 3, "")
        a[0, 0, 1] = "A"
        a[0, 1, 1] = "B"
        a[1, 0, 1] = "C"
        a[1, 1, 1] = "D"

        a.copyUpZLocal(1)
        assertEquals("", a[0, 0, 0])
        assertEquals("", a[0, 1, 0])
        assertEquals("", a[1, 0, 0])
        assertEquals("", a[1, 1, 0])
        assertEquals("A", a[0, 0, 2])
        assertEquals("B", a[0, 1, 2])
        assertEquals("C", a[1, 0, 2])
        assertEquals("D", a[1, 1, 2])

        a[0, 0, 2] = "Z"
        a.copyDownZLocal(2)
        assertEquals("Z", a[0, 0, 0])
        assertEquals("B", a[0, 1, 0])
        assertEquals("C", a[1, 0, 0])
        assertEquals("D", a[1, 1, 0])
        assertEquals("Z", a[0, 0, 1])
        assertEquals("B", a[0, 1, 1])
        assertEquals("C", a[1, 0, 1])
        assertEquals("D", a[1, 1, 1])
    }

    @Test
    fun `rotate around Y`() {
        val a = Array3D(2, 2, 2, "")
        a[0, 0, 0] = "A"
        a[0, 0, 1] = "B"
        a[1, 0, 0] = "C"
        a[1, 0, 1] = "D"
        a[0, 1, 0] = "E"
        a[0, 1, 1] = "F"
        a[1, 1, 0] = "G"
        a[1, 1, 1] = "H"

        val b = a.rotateY90CW()
        assertEquals("B", b[0, 0, 0])
        assertEquals("D", b[0, 0, 1])
        assertEquals("A", b[1, 0, 0])
        assertEquals("C", b[1, 0, 1])
        assertEquals("F", b[0, 1, 0])
        assertEquals("H", b[0, 1, 1])
        assertEquals("E", b[1, 1, 0])
        assertEquals("G", b[1, 1, 1])
    }
}