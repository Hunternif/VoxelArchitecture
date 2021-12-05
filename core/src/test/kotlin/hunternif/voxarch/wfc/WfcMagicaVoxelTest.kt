package hunternif.voxarch.wfc

import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.magicavoxel.writeToVoxFile
import hunternif.voxarch.wfc.WangVoxel.*
import hunternif.voxarch.wfc.wang7x3x7.air
import hunternif.voxarch.wfc.wang7x3x7.generateValidTiles7x3x7
import hunternif.voxarch.wfc.wang7x3x7.ground
import hunternif.voxarch.wfc.wang7x3x7.groundedAir
import org.junit.Test
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WfcMagicaVoxelTest {

    private val colorMap = mapOf(
        AIR to null,
        GROUND to VoxColor(0x77B249),
        WALL to VoxColor(0xD7CAB5),
        FLOOR to VoxColor(0x4F6FD7)
    )

    @Test
    fun `output to Magica Voxel`() {
        val wave = WfcGrid(10, 5, 10,
            generateValidTiles7x3x7(), 1
        )
        wave.setAirAndGroundBoundary(air, groundedAir, ground)
        wave.collapse()
        println("WFC complete!")

        val path = Paths.get("./out/voxarch-wfc-${today()}.vox")
        wave.writeToVoxFile(path, colorMap)
    }

    private fun today() =
        DateTimeFormatter.ofPattern("YYY-MM-dd_HH_mm")
            .format(LocalDateTime.now())
}