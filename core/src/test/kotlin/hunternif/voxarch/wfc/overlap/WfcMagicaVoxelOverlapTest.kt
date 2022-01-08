package hunternif.voxarch.wfc.overlap

import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.magicavoxel.readVoxFile
import hunternif.voxarch.storage.IArray3D
import hunternif.voxarch.util.forEachPos
import hunternif.voxarch.wfc.WfcColor.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.file.Path
import java.nio.file.Paths

class WfcMagicaVoxelOverlapTest {

    private val colorMap = mapOf(
        AIR to null,
        GROUND to VoxColor(0x77B249),
        WALL to VoxColor(0xD7CAB5),
        FLOOR to VoxColor(0x4F6FD7)
    )
    private val reverseColorMap = colorMap.toList()
        .associate { it.second to it.first }

    @Test
    fun `lattice pattern with 2 attempts`() {
        val input = readVoxFile(
            REFERENCES_DIR.resolve("lattice-sample.vox"),
            reverseColorMap
        )
        val patterns = input.findPatterns(3, 3)
        println("${patterns.size} patterns read")

        val wave = WfcOverlapModel(8, 8, 8, patterns, 1)
        wave.observe()
        println("WFC 1 complete!")
        val lattice1Ref = readVoxFile(
            REFERENCES_DIR.resolve("lattice-1.vox"),
            reverseColorMap
        )
        assertStorageEquals(lattice1Ref, wave)

        wave.reset(2)
        wave.observe()
        println("WFC 2 complete!")
        val lattice2Ref = readVoxFile(
            REFERENCES_DIR.resolve("lattice-2.vox"),
            reverseColorMap
        )
        assertStorageEquals(lattice2Ref, wave)
    }

    @Test
    fun `stripe pattern`() {
        val wave = WfcOverlapModel(8, 1, 8, patterns)
        wave.observe()
        println("WFC complete!")

        val stripesRef = readVoxFile(
            REFERENCES_DIR.resolve("stripes.vox"),
            reverseColorMap
        )
        assertStorageEquals(stripesRef, wave)
    }

    private val patterns = listOf(
        WfcPattern(3, 1, 3) { x, y, z ->
            when {
                z == 0 || z == 2 -> GROUND
                else -> AIR
            }
        },
        WfcPattern(3, 1, 3) { x, y, z ->
            when {
                z == 1 -> GROUND
                else -> AIR
            }
        }
    )

    companion object {
        val REFERENCES_DIR: Path = Paths.get("./src/test/resources/wfc")

        private fun <T> assertStorageEquals(
            expected: IArray3D<in T>,
            actual: IArray3D<in T>
        ) {
            assertEquals(expected.width, actual.width)
            assertEquals(expected.height, actual.height)
            assertEquals(expected.length, actual.length)
            expected.forEachPos { x, y, z, t ->
                assertEquals(t, actual[x, y, z])
            }
        }
    }
}