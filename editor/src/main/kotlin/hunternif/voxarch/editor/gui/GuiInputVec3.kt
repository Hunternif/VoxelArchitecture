package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.util.readFromFloatArray
import hunternif.voxarch.editor.util.writeToFloatArray
import hunternif.voxarch.vector.Vec3
import imgui.ImGui

/** Encapsulates ImGui.dragFloat3 with some convenience methods */
class GuiInputVec3(
    val title: String,
    val min: Float = -999f,
    val max: Float = 999f,
) {
    /** Format string for the input */
    @PublishedApi internal var format = "%.0f"

    /** Stores the original value before modification via UI. */
    val original = Vec3(0, 0, 0)

    /** Reference to the value that is updated in real time. */
    @PublishedApi internal var ref = Vec3(0, 0, 0)

    /** The new value after modification. This also contains intermediate values
     * while the input is being dragged. */
    val newValue = Vec3(0, 0, 0)

    /** Data storage that ImGui understands */
    @PublishedApi internal val data = FloatArray(3)

    /** True when this UI is being interacted with. */
    @PublishedApi internal var isBeingChanged = false
    @PublishedApi internal var dirty = false

    fun setInitialValue(value: Vec3) {
        if (!isBeingChanged) {
            dirty = false
            ref = value
            original.set(value)
            newValue.set(value)
            newValue.writeToFloatArray(data)
        }
    }

    inline fun render(
        crossinline onUpdate: () -> Unit = {}
    ) {
        updateFormat()
        if (ImGui.dragFloat3(title, data, 0.1f, min, max, format)) {
            dirty = true
        }
        if (isBeingChanged && !ImGui.isItemActive()) {
            // Apply the new value
            dirty = false
            if (original != newValue) {
                onUpdate()
            }
        }
        isBeingChanged = ImGui.isItemActive()
        newValue.readFromFloatArray(data)
        ref.set(newValue)
    }

    @PublishedApi internal fun updateFormat() {
        for (x in data) {
            if ((x * 10).toInt() % 10 != 0) {
                format = "%0.1f"
                return
            }
        }
        format = "%.0f"
    }
}