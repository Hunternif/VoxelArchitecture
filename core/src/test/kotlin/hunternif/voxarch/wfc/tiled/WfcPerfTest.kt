package hunternif.voxarch.wfc.tiled

import hunternif.voxarch.wfc.tiled.wang7x3x7.air
import hunternif.voxarch.wfc.tiled.wang7x3x7.generateValidTiles7x3x7
import hunternif.voxarch.wfc.tiled.wang7x3x7.ground
import hunternif.voxarch.wfc.tiled.wang7x3x7.groundedAir
import org.junit.Ignore
import org.junit.Test

class WfcPerfTest {
    @Test
    @Ignore
    fun `performance test`() {
        for (i in 1..10) {
            val wave = WfcTiledModel(50, 30, 50,
                generateValidTiles7x3x7(), i.toLong()
            )
            wave.setAirAndGroundBoundary(air, groundedAir, ground)
            wave.observe()
            println("WFC complete!")
        }
    }
}