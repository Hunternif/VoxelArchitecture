package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.setTool
import hunternif.voxarch.editor.util.ColorRGBa
import imgui.ImFont
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar

val accentColor = ColorRGBa.fromHex(0xD27626)

/** Draws a square button with an icon from FontAwesome */
inline fun GuiBase.iconButton(
    icon: String,
    tooltip: String = "",
    selected: Boolean = false,
    size: Float,
    font: ImFont = fontBigIcons,
    crossinline onClick: () -> Unit = {}
) {
    ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0f, 0f)
    if (selected)
        ImGui.pushStyleColor(
            ImGuiCol.Button,
            accentColor.r,
            accentColor.g,
            accentColor.b,
            accentColor.a
        )
    ImGui.pushFont(font)
    if (ImGui.button(icon, size, size)) onClick()
    ImGui.popFont()
    if (tooltip.isNotEmpty() && ImGui.isItemHovered()) ImGui.setTooltip(tooltip)
    ImGui.popStyleVar(1)
    if (selected) ImGui.popStyleColor()
}

inline fun GuiBase.bigIconButton(
    icon: String,
    tooltip: String = "",
    selected: Boolean = false,
    crossinline onClick: () -> Unit = {}
) {
    iconButton(icon, tooltip, selected, 21f, fontBigIcons, onClick)
}

/** Draws a smaller square button with an icon from FontAwesome */
inline fun GuiBase.smallIconButton(
    icon: String,
    tooltip: String = "",
    selected: Boolean = false,
    crossinline onClick: () -> Unit = {}
) {
    iconButton(icon, tooltip, selected, 19f, fontSmallIcons, onClick)
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