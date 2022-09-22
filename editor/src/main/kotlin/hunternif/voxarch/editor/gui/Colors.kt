package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.util.ColorRGBa

class Colors {
    companion object {
        val text = ColorRGBa.fromHex(0xffffff)
        val transparent = ColorRGBa.fromHex(0xffffff, 0f)

        // Default blue from Dear ImGui
        val buttonActive = ColorRGBa.fromHex(0x0f87fa)
        val buttonHovered = ColorRGBa.fromHex(0x4296fa)
        val buttonBg = ColorRGBa.fromHex(0x4296fa, 0.4f)
        val headerActive = ColorRGBa.fromHex(0x4296fa)
        val headerHovered = ColorRGBa.fromHex(0x4296fa, 0.8f)
        val headerBg = ColorRGBa.fromHex(0x4296fa, 0.31f)

        // Orange accent
        val accentHiActive = ColorRGBa.fromHex(0xC7631E)
        val accentLoActive = ColorRGBa.fromHex(0xC7631E, 0.8f)
        val accentHiHovered = ColorRGBa.fromHex(0xD27626)
        val accentLoHovered = ColorRGBa.fromHex(0xD27626, 0.8f)
        val accentHiBg = ColorRGBa.fromHex(0xD27626, 0.8f)
        val accentLoBg = ColorRGBa.fromHex(0xD27626, 0.5f)

        val defaultNodeBox = buttonBg.copy(a = 0.2f)
        val defaultGeneratedNodeBox = accentLoBg.copy(a = 0.2f)
        val selectedNodeOutline = ColorRGBa.fromHex(0xffffff)

        // Node tree colors
        val parentNode = ColorRGBa.fromHex(0x373737)
        val childNode = ColorRGBa.fromHex(0x262626)
        val generatedLabel = accentHiHovered.copy()
        val generatedHiddenLabel = generatedLabel.copy(a = 0.6f)
        val hiddenItemLabel = ColorRGBa.fromHex(0xffffff, 0.6f)

        // Blueprint editor colors
        val emptySlot = ColorRGBa.fromHex(0x999999)
        val filledSlot = buttonHovered.copy()

        // Pastel RGB colors
        val axisX = ColorRGBa.fromHex(0xD46363)
        val axisY = ColorRGBa.fromHex(0x69B042)
        val axisZ = ColorRGBa.fromHex(0x4296fa)
    }
}