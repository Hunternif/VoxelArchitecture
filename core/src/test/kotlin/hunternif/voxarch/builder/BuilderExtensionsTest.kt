package hunternif.voxarch.builder

import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.magicavoxel.readVoxFile
import hunternif.voxarch.util.assertStorageEquals
import hunternif.voxarch.util.loopSequence
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.file.Paths

class BuilderExtensionsTest {
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