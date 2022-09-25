package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.type.ImInt

class GuiCombo<T>(
    val label: String,
    vararg val values: T,
) {
    /** Data storage that ImGui understands */
    @PublishedApi internal val index = ImInt(-1)
    @PublishedApi internal val names = values.map { it.toString() }.toTypedArray()

    /** True when this UI is being interacted with. */
    @PublishedApi internal var isBeingChanged = false

    fun setInitialValue(value: T) {
        if (!isBeingChanged) {
            index.set(values.indexOf(value))
        }
    }

    inline fun render(
        initialValue: T,
        crossinline onUpdate: (newValue: T) -> Unit = {}
    ) {
        setInitialValue(initialValue)
        if (ImGui.combo(label, index, names)) {
            onUpdate(values[index.get()])
        }
        isBeingChanged = ImGui.isItemActive()
    }
}