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
        assertVec3Equals(Vec3(0, 0, 0), planeX.findPoint())
        assertEquals(0.0, planeX.distance(Vec3(2, 1, 0)), 0.0)
        assertEquals(-1.0, planeX.distance(Vec3(2, 1, 1)), 0.0)
        assertEquals(1.0, planeX.distance(Vec3(2, 1, -1)), 0.0)

        val planeZ = Plane.vertical(Vec3(0, 0, 0), Vec3(0, 0, 1))
        assertVec3Equals(Vec3(1, 0, 0), planeZ.normal)
        assertVec3Equals(Vec3(0, 0, 0), planeZ.findPoint())
        assertEquals(0.0, planeZ.distance(Vec3(0, 1, 2)), 0.0)
        assertEquals(1.0, planeZ.distance(Vec3(1, 1, 2)), 0.0)
        assertEquals(-1.0, planeZ.distance(Vec3(-1, 1, 2)), 0.0)
    }

    @Test
    fun `horizontal plane`() {
        val planeY = Plane.horizontal(1.0)
        assertVec3Equals(Vec3(0, 1, 0), planeY.normal)
        assertVec3Equals(Vec3(0, 1, 0), planeY.findPoint())
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
        assertVec3Equals(Vec3(1/3.0, 1/3.0, -1/3.0), plane.findPoint())
        assertEquals(0.0, plane.distance(Vec3(0.5, 0.0, -0.5)), 0.0)
        assertEquals(0.0, plane.distance(Vec3(0.5, 0.5, 0.0)), 0.0)
        assertEquals(1 / sqrt(3.0), plane.distance(Vec3(1, 1, 0)), 0.0)
    }

    @Test
    fun `move vertical plane`() {
        val planeX = Plane.vertical(Vec3(0, 0, 0), Vec3(1, 0, 0))
            .move(Vec3(2, 2, 2))
        assertVec3Equals(Vec3(0, 0, -1), planeX.normal)
        assertVec3Equals(Vec3(0, 0, 2), planeX.findPoint())
        assertEquals(0.0, planeX.distance(Vec3(4, 1, 2)), 0.0)
        assertEquals(-1.0, planeX.distance(Vec3(4, 1, 3)), 0.0)
        assertEquals(1.0, planeX.distance(Vec3(4, 1, 1)), 0.0)

        val planeZ = Plane.vertical(Vec3(0, 0, 0), Vec3(0, 0, 1))
            .move(Vec3(2, 2, 2))
        assertVec3Equals(Vec3(1, 0, 0), planeZ.normal)
        assertVec3Equals(Vec3(2, 0, 0), planeZ.findPoint())
        assertEquals(0.0, planeZ.distance(Vec3(2, 1, 4)), 0.0)
        assertEquals(1.0, planeZ.distance(Vec3(3, 1, 4)), 0.0)
        assertEquals(-1.0, planeZ.distance(Vec3(1, 1, 4)), 0.0)
    }

    @Test
    fun `move horizontal plane`() {
        val planeY = Plane.horizontal(1.0).move(Vec3(2, 2, 2))
        assertVec3Equals(Vec3(0, 1, 0), planeY.normal)
        assertVec3Equals(Vec3(0, 3, 0), planeY.findPoint())
        assertEquals(0.0, planeY.distance(Vec3(2, 3, 3)), 0.0)
        assertEquals(-1.0, planeY.distance(Vec3(2, 2, 3)), 0.0)
        assertEquals(1.0, planeY.distance(Vec3(2, 4, 3)), 0.0)
    }

    @Test
    fun `move plane from 3 points`() {
        val plane = Plane.from3Points(
            Vec3(1, 0, 0), Vec3(0, 0, -1), Vec3(0, 1, 0)
        ).move(Vec3(2, 2, 2))
        assertVec3Equals(Vec3(1/sqrt(3.0), 1/sqrt(3.0), -1/sqrt(3.0)), plane.normal)
        assertEquals(0.0, plane.distance(Vec3(2.5, 2.0, 1.5)), 0.00001)
        assertEquals(0.0, plane.distance(Vec3(2.5, 2.5, 2.0)), 0.00001)
        assertEquals(1 / sqrt(3.0), plane.distance(Vec3(3, 3, 2)), 0.00001)
    }

    @Test
    fun `rotate vertical plane`() {
        val planeX = Plane.vertical(Vec3(0, 0, 0), Vec3(1, 0, 0))
            .transform(LinearTransformation().rotateY(90.0))
        assertVec3Equals(Vec3(-1, 0, 0), planeX.normal)
        assertVec3Equals(Vec3(0, 0, 0), planeX.findPoint())
        assertEquals(0.0, planeX.distance(Vec3(0, 1, 2)), 0.0)
        assertEquals(-1.0, planeX.distance(Vec3(1, 1, 2)), 0.0)
        assertEquals(1.0, planeX.distance(Vec3(-1, 1, 2)), 0.0)

        val planeZ = Plane.vertical(Vec3(0, 0, 0), Vec3(0, 0, 1))
            .transform(LinearTransformation().rotateY(90.0))
        assertVec3Equals(Vec3(0, 0, -1), planeZ.normal)
        assertVec3Equals(Vec3(0, 0, 0), planeZ.findPoint())
        assertEquals(0.0, planeZ.distance(Vec3(2, 1, 0)), 0.0)
        assertEquals(-1.0, planeZ.distance(Vec3(2, 1, 1)), 0.0)
        assertEquals(1.0, planeZ.distance(Vec3(2, 1, -1)), 0.0)
    }
}