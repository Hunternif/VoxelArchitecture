package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.util.ColorRGBa

class Colors {
    companion object {
        val text = ColorRGBa.fromHex(0xffffff)
        val transparent = ColorRGBa.fromHex(0xffffff, 0f)

        val buttonActive = ColorRGBa.fromHex(0x0f87fa)
        val buttonHovered = ColorRGBa.fromHex(0x4296fa)
        val buttonBg = ColorRGBa.fromHex(0x4296fa, 0.4f)

        val accentActive = ColorRGBa.fromHex(0xD27626)
        val accentHovered = ColorRGBa.fromHex(0xD27626, 0.8f)
        val accentBg = ColorRGBa.fromHex(0xD27626, 0.5f)

        val hiddenItemLabel = ColorRGBa.fromHex(0xffffff, 0.6f)

        val defaultNodeBox = buttonBg.copy(a = 0.2f)

        val axisX = ColorRGBa.fromHex(0xD46363)
        val axisY = ColorRGBa.fromHex(0x69B042)
        val axisZ = ColorRGBa.fromHex(0x4296fa)
    }
}
