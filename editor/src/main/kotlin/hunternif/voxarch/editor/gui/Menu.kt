package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.flag.ImGuiStyleVar

inline fun mainMenuBar(crossinline content: () -> Unit) {
    ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
    if (ImGui.beginMainMenuBar()) {
        content()
        ImGui.endMainMenuBar()
    }
    ImGui.popStyleVar(1)
}

inline fun menu(label: String, crossinline content: () -> Unit) {
    if (ImGui.beginMenu(label)) {
        content()
        ImGui.endMenu()
    }
}

inline fun menuItem(
    label: String,
    shortcut: String = "",
    enabled: Boolean = true,
    tooltip: String? = null,
    crossinline onClick: () -> Unit,
) {
    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 10f, 10f)
    if (ImGui.menuItem(label, shortcut, false, enabled)) { onClick() }
    tooltip(tooltip)
    ImGui.popStyleVar()
}

inline fun menuCheck(
    label: String,
    selected: Boolean,
    enabled: Boolean = true,
    tooltip: String? = null,
    crossinline onClick: () -> Unit,
) {
    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 10f, 10f)
    if (ImGui.menuItem(label, "", selected, enabled)) { onClick() }
    tooltip(tooltip)
    ImGui.popStyleVar()
}