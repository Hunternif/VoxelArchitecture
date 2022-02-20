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
    width: Float = 0f,
    crossinline onClick: () -> Unit = {}
) {
    if (ImGui.button(text, width, 0f)) onClick()
    if (tooltip.isNotEmpty() && ImGui.isItemHovered()) ImGui.setTooltip(tooltip)
}

inline fun accentButton(
    text: String,
    tooltip: String = "",
    width: Float = 0f,
    crossinline onClick: () -> Unit = {}
) {
    pushStyleColor(ImGuiCol.Button, Colors.accentLoBg)
    pushStyleColor(ImGuiCol.ButtonHovered, Colors.accentLoHovered)
    pushStyleColor(ImGuiCol.ButtonActive, Colors.accentLoActive)
    button(text, tooltip, width, onClick)
    ImGui.popStyleColor(3)
}

/** Draws a square button with an icon from FontAwesome */
inline fun GuiBase.iconButton(
    icon: String,
    tooltip: String = "",
    accent: Boolean = false,
    selected: Boolean = false,
    transparent: Boolean = false,
    width: Float = ImGui.getFrameHeight(),
    height: Float = width,
    font: ImFont = fontBigIcons,
    crossinline onClick: () -> Unit = {}
) {
    ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, -1f, -1f)
    if (accent) {
        pushStyleColor(ImGuiCol.Button, Colors.accentLoBg)
        pushStyleColor(ImGuiCol.ButtonHovered, Colors.accentLoHovered)
        pushStyleColor(ImGuiCol.ButtonActive, Colors.accentLoActive)
    }
    else if (selected) {
        pushStyleColor(ImGuiCol.Button, Colors.accentHiBg)
        pushStyleColor(ImGuiCol.ButtonHovered, Colors.accentHiBg)
        pushStyleColor(ImGuiCol.ButtonActive, Colors.accentHiBg)
    }
    else if (transparent)
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0)
    ImGui.pushFont(font)
    if (ImGui.button(icon, width, height)) onClick()
    ImGui.popFont()
    if (tooltip.isNotEmpty() && ImGui.isItemHovered()) ImGui.setTooltip(tooltip)
    ImGui.popStyleVar(1)
    if (accent || selected) ImGui.popStyleColor(3)
    else if (transparent) ImGui.popStyleColor()
}

inline fun GuiBase.bigIconButton(
    icon: String,
    tooltip: String = "",
    selected: Boolean = false,
    transparent: Boolean = false,
    crossinline onClick: () -> Unit = {}
) {
    iconButton(icon, tooltip, false, selected, transparent, 22f, 22f, fontBigIcons, onClick)
}

/** Draws a smaller square button with an icon from FontAwesome */
inline fun GuiBase.smallIconButton(
    icon: String,
    tooltip: String = "",
    selected: Boolean = false,
    transparent: Boolean = false,
    crossinline onClick: () -> Unit = {}
) {
    iconButton(icon, tooltip, false, selected, transparent, 20f, 19f, fontSmallIcons, onClick)
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

inline fun GuiBase.inlineIconButton(
    text: String,
    font: ImFont = fontSmallIcons,
    crossinline onClick: () -> Unit = {}
) {
    ImGui.pushFont(font)
    ImGui.pushStyleVar(ImGuiStyleVar.SelectableTextAlign, 0.5f, 0f)
    if (ImGui.selectable(text, false)) onClick()
    ImGui.popStyleVar(1)
    ImGui.popFont()
}