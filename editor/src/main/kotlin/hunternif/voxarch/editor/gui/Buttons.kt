package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.setTool
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.pushStyleColor
import imgui.ImFont
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar

val accentColorActive = ColorRGBa.fromHex(0xD27626)
val accentColorHovered = ColorRGBa.fromHex(0xD27626, 0.8f)
val accentColorBg = ColorRGBa.fromHex(0xD27626, 0.5f)

inline fun button(
    text: String,
    tooltip: String = "",
    crossinline onClick: () -> Unit = {}
) {
    if (ImGui.button(text)) onClick()
    if (tooltip.isNotEmpty() && ImGui.isItemHovered()) ImGui.setTooltip(tooltip)
}

/** Draws a square button with an icon from FontAwesome */
inline fun GuiBase.iconButton(
    icon: String,
    tooltip: String = "",
    selected: Boolean = false,
    transparent: Boolean = false,
    size: Float,
    font: ImFont = fontBigIcons,
    crossinline onClick: () -> Unit = {}
) {
    ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0f, 0f)
    if (selected) pushStyleColor(ImGuiCol.Button, accentColorActive)
    else if (transparent)
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0)
    ImGui.pushFont(font)
    if (ImGui.button(icon, size, size)) onClick()
    ImGui.popFont()
    if (tooltip.isNotEmpty() && ImGui.isItemHovered()) ImGui.setTooltip(tooltip)
    ImGui.popStyleVar(1)
    if (selected || transparent) ImGui.popStyleColor()
}

inline fun GuiBase.bigIconButton(
    icon: String,
    tooltip: String = "",
    selected: Boolean = false,
    transparent: Boolean = false,
    crossinline onClick: () -> Unit = {}
) {
    iconButton(icon, tooltip, selected, transparent, 21f, fontBigIcons, onClick)
}

/** Draws a smaller square button with an icon from FontAwesome */
inline fun GuiBase.smallIconButton(
    icon: String,
    tooltip: String = "",
    selected: Boolean = false,
    transparent: Boolean = false,
    crossinline onClick: () -> Unit = {}
) {
    iconButton(icon, tooltip, selected, transparent, 19f, fontSmallIcons, onClick)
}

fun MainGui.toolButton(tool: Tool) {
    bigIconButton(
        tool.icon,
        tool.description,
        selected = app.currentTool == tool
    ) {
        app.setTool(tool)
    }
}