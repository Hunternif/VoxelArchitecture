package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.gui.Corner.*
import imgui.ImGui
import imgui.flag.ImGuiCond
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags

enum class Corner {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT
}

inline fun overlay(
    corner: Corner,
    padding: Float = 10f,
    crossinline content: () -> Unit
) {
    val flags = 0 or
        ImGuiWindowFlags.NoDecoration or
        ImGuiWindowFlags.AlwaysAutoResize or
        ImGuiWindowFlags.NoFocusOnAppearing or
        ImGuiWindowFlags.NoNav or
        ImGuiWindowFlags.NoMove or
        ImGuiWindowFlags.NoScrollbar or
        ImGuiWindowFlags.NoDocking
    ImGui.setNextWindowBgAlpha(0.35f)

    val size = ImGui.getWindowSize()
    val pos = ImGui.getWindowPos()

    val posX = when (corner) {
        TOP_LEFT, BOTTOM_LEFT -> pos.x + padding
        TOP_RIGHT, BOTTOM_RIGHT -> pos.x + size.x - padding
    }
    val posY = when (corner) {
        TOP_LEFT, TOP_RIGHT -> pos.y + padding
        BOTTOM_LEFT, BOTTOM_RIGHT -> pos.y + size.y - padding
    }

    val pivotX = when (corner) {
        TOP_LEFT, BOTTOM_LEFT -> 0f
        TOP_RIGHT, BOTTOM_RIGHT -> 1f
    }
    val pivotY = when (corner) {
        TOP_LEFT, TOP_RIGHT -> 0f
        BOTTOM_LEFT, BOTTOM_RIGHT -> 1f
    }

    ImGui.setNextWindowPos(posX, posY, ImGuiCond.Always, pivotX, pivotY)

    ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 6f, 6f)
    ImGui.pushStyleVar(ImGuiStyleVar.WindowMinSize, 10f, 10f)

    if (ImGui.begin("overlay", flags)) {
        ImGui.popStyleVar(3)
        content()
    }
    ImGui.end()
}