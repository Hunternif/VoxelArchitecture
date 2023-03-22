package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.type.ImString

class GuiInputText(
    val label: String,
    val hint: String? = null,
) {
    /** Data storage that ImGui understands */
    @PublishedApi
    internal val data = ImString()

    inline fun render(
        initialValue: String,
        crossinline onUpdate: (newValue: String) -> Unit = {}
    ) {
        data.set(initialValue)
        if (renderText()) {
            onUpdate(data.get())
        }
    }

    @PublishedApi
    internal fun renderText(): Boolean {
        return if (hint != null) ImGui.inputTextWithHint(label, hint, data)
        else ImGui.inputText(label, data)
    }
}