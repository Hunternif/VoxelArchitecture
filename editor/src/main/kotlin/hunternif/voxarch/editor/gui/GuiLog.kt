package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.util.LogMessage
import imgui.ImGui
import imgui.flag.ImGuiInputTextFlags
import imgui.type.ImString

/**
 * Stores and renders logs.
 */
class GuiLog {
    private val text = ImString()

    var lastLine: String = "..."
        private set

    fun append(line: String) {
        lastLine = line
        val newText = StringBuilder(text.get()).append(line).append('\n').toString()
        text.set(newText, true)
    }

    fun append(logMessage: LogMessage) {
        lastLine = logMessage.formattedString
        val sb = StringBuilder(text.get())
        sb.append(logMessage.formattedString).append('\n')
        for (line in logMessage.moreLines) {
            sb.append(line).append('\n')
        }
        text.set(sb.toString(), true)
    }

    fun render() {
        val flags = ImGuiInputTextFlags.ReadOnly
        // -1 makes it use 100% width/height
        ImGui.inputTextMultiline("##logs", text, -1f, -1f, flags)
    }
}