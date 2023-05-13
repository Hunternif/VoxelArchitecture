package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.util.ColorRGBa
import imgui.ImGui
import imgui.flag.ImGuiColorEditFlags

/** Encapsulates ImGui.dragColor3 with some convenience methods */
class GuiInputColor(
    label: String,
    val useAlpha: Boolean = false,
    val showInput: Boolean = true,
) : GuiInput(label) {
    /** Stores the original value before modification via UI. */
    val original = ColorRGBa(0f, 0f, 0f)

    /** Reference to the value that is updated in real time. */
    @PublishedApi internal var ref = ColorRGBa(0f, 0f, 0f)

    /** The new value after modification. This also contains intermediate values
     * while the input is being dragged. */
    val newValue = ColorRGBa(0f, 0f, 0f)

    /** Data storage that ImGui understands */
    @PublishedApi internal val data = FloatArray(4)

    /** True when this UI is being interacted with. */
    @PublishedApi internal var isBeingChanged = false
    @PublishedApi internal var dirty = false

    fun setInitialValue(value: ColorRGBa) {
        if (!isBeingChanged) {
            dirty = false
            ref = value
            original.set(value)
            newValue.set(value)
            newValue.writeToFloatArray(data)
        }
    }

    inline fun render(
        initialValue: ColorRGBa,
        crossinline onEditFinished: GuiInputColor.() -> Unit = {}
    ) {
        setInitialValue(initialValue)
        var flags = ImGuiColorEditFlags.DisplayHex
        if (!useAlpha) {
            flags = flags or ImGuiColorEditFlags.NoAlpha
        }
        if (!showInput) {
            flags = flags or ImGuiColorEditFlags.NoInputs
        }
        if (useAlpha && ImGui.colorEdit4(labelForImgui, data, flags) ||
            ImGui.colorEdit3(labelForImgui, data, flags)
        ) {
            dirty = true
        }
        if (isBeingChanged && !ImGui.isItemActive()) {
            // Apply the new value
            dirty = false
            if (original != newValue) {
                this.onEditFinished()
            }
        }
        isBeingChanged = ImGui.isItemActive()
        newValue.readFromFloatArray(data)
        ref.set(newValue)
    }
}