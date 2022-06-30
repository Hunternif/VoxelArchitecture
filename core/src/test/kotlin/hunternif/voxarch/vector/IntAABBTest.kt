package hunternif.voxarch.vector

import hunternif.voxarch.util.*
import org.junit.Assert.assertEquals
import org.junit.Test

class IntAABBTest {
    @Test
    fun `AABB to IntAABB`() {
        val aabb = AABB()
        aabb.union(-1.5, -0.1, 0.0)
        aabb.union(1.0, 9.9, 3.0)
        aabb.correctBounds()

        val iaabb = aabb.toIntAABB()
        assertEquals(-2, iaabb.minX)
        assertEquals(-1, iaabb.minY)
        assertEquals(0, iaabb.minZ)
        assertEquals(1, iaabb.maxX)
        assertEquals(10, iaabb.maxY)
        assertEquals(3, iaabb.maxZ)
    }

    @Test
    fun forEachXZ() {
        val aabb = IntAABB()
        aabb.union(1, 0, 0)
        aabb.union(2, 0, 1)
        aabb.correctBounds()
        assertEquals(1, aabb.minX)
        assertEquals(0, aabb.minY)
        assertEquals(0, aabb.minZ)
        assertEquals(2, aabb.maxX)
        assertEquals(0, aabb.maxY)
        assertEquals(1, aabb.maxZ)

        val ar = Array3D(4, 1, 3, '#')
        assertEquals("""
####
####
####
""".trim(),
            ar.printSliceY(0)
        )

        aabb.forEachXZ { x, z ->
            ar[x, 0, z] = 'O'
        }
        assertEquals("""
#OO#
#OO#
####
""".trim(),
            ar.printSliceY(0)
        )
    }
}