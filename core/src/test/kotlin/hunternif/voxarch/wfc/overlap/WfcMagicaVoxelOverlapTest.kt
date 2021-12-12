package hunternif.voxarch.wfc.overlap

import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.magicavoxel.writeToVoxFile
import hunternif.voxarch.wfc.WfcColor.*
import org.junit.Test
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WfcMagicaVoxelOverlapTest {

    private val colorMap = mapOf(
        AIR to null,
        GROUND to VoxColor(0x77B249),
        WALL to VoxColor(0xD7CAB5),
        FLOOR to VoxColor(0x4F6FD7)
    )

    @Test
    fun `output to Magica Voxel`() {
        val wave = WfcOverlapModel(10, 1, 10,
            patterns
        )
        wave.observe()
        println("WFC complete!")

        val path = Paths.get(
            "./out/voxarch-wfc-overlap-" +
            "${wave.width}x${wave.height}x${wave.length}-" +
            "${today()}.vox"
        )
        wave.writeToVoxFile(path, colorMap)
    }

    private val patterns = listOf(
        WfcPattern(3, 1, 3) { x, y, z ->
            when {
                x == 0 || x == 2 -> GROUND
                else -> AIR
            }
        }
    )

    private fun today() =
        DateTimeFormatter.ofPattern("YYY-MM-dd_HH_mm_ss")
            .format(LocalDateTime.now())
}