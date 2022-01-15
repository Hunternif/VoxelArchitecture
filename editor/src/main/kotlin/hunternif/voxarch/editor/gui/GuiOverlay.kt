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
    title: String,
    corner: Corner,
    padding: Float = 10f,
    innerPadding: Float = 6f,
    offsetX: Float = 0f,
    offsetY: Float = 0f,
    bgAlpha: Float = 0.35f,
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
    ImGui.setNextWindowBgAlpha(bgAlpha)

    val size = ImGui.getWindowSize()
    val pos = ImGui.getWindowPos()

    val posX = when (corner) {
        TOP_LEFT, BOTTOM_LEFT -> pos.x + padding
        TOP_RIGHT, BOTTOM_RIGHT -> pos.x + size.x - padding
    } + offsetX
    val posY = when (corner) {
        TOP_LEFT, TOP_RIGHT -> pos.y + padding
        BOTTOM_LEFT, BOTTOM_RIGHT -> pos.y + size.y - padding
    } + offsetY

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
    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, innerPadding, innerPadding)
    ImGui.pushStyleVar(ImGuiStyleVar.WindowMinSize, 10f, 10f)

    if (ImGui.begin(title, flags)) {
        content()
    }
    ImGui.popStyleVar(3)
    ImGui.end()
}