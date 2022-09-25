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
            StyleSize::class -> ItemSize(it)
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
            stringRepr = "${rule.name}: $value"
        }
    }

    class ItemSize(rule: StyleRuleInfoAny) : Item(rule) {
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