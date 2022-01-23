package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.type.ImBoolean

class GuiCheckbox(val label: String) {
    /** Data storage that ImGui understands */
    @PublishedApi internal val data = ImBoolean(false)

    fun setInitialValue(value: Boolean) {
        data.set(value)
    }

    inline fun render(
        initialValue: Boolean,
        crossinline onUpdate: (newValue: Boolean) -> Unit = {}
    ) {
        setInitialValue(initialValue)
        if (ImGui.checkbox(label, data)) {
            onUpdate(data.get())
        }
    }
}