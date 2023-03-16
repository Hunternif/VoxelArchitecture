package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.type.ImInt

class GuiCombo<T>(
    val label: String,
    val values: List<T>,
    val width: Float? = null,
) {
    constructor(label: String, vararg values: T, width: Float? = null)
        : this(label, values.toList(), width)

    constructor(label: String, values: Iterable<T>, width: Float? = null)
        : this(label, values.toList(), width)

    /** Data storage that ImGui understands */
    @PublishedApi
    internal val index = ImInt(-1)

    @PublishedApi
    internal val names = values.map { it.toString() }.toTypedArray()

    /** True when this UI is being interacted with. */
    @PublishedApi
    internal var isBeingChanged = false

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
        width?.let { ImGui.pushItemWidth(it) }
        if (ImGui.combo(label, index, names)) {
            onUpdate(values[index.get()])
        }
        width?.let { ImGui.popItemWidth() }
        isBeingChanged = ImGui.isItemActive()
    }
}