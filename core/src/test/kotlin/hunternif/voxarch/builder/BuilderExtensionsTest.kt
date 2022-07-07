package hunternif.voxarch.builder

import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.magicavoxel.readVoxFile
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.plan.centeredRoom
import hunternif.voxarch.plan.room
import hunternif.voxarch.util.assertStorageEquals
import hunternif.voxarch.util.loopSequence
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.LinearTransformation
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.file.Paths
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

    @Test
    fun `draw lines in 3D`() {
        val path = Paths.get("./src/test/resources/lines_from_center_in_21_cube.vox")
        val reference = readVoxFile(path)
        assertEquals(909, reference.size) // Verify the ref is read correctly

        val out = Array3D<VoxColor?>(21, 21, 21, null)
        val colors = listOf(0xD7CAB5, 0x69B042, 0x4296fa, 0xD46363)
            .loopSequence().iterator()

        val points = mutableListOf<Vec3>()
        for (x in listOf(0, 5, 10, 15, 20))
            for (y in listOf(0, 5, 10, 15, 20))
                for (z in listOf(0, 5, 10, 15, 20))
                    points.add(Vec3(x, y, z))

        for (p in points) {
            val color = colors.next()
            line2(Vec3(10, 10, 10), p) {
                out[it.toIntVec3()] = VoxColor(color)
            }
        }

        // This was used to generate the reference:
        // out.writeToVoxFile(path)
        assertStorageEquals(reference, out)
    }
}