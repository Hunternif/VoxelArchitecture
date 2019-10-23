package hunternif.voxarch.mc.item

import hunternif.voxarch.mc.item.ItemRadar.Companion.getColor
import hunternif.voxarch.world.HeightMap
import org.junit.Assert.assertEquals
import org.junit.Test

class ItemRadarTest {
    @Test
    fun getColor() {
        val heightMap = HeightMap(0, 0).apply {
            minHeight = 0
            maxHeight = 2
        }
        ItemRadar
        assertEquals(0x000000, heightMap.getColor(0, 0xccffcc))
        assertEquals(0x657f65, heightMap.getColor(1, 0xccffcc))
        assertEquals(0xccffcc, heightMap.getColor(2, 0xccffcc))
    }
}