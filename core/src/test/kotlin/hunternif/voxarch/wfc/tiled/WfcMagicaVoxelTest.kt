package hunternif.voxarch.wfc.tiled

import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.magicavoxel.writeToVoxFile
import hunternif.voxarch.util.today
import hunternif.voxarch.wfc.WfcColor.*
import hunternif.voxarch.wfc.tiled.wang7x3x7.air
import hunternif.voxarch.wfc.tiled.wang7x3x7.generateValidTiles7x3x7
import hunternif.voxarch.wfc.tiled.wang7x3x7.ground
import hunternif.voxarch.wfc.tiled.wang7x3x7.groundedAir
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

class WfcMagicaVoxelTest {

    private val colorMap = mapOf(
        AIR to null,
        GROUND to VoxColor(0x77B249),
        WALL to VoxColor(0xD7CAB5),
        FLOOR to VoxColor(0x4F6FD7)
    )

    @Test
    fun `output to Magica Voxel`() {
        val wave = WfcTiledModel(10, 10, 10,
            generateValidTiles7x3x7(), 2
        )
        wave.setAirAndGroundBoundary(air, groundedAir, ground)
        wave.observe()
        println("WFC complete!")

        val path = Paths.get(
            "./out/voxarch-wfc-" +
            "${wave.width}x${wave.height}x${wave.depth}-" +
            "${today()}.vox"
        )
        wave.writeToVoxFile(path) { colorMap[it] }
        Files.delete(path) // clean up
    }
}