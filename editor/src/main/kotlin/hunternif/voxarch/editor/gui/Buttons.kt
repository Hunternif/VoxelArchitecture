package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.flag.ImGuiStyleVar

/** Draws a square button with an icon from FontAwesome */
inline fun iconButton(
    icon: String,
    tooltip: String = "",
    crossinline onClick: () -> Unit = {}
) {
    ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 1f, 1f)
    ImGui.pushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0.5f, 0.85f)
    if (ImGui.button(icon, 21f, 21f)) onClick()
    if (tooltip.isNotEmpty() && ImGui.isItemHovered()) ImGui.setTooltip(tooltip)
    ImGui.popStyleVar(2)
}