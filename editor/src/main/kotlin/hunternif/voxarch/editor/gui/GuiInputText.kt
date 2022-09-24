package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.type.ImString

class GuiInputText(
    val label: String,
) {
    /** Data storage that ImGui understands */
    @PublishedApi internal val data = ImString()

    inline fun render(
        initialValue: String,
        crossinline onUpdate: (newValue: String) -> Unit = {}
    ) {
        data.set(initialValue)
        if (ImGui.inputText(label, data)) {
            onUpdate(data.get())
        }
    }
}