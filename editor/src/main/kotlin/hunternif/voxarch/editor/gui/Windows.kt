package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.ImGuiWindowClass
import imgui.flag.ImGuiWindowFlags
import imgui.internal.flag.ImGuiDockNodeFlags

inline fun toolbar(name: String, crossinline renderWindow: () -> Unit = {}) {
    ImGui.setNextWindowClass(
        ImGuiWindowClass().apply {
            dockNodeFlagsOverrideSet = 0 or
                ImGuiDockNodeFlags.NoTabBar or
                ImGuiDockNodeFlags.NoDocking or
                ImGuiDockNodeFlags.NoDockingSplitMe
        }
    )
    val flags = 0 or
        ImGuiWindowFlags.NoTitleBar or
        ImGuiWindowFlags.NoCollapse or
        ImGuiWindowFlags.NoMove
    if (ImGui.begin(name, flags)) {
        renderWindow()
    }
    ImGui.end()
}

fun isMouseHoveringCurrentWindow(): Boolean {
    val pos = ImGui.getWindowPos()
    val vMin = ImGui.getWindowContentRegionMin()
    val vMax = ImGui.getWindowContentRegionMax()
    return  ImGui.isMouseHoveringRect(
        pos.x + vMin.x,
        pos.y + vMin.y,
        pos.x + vMax.x,
        pos.y + vMax.y
    )
}