package hunternif.voxarch.wfc

import hunternif.voxarch.util.Direction3D
import org.junit.Assert.*
import org.junit.Test

class WfcBoundaryTest {
    @Test
    fun `air boundary 3x3x3`() {
        val wave = WfcGrid(3,3, 3, emptyList())
        wave.setAirBoundary(air)
        assertEquals(air, wave[0, 0, 0])
        assertEquals(air, wave[0, 0, 1])
        assertEquals(air, wave[0, 1, 2])
        assertEquals(air, wave[0, 1, 1])
        assertEquals(air, wave[1, 0, 1])
        assertEquals(air, wave[1, 1, 0])
        assertEquals(air, wave[2, 2, 2])
        assertEquals(null, wave[1, 1, 1])
    }

    @Test
    fun `air & ground boundary 3x3x3`() {
        val wave = WfcGrid(3,3, 3, emptyList())
        wave.setAirAndGroundBoundary(air, groundedAir, ground)
        assertEquals(ground, wave[0, 0, 0])
        assertEquals(ground, wave[0, 0, 1])
        assertEquals(ground, wave[1, 0, 1])
        assertEquals(groundedAir, wave[0, 1, 2])
        assertEquals(groundedAir, wave[0, 1, 1])
        assertEquals(groundedAir, wave[1, 1, 0])
        assertEquals(air, wave[0, 2, 2])
        assertEquals(air, wave[1, 2, 1])
        assertEquals(air, wave[2, 2, 2])
        assertEquals(null, wave[1, 1, 1])
    }

    class DummyTile: WfcTile {
        override fun matchesSide(other: WfcTile, dir: Direction3D) = false
    }

    companion object {
        private val air = DummyTile()
        private val groundedAir = DummyTile()
        private val ground = DummyTile()
    }
}