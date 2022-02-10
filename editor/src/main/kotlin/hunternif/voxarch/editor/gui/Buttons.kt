package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.setTool
import hunternif.voxarch.editor.util.pushStyleColor
import imgui.ImFont
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar

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
    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 8f, 8f)
    if (selected) pushStyleColor(ImGuiCol.Button, Colors.accentActive)
    else if (transparent)
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0)
    ImGui.pushFont(font)
    if (ImGui.button(icon, size, size)) onClick()
    ImGui.popFont()
    if (tooltip.isNotEmpty() && ImGui.isItemHovered()) ImGui.setTooltip(tooltip)
    ImGui.popStyleVar(2)
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
        tool.fullDescription,
        selected = app.state.currentTool == tool
    ) {
        app.setTool(tool)
    }
}

/** A hack that uses a transparent selectable to draw text,
 * so that the text can be aligned horizontally */
fun centeredText(text: String) {
    ImGui.pushStyleVar(ImGuiStyleVar.SelectableTextAlign, 0.5f, 0f)
    // Hack: hide the selectable highlight
    pushStyleColor(ImGuiCol.Header, Colors.transparent)
    pushStyleColor(ImGuiCol.HeaderHovered, Colors.transparent)
    pushStyleColor(ImGuiCol.HeaderActive, Colors.transparent)
    ImGui.selectable(text, false)
    ImGui.popStyleColor(3)
    ImGui.popStyleVar(1)
}