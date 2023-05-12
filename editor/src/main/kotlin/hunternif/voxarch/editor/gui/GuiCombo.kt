package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.type.ImInt

class GuiCombo<T>(
    override val label: String,
    values: List<T>,
    val width: Float? = null,
) : GuiInput() {
    constructor(label: String, vararg values: T, width: Float? = null)
        : this(label, values.toList(), width)

    constructor(label: String, values: Iterable<T>, width: Float? = null)
        : this(label, values.toList(), width)

    /** Data storage that ImGui understands */
    @PublishedApi
    internal val index = ImInt(-1)

    @PublishedApi
    internal var names: Array<String> = values.map { it.toString() }.toTypedArray()

    /** True when this UI is being interacted with. */
    @PublishedApi
    internal var isBeingChanged = false

    var values: List<T> = values
        set(value) {
            if (field != value) {
                field = value
                refreshNames()
            }
        }

    fun setInitialValue(value: T) {
        if (!isBeingChanged) {
            index.set(values.indexOf(value))
        }
    }

    fun refreshNames() {
        if (values.size == names.size) {
            values.forEachIndexed { i, v -> names[i] = v.toString()}
        } else {
            names = values.map { it.toString() }.toTypedArray()
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