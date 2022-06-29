package hunternif.voxarch.builder

import hunternif.voxarch.plan.Structure
import hunternif.voxarch.plan.centeredRoom
import hunternif.voxarch.plan.room
import hunternif.voxarch.vector.LinearTransformation
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.sqrt

class BuilderExtensionsTest {
    @Test
    fun `find AABB for straight room without walls`() {
        val room = Structure().room(
            Vec3(1, 2, 3), Vec3(10, 20, 30)
        )
        val trans = LinearTransformation()
            .translate(room.origin)
        val aabb = room.findIntAABB(trans)
        assertEquals(1, aabb.minX)
        assertEquals(2, aabb.minY)
        assertEquals(3, aabb.minZ)
        assertEquals(10, aabb.maxX)
        assertEquals(20, aabb.maxY)
        assertEquals(30, aabb.maxZ)
    }

    @Test
    fun `find AABB for straight room with walls`() {
        val room = Structure().room(
            Vec3(1, 2, 3), Vec3(10, 20, 30)
        ) {
            createFourWalls()
        }
        val trans = LinearTransformation()
            .translate(room.origin)
        val aabb = room.findIntAABB(trans)
        assertEquals(1, aabb.minX)
        assertEquals(2, aabb.minY)
        assertEquals(3, aabb.minZ)
        assertEquals(10, aabb.maxX)
        assertEquals(20, aabb.maxY)
        assertEquals(30, aabb.maxZ)
    }

    @Test
    fun `find AABB for room rotated 45 deg`() {
        val room = Structure().centeredRoom(
            Vec3(0, 0, 0), Vec3(2, 1, 2)
        ) {
            createFourWalls()
            rotationY = 45.0
        }
        val trans = LinearTransformation()
            .translate(room.origin)
            .rotateY(room.rotationY)

        val aabb = room.findAABB(trans)
        assertEquals(-sqrt(2.0), aabb.minX, 0.0001)
        assertEquals(0.0, aabb.minY, 0.0001)
        assertEquals(-sqrt(2.0), aabb.minZ, 0.0001)
        assertEquals(sqrt(2.0), aabb.maxX, 0.0001)
        assertEquals(1.0, aabb.maxY, 0.0001)
        assertEquals(sqrt(2.0), aabb.maxZ, 0.0001)

        val iaabb = room.findIntAABB(trans)
        assertEquals(-2, iaabb.minX)
        assertEquals(0, iaabb.minY)
        assertEquals(-2, iaabb.minZ)
        assertEquals(2, iaabb.maxX)
        assertEquals(1, iaabb.maxY)
        assertEquals(2, iaabb.maxZ)
    }
}