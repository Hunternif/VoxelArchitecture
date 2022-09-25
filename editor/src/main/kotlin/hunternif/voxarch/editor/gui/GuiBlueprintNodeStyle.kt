package hunternif.voxarch.editor.gui

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.StyleRuleInfo
import hunternif.voxarch.editor.blueprint.StyleRuleInfoAny
import hunternif.voxarch.editor.blueprint.allRules
import hunternif.voxarch.generator.ChainedGenerator
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.sandbox.castle.turret.BodyShape
import hunternif.voxarch.sandbox.castle.turret.BottomShape
import hunternif.voxarch.sandbox.castle.turret.RoofShape
import imgui.ImGui

class GuiBlueprintNodeStyle(
    private val node: BlueprintNode
) {
    val items: List<Item<*>> = allRules.mapNotNull {
        when (it.parameterClass) {
            StylePosition::class -> ItemNumber(this, it)
            StyleSize::class -> ItemNumber(this, it, min = 0f)
            StyleShape::class -> ItemEnum(this, it, PolygonShape.SQUARE)
            StyleTurretBodyShape::class -> ItemEnum(this, it, BodyShape.SQUARE)
            StyleTurretRoofShape::class -> ItemEnum(this, it, RoofShape.FLAT_BORDERED)
            StyleTurretBottomShape::class -> ItemEnum(this, it, BottomShape.FLAT)
            else -> null
        }
    }

    fun updateStylesheet() {
        val gen = node.generator as? ChainedGenerator ?: return
        gen.localStyle.clear()
        items.filter { it.enabled }
            .forEach { it.addStyle(gen.localStyle) }
    }

    abstract class Item<V: Any>(
        protected val style: GuiBlueprintNodeStyle,
        val rule: StyleRuleInfo<*, *, V>,
    ) {
        private val checkbox = GuiCheckbox("##enabled_${rule.name}")
        abstract val value: Any
        var stringRepr: String = "${rule.name}:"
            private set

        var enabled: Boolean = false

        fun render() {
            checkbox.render(enabled) {
                enabled = it
                style.updateStylesheet()
            }
            ImGui.sameLine()
            disabled(!enabled) {
                renderInput()
            }
        }
        protected abstract fun renderInput()

        fun addStyle(stylesheet: Stylesheet) {
            rule.addStyleForNodeClass(stylesheet) {
                applyStyle()
            }
        }
        abstract fun StyleParameter.applyStyle(): V

        protected fun updateStringRepr() {
            stringRepr = "${rule.name}: ${valueToString()}"
        }
        protected open fun valueToString(): String = value.toString()
    }

    class ItemNumber(
        style: GuiBlueprintNodeStyle,
        rule: StyleRuleInfoAny,
        min: Float = -999f,
        max: Float = 999f,
    ) : Item<Dimension>(style, rule as StyleRuleInfo<*, *, Dimension>) {
        val gui = GuiInputFloat(rule.name, min, max)
        override var value: Float = 0f
        init { updateStringRepr() }
        override fun renderInput() {
            gui.render(value) {
                value = gui.newValue
                updateStringRepr()
                style.updateStylesheet()
            }
        }
        /** Returns value in voxels */
        override fun StyleParameter.applyStyle(): Dimension = value.vx
        override fun valueToString() = gui.format.format(value)
    }

    class ItemEnum<E: Enum<E>>(
        style: GuiBlueprintNodeStyle,
        rule: StyleRuleInfoAny,
        initialValue: E,
        values: Array<E>,
    ) : Item<Option<E>>(style, rule as StyleRuleInfo<*, *, Option<E>>) {
        companion object {
            inline operator fun <reified E: Enum<E>> invoke(
                style: GuiBlueprintNodeStyle,
                rule: StyleRuleInfoAny,
                initialValue: E,
            ): ItemEnum<E> =
                ItemEnum(style, rule, initialValue, enumValues())
        }
        val gui = GuiCombo(rule.name, *values)
        override var value: E = initialValue
        init { updateStringRepr() }
        override fun renderInput() {
            gui.render(value) {
                value = it
                updateStringRepr()
                style.updateStylesheet()
            }
        }
        override fun StyleParameter.applyStyle(): Option<E> = set(value)
    }
}