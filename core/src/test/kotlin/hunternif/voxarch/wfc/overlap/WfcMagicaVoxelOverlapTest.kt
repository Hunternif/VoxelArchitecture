package hunternif.voxarch.wfc.overlap

import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.magicavoxel.readVoxFile
import hunternif.voxarch.magicavoxel.writeToVoxFile
import hunternif.voxarch.wfc.WfcColor.*
import org.junit.Ignore
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
    private val reverseColorMap = colorMap.toList()
        .associate { it.second to it.first }

    @Ignore
    @Test
    fun `read VOX input and write VOX output`() {
        val input = readVoxFile(
            Paths.get("./out/voxarch-wfc-overlap-input.vox"),
            reverseColorMap
        )
        val patterns = input.findPatterns(3, 1)
        println("${patterns.size} patterns read")

        val wave = WfcOverlapModel(10, 1, 10,
            patterns, System.currentTimeMillis()
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

    private fun today() =
        DateTimeFormatter.ofPattern("YYY-MM-dd_HH_mm_ss")
            .format(LocalDateTime.now())
}