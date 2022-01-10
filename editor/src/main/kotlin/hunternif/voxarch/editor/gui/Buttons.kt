package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.flag.ImGuiStyleVar

/** Draws a square button with an icon from FontAwesome */
fun iconButton(icon: String, tooltip: String = ""): Boolean {
    ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 1f, 1f)
    ImGui.pushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0.5f, 0.85f)
    val pressed = ImGui.button(icon, 21f, 21f)
    if (tooltip.isNotEmpty() && ImGui.isItemHovered()) {
        ImGui.setTooltip(tooltip)
    }
    ImGui.popStyleVar(2)
    return pressed
}