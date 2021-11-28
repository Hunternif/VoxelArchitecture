package hunternif.voxarch.sandbox

import hunternif.voxarch.world.HeightMap
import org.junit.Assert.assertEquals
import org.junit.Test

class RadarTest {
    @Test
    fun getColor() {
        val heightMap = HeightMap(1, 1).apply {
            minHeight = 0
            maxHeight = 2
        }
        assertEquals(0x000000, heightMap.getColor(0, 0xccffcc))
        assertEquals(0x657f65, heightMap.getColor(1, 0xccffcc))
        assertEquals(0xccffcc, heightMap.getColor(2, 0xccffcc))
    }
}