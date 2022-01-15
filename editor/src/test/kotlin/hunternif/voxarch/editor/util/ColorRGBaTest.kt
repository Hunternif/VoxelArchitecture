package hunternif.voxarch.editor.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ColorRGBaTest {
    @Test
    fun `fromHex re-generates correct hex value`() {
        val colors = listOf(
            0x000000,
            0x336699,
            0xfc0267,
            0xffffff,
        )
        for (color in colors) {
            val rgba = ColorRGBa.fromHex(color)
            assertEquals(color, rgba.hex)
        }
    }
}