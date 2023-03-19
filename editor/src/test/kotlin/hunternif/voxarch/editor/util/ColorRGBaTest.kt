package hunternif.voxarch.editor.util

import org.junit.Assert.assertEquals
import org.junit.Test
import org.lwjgl.system.MemoryUtil
import hunternif.voxarch.editor.util.ColorRGBa.Companion.fromHex as rgb

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
            val rgba = rgb(color)
            assertEquals(color, rgba.hex)
        }
    }

    @Test
    fun `add 2 colors`() {
        // commutativity
        assertEquals(rgb(0x336699), rgb(0x000000).add(rgb(0x336699)))
        assertEquals(rgb(0x336699), rgb(0x336699).add(rgb(0x000000)))

        // actually summing 2 parts
        assertEquals(rgb(0x669900), rgb(0x663300).add(rgb(0x006600)))

        // limiting at 0xff
        assertEquals(rgb(0xffffff), rgb(0xffaa00).add(rgb(0x11aaff)))

        // use alpha
        assertEquals(
            rgb(0xffff66, 0.5f),
            rgb(0xff0033, 0.25f).add(rgb(0x00ff33, 0.25f))
        )
    }

    @Test
    fun `alpha-blend 2 colors`() {
        assertEquals(
            rgb(0x5500AA, 0.75f),
            rgb(0xff0000, 0.5f).blend(rgb(0x0000ff, 0.5f))
        )
        // non-commutable, the 2nd color is on top
        assertEquals(
            rgb(0xAA0055, 0.75f),
            rgb(0x0000ff, 0.5f).blend(rgb(0xff0000, 0.5f))
        )
    }

    @Test
    fun `color to string`() {
        assertEquals("0x00CC99 75%", ColorRGBa.fromHex(0x00cc99, 0.751f).toString())
    }

    @Test
    fun `read color from buffer`() {
        val buffer = MemoryUtil.memAlloc(3)
        buffer.put(0x00.toByte())
        buffer.put(0xcc.toByte())
        buffer.put(0x99.toByte())
        buffer.flip()
        val colorFromBytes = ColorRGBa.fromRGBBytes(buffer)
        val expected = ColorRGBa.fromHex(0x00cc99)
        assertEquals(expected, colorFromBytes)
    }

    @Test
    fun `to hex ABGR`() {
        val hexABGR = ColorRGBa.fromHex(0xfc0267).hexABGR
        assertEquals("0xFF6702FC", String.format("0x%08X", hexABGR))
    }
}