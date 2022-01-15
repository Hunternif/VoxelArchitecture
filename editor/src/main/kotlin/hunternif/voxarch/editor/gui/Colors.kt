package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.util.ColorRGBa

class Colors {
    companion object {
        val buttonActive = ColorRGBa.fromHex(0x0f87fa)
        val buttonHovered = ColorRGBa.fromHex(0x4296fa)
        val buttonBg = ColorRGBa.fromHex(0x4296fa, 0.4f)

        val defaultNodeBox = buttonBg.copy(a = 0.3f)

        val accentActive = ColorRGBa.fromHex(0xD27626)
        val accentHovered = ColorRGBa.fromHex(0xD27626, 0.8f)
        val accentBg = ColorRGBa.fromHex(0xD27626, 0.5f)
    }
}
