package hunternif.voxarch.editor.gui

import hunternif.voxarch.dom.style.Declaration
import hunternif.voxarch.dom.style.Property
import hunternif.voxarch.dom.style.Rule
import hunternif.voxarch.dom.style.set
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.blueprintEditorStyleProperties
import imgui.ImGui

class GuiBlueprintNodeStyle(
    node: BlueprintNode,
) {
    private val rule = node.rule

    @Suppress("TYPE_MISMATCH_WARNING", "UNCHECKED_CAST")
    val items: List<Item<*>> by lazy {
        blueprintEditorStyleProperties.mapNotNull { p ->
            when (p.default) {
                is Number -> ItemNumber(rule, p as Property<Number>)
                is Enum<*> -> ItemEnum(rule, p as Property<Enum<*>>)
                else -> null
            }
        }
    }

    abstract class Item<V : Any>(
        private val rule: Rule,
        private val property: Property<V>,
    ) {
        private val checkbox = GuiCheckbox("##enabled_${property.name}")
        abstract val value: V
        protected val declaration = rule.propertyMap[property]
            ?: Declaration.defaultForProperty(property)
        var stringRepr: String = "${property.name}:"
            private set

        var enabled: Boolean = property in rule.propertyMap

        fun render() {
            checkbox.render(enabled) {
                enabled = it
                if (enabled) rule.add(declaration)
                else rule.remove(declaration)
            }
            ImGui.sameLine()
            disabled(!enabled) {
                renderInput()
            }
        }

        protected abstract fun renderInput()

        protected fun updateStringRepr() {
            stringRepr = "${property.name}: ${valueToString()}"
        }

        protected open fun valueToString(): String = value.toString()
    }

    class ItemNumber(
        rule: Rule,
        property: Property<Number>,
        min: Float = -999f,
        max: Float = 999f,
    ) : Item<Number>(rule, property) {
        private val gui = GuiInputFloat(property.name, min = min, max = max)
        override var value: Float = declaration.value.invoke(0.0, 0L).toFloat()

        init {
            updateStringRepr()
        }

        override fun renderInput() {
            gui.render(value.toFloat()) {
                value = gui.newValue
                updateStringRepr()
                declaration.value = set(value)
            }
        }

        override fun valueToString() = gui.format.format(value)
    }

    class ItemEnum<E : Enum<E>>(
        rule: Rule,
        property: Property<E>,
    ) : Item<E>(rule, property) {
        private val values = property.valType.enumConstants
        private val gui = GuiCombo(property.name, *values)
        override var value: E = declaration.value.invoke(property.default, 0L)

        init {
            updateStringRepr()
        }

        override fun renderInput() {
            gui.render(value) {
                value = it
                updateStringRepr()
                declaration.value = set(value)
            }
        }
    }
}