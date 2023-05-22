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
        appendToGui(line)
    }

    fun append(logMessage: LogMessage) {
        lastLine = logMessage.formattedString
        val sb = StringBuilder()
        sb.append(logMessage.formattedString)
        for (line in logMessage.moreLines) {
            sb.append('\n').append(line)
        }
        appendToGui(sb.toString())
    }

    private fun appendToGui(msg: String) {
        val newText = StringBuilder(text.get()).append(msg).append('\n').toString()
        text.set(newText, true)
        println(msg) // also log to stdout
    }

    fun render() {
        val flags = ImGuiInputTextFlags.ReadOnly
        // -1 makes it use 100% width/height
        ImGui.inputTextMultiline("##logs", text, -1f, -1f, flags)
    }
}