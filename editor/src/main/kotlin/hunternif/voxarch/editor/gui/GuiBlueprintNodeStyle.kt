package hunternif.voxarch.editor.gui

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.blueprintEditorStyleProperties
import imgui.ImGui

class GuiBlueprintNodeStyle(
    node: BlueprintNode,
) {
    private val rule: Rule = node.rule

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

    init {
        refreshDeclarations()
    }

    fun render() {
        disabled {
            ImGui.textWrapped("Note that dynamic values may not be represented correctly here")
        }
        items.forEach { it.render() }
    }

    /** Update declaration content from the rule */
    private fun refreshDeclarations() {
        val propMap = rule.propertyMap // calculate it once
        items.forEach { it.updateDeclaration(propMap) }
    }

    abstract class Item<V : Any>(
        val rule: Rule,
        val property: Property<V>,
    ) {
        private val checkbox = GuiCheckbox("##enabled_${property.name}")
        var declaration: Declaration<V> = Declaration.defaultForProperty(property)
        lateinit var value: V

        private var enabled: Boolean = property in rule.propertyMap

        /**
         * Calculates value from the declaration, to display it in the UI.
         * Note that this can give incorrect results for dynamic values,
         * e.g. if it's %-based.
         */
        protected abstract fun findValue(declaration: Declaration<V>) : V

        fun render() {
            if (!::value.isInitialized) value = findValue(declaration)
            checkbox.render(enabled) {
                enabled = it
                if (enabled) {
                    rule.add(declaration)
                } else {
                    rule.remove(declaration)
                }
            }
            ImGui.sameLine()
            disabled(!enabled) {
                renderInput()
            }
        }

        protected abstract fun renderInput()

        /** Called when the underlying rule was updated from the outside, e.g. history */
        fun updateDeclaration(propMap: PropertyMap) {
            val newDeclaration = propMap[property]
            if (newDeclaration != null) {
                enabled = true
                declaration = newDeclaration
                value = findValue(newDeclaration)
            } else {
                enabled = false
            }
        }
    }

    class ItemNumber(
        rule: Rule,
        property: Property<Number>,
        min: Float = -999f,
        max: Float = 999f,
    ) : Item<Number>(rule, property) {
        private val gui = GuiInputFloat(property.name, min = min, max = max)

        override fun findValue(declaration: Declaration<Number>): Number =
            declaration.value.invoke(0.0, 0L)

        override fun renderInput() {
            gui.render(value.toFloat()) {
                value = gui.newValue
                declaration.value = set(value)
            }
        }
    }

    class ItemEnum<E : Enum<E>>(
        rule: Rule,
        property: Property<E>,
    ) : Item<E>(rule, property) {
        private val values = property.valType.enumConstants
        private val gui = GuiCombo(property.name, *values)

        override fun findValue(declaration: Declaration<E>): E =
            declaration.value.invoke(property.default, 0L)

        override fun renderInput() {
            gui.render(value) {
                value = it
                declaration.value = set(value)
            }
        }
    }
}