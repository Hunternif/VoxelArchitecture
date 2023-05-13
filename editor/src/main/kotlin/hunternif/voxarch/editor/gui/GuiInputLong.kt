package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.flag.ImGuiDataType
import imgui.type.ImLong

/** Encapsulates ImGui.dragScalar with some convenience methods */
class GuiInputLong(
    label: String,
    val speed: Float = 0.2f,
    val min: Long = 0L,
    val max: Long = Long.MAX_VALUE,
) : GuiInput(label) {
    /** Stores the original value before modification via UI. */
    var original = 0L

    /** The new value after modification. This also contains intermediate values
     * while the input is being dragged. */
    var newValue = 0L

    /** Data storage that ImGui understands */
    @PublishedApi internal val data = ImLong()

    /** True when this UI is being interacted with. */
    @PublishedApi internal var isBeingChanged = false
    @PublishedApi internal var dirty = false

    fun setInitialValue(value: Long) {
        if (!isBeingChanged) {
            dirty = false
            original = value
            newValue = value
            data.set(value)
        }
    }

    inline fun render(
        initialValue: Long,
        crossinline onUpdated: GuiInputLong.() -> Unit = {},
        crossinline onEditFinished: GuiInputLong.() -> Unit = {},
    ) {
        setInitialValue(initialValue)
        if (ImGui.dragScalar(label, ImGuiDataType.U64, data, speed, min, max)) {
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