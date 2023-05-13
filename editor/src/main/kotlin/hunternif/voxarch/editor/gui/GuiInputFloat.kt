package hunternif.voxarch.editor.gui

import imgui.ImGui

/** Encapsulates ImGui.dragFloat3 with some convenience methods */
class GuiInputFloat(
    label: String,
    val speed: Float = 0.1f,
    val min: Float = -999f,
    val max: Float = 999f,
) : GuiInput(label) {
    /** Format string for the input */
    @PublishedApi internal var format = "%.0f"

    /** Stores the original value before modification via UI. */
    var original = 0f

    /** The new value after modification. This also contains intermediate values
     * while the input is being dragged. */
    var newValue = 0f

    /** Data storage that ImGui understands */
    @PublishedApi internal val data = FloatArray(1)

    /** True when this UI is being interacted with. */
    @PublishedApi internal var isBeingChanged = false
    @PublishedApi internal var dirty = false

    fun setInitialValue(value: Float) {
        if (!isBeingChanged) {
            dirty = false
            original = value
            newValue = value
            data[0] = value
        }
    }

    inline fun render(
        initialValue: Float,
        crossinline onUpdated: GuiInputFloat.() -> Unit = {},
        crossinline onEditFinished: GuiInputFloat.() -> Unit = {},
    ) {
        setInitialValue(initialValue)
        updateFormat()
        if (ImGui.dragFloat(label, data, speed, min, max, format)) {
            dirty = true
            newValue = data[0]
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
        newValue = data[0]
    }

    @PublishedApi internal fun updateFormat() {
        for (x in data) {
            if ((x * 10).toInt() % 10 != 0) {
                format = "%.1f"
                return
            }
        }
        format = "%.0f"
    }
}