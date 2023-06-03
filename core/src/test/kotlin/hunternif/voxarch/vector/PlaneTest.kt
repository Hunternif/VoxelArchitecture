package hunternif.voxarch.vector

import hunternif.voxarch.util.assertVec3Equals
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.sqrt

class PlaneTest {
    @Test
    fun `vertical plane`() {
        val planeX = Plane.vertical(Vec3(0, 0, 0), Vec3(1, 0, 0))
        assertVec3Equals(Vec3(0, 0, -1), planeX.normal)
        assertEquals(0.0, planeX.distance(Vec3(2, 1, 0)), 0.0)
        assertEquals(-1.0, planeX.distance(Vec3(2, 1, 1)), 0.0)
        assertEquals(1.0, planeX.distance(Vec3(2, 1, -1)), 0.0)

        val planeZ = Plane.vertical(Vec3(0, 0, 0), Vec3(0, 0, 1))
        assertVec3Equals(Vec3(1, 0, 0), planeZ.normal)
        assertEquals(0.0, planeZ.distance(Vec3(0, 1, 2)), 0.0)
        assertEquals(1.0, planeZ.distance(Vec3(1, 1, 2)), 0.0)
        assertEquals(-1.0, planeZ.distance(Vec3(-1, 1, 2)), 0.0)
    }

    @Test
    fun `horizontal plane`() {
        val planeY = Plane.horizontal(1.0)
        assertVec3Equals(Vec3(0, 1, 0), planeY.normal)
        assertEquals(0.0, planeY.distance(Vec3(2, 1, 3)), 0.0)
        assertEquals(-1.0, planeY.distance(Vec3(2, 0, 3)), 0.0)
        assertEquals(1.0, planeY.distance(Vec3(2, 2, 3)), 0.0)
    }

    @Test
    fun `plane from 3 points`() {
        val plane = Plane.from3Points(
            Vec3(1, 0, 0), Vec3(0, 0, -1), Vec3(0, 1, 0)
        )
        assertVec3Equals(Vec3(1/sqrt(3.0), 1/sqrt(3.0), -1/sqrt(3.0)), plane.normal)

        assertEquals(0.0, plane.distance(Vec3(0.5, 0.0, -0.5)), 0.0)
        assertEquals(0.0, plane.distance(Vec3(0.5, 0.5, 0.0)), 0.0)
    }
}