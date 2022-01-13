package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.setTool
import hunternif.voxarch.editor.util.ColorRGBa
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar

val accentColor = ColorRGBa.fromHex(0xD27626)

/** Draws a square button with an icon from FontAwesome */
inline fun iconButton(
    icon: String,
    tooltip: String = "",
    selected: Boolean = false,
    crossinline onClick: () -> Unit = {}
) {
    ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 1f, 1f)
    ImGui.pushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0.5f, 0.85f)
    if (selected)
        ImGui.pushStyleColor(
            ImGuiCol.Button,
            accentColor.r,
            accentColor.g,
            accentColor.b,
            accentColor.a
        )
    if (ImGui.button(icon, 21f, 21f)) onClick()
    if (tooltip.isNotEmpty() && ImGui.isItemHovered()) ImGui.setTooltip(tooltip)
    ImGui.popStyleVar(2)
    if (selected) ImGui.popStyleColor()
}

fun MainGui.toolButton(tool: Tool) {
    iconButton(
        tool.icon,
        tool.description,
        selected = app.currentTool == tool
    ) {
        app.setTool(tool)
    }
}