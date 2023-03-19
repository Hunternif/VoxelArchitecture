package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.toggleLogs
import hunternif.voxarch.editor.util.pushStyleColor
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags

class GuiStatusBar(private val app: EditorApp) {
    private val updateTimer = Timer(0.1)

    val height: Float = 19f
    val maxMsgLength = 100

    private var message: String = "..."

    fun render() {
        updateTimer.runAtInterval { updateMessage() }

        val vp = ImGui.getMainViewport()
        ImGui.setNextWindowPos(vp.workPosX, vp.workPosY + vp.workSizeY)
        ImGui.setNextWindowSize(vp.workSizeX, height)

        pushStyleColor(ImGuiCol.Border, Colors.statusBarBg)
        pushStyleColor(ImGuiCol.WindowBg, Colors.statusBarBg)
        pushStyleColor(ImGuiCol.HeaderHovered, Colors.statusBarBgHovered)
        pushStyleColor(ImGuiCol.HeaderActive, Colors.statusBarBgPressed)
        pushStyleColor(ImGuiCol.Text, Colors.statusBarText)

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 6f, 2f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowMinSize, 10f, height)

        val flags = 0 or
            ImGuiWindowFlags.NoDecoration or
            ImGuiWindowFlags.NoFocusOnAppearing or
            ImGuiWindowFlags.NoNav or
            ImGuiWindowFlags.NoMove or
            ImGuiWindowFlags.NoScrollbar or
            ImGuiWindowFlags.NoDocking
        ImGui.begin("status_bar", flags)
        if (ImGui.selectable(message, false)) {
            app.toggleLogs()
        }
        ImGui.end()

        ImGui.popStyleVar(2)
        ImGui.popStyleColor(5)
    }

    private fun updateMessage() {
        val lastMsg = app.logs.lastOrNull() ?: return
        val lastLine = lastMsg.msg.lines().firstOrNull() ?: return
        if (lastLine.length > maxMsgLength) {
            message = lastLine.substring(0, maxMsgLength - 3) + "..."
        } else {
            message = lastLine
        }
    }
}
