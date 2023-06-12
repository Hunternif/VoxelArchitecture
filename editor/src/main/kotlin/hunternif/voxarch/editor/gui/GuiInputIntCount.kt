package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.type.ImInt

/** Encapsulates ImGui.inputInt with [+/-] buttons, with some convenience methods */
class GuiInputIntCount(
    label: String,
    val min: Int = 0,
    val max: Int = Int.MAX_VALUE,
) : GuiInput(label) {
    /** Stores the original value before modification via UI. */
    var original = 0

    /** The new value after modification. This also contains intermediate values
     * while the input is being dragged. */
    var newValue = 0

    /** Data storage that ImGui understands */
    @PublishedApi internal val data = ImInt()

    /** True when this UI is being interacted with. */
    @PublishedApi internal var isBeingChanged = false
    @PublishedApi internal var dirty = false

    fun setInitialValue(value: Int) {
        if (!isBeingChanged) {
            dirty = false
            original = value
            newValue = value
            data.set(value)
        }
    }

    inline fun render(
        initialValue: Int,
        crossinline onUpdated: GuiInputIntCount.() -> Unit = {},
        crossinline onEditFinished: GuiInputIntCount.() -> Unit = {},
    ) {
        setInitialValue(initialValue)
        if (ImGui.inputInt(label, data)) {
            dirty = true
            newValue = data.get()
            this.onUpdated()
        }
        if (isBeingChanged && !ImGui.isItemActive()) {
            // Apply the new value
            dirty = false
            if (original != newValue) {
                this.onEditFinished()
            }
        }
        isBeingChanged = ImGui.isItemActive()
        newValue = data.get()
    }
}