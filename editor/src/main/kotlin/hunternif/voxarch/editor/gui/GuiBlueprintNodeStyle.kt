package hunternif.voxarch.editor.gui

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.editor.blueprint.StyleRuleInfoAny
import hunternif.voxarch.editor.blueprint.allRules
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.sandbox.castle.turret.BodyShape
import hunternif.voxarch.sandbox.castle.turret.BottomShape
import hunternif.voxarch.sandbox.castle.turret.RoofShape
import imgui.ImGui

class GuiBlueprintNodeStyle {
    val items: List<Item> = allRules.map {
        when (it.parameterClass) {
            StylePosition::class -> ItemNumber(it)
            StyleSize::class -> ItemNumber(it, min = 0f)
            StyleShape::class -> ItemEnum(it, PolygonShape.SQUARE)
            StyleTurretBodyShape::class -> ItemEnum(it, BodyShape.SQUARE)
            StyleTurretRoofShape::class -> ItemEnum(it, RoofShape.FLAT_BORDERED)
            StyleTurretBottomShape::class -> ItemEnum(it, BottomShape.FLAT)
            else -> ItemGeneric(it)
        }
    }

    abstract class Item(val rule: StyleRuleInfoAny) {
        private val checkbox = GuiCheckbox("##enabled_${rule.name}")
        abstract val value: Any
        var stringRepr: String = "${rule.name}:"
            private set

        var enabled: Boolean = false

        fun render() {
            checkbox.render(enabled) { v -> enabled = v }
            ImGui.sameLine()
            disabled(!enabled) {
                renderInput()
            }
        }

        protected abstract fun renderInput()

        protected fun updateStringRepr() {
            stringRepr = "${rule.name}: ${valueToString()}"
        }
        protected open fun valueToString(): String = value.toString()
    }

    class ItemNumber(
        rule: StyleRuleInfoAny,
        min: Float = -999f,
        max: Float = 999f,
    ) : Item(rule) {
        val gui = GuiInputFloat(rule.name, min, max)
        override var value: Float = 0f
        init { updateStringRepr() }
        override fun renderInput() {
            gui.render(value) {
                value = gui.newValue
                updateStringRepr()
            }
        }
        override fun valueToString() = gui.format.format(value)
    }

    class ItemEnum<E: Enum<E>>(
        rule: StyleRuleInfoAny,
        initialValue: E,
        values: Array<E>
    ) : Item(rule) {
        companion object {
            inline operator fun <reified E: Enum<E>> invoke(
                rule: StyleRuleInfoAny,
                initialValue: E,
            ): ItemEnum<E> = ItemEnum(rule, initialValue, enumValues())
        }
        val gui = GuiCombo(rule.name, *values)
        override var value: E = initialValue
        init { updateStringRepr() }
        override fun renderInput() {
            gui.render(value) {
                value = it
                updateStringRepr()
            }
        }
    }

    class ItemGeneric(rule: StyleRuleInfoAny) : Item(rule) {
        val gui = GuiInputText(rule.name)
        override var value: String = ""
        init { updateStringRepr() }
        override fun renderInput() {
            gui.render(value) {
                value = it
                updateStringRepr()
            }
        }
    }
}