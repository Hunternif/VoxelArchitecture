package hunternif.voxarch.editor.gui

import hunternif.voxarch.dom.style.StyleParameter
import hunternif.voxarch.editor.blueprint.StyleRuleInfo
import hunternif.voxarch.editor.blueprint.allRules
import hunternif.voxarch.plan.Node

class GuiBlueprintNodeStyle {
    val items = allRules.map {
        Item(
            it,
            GuiCheckbox("##enabled_${it.name}"),
            GuiInputText(it.name)
        )
    }

    class Item<N : Node, P : StyleParameter, V : Any>(
        val rule: StyleRuleInfo<N, P, V>,
        val checkbox: GuiCheckbox,
        val text: GuiInputText,
        var enabled: Boolean = false,
        var value: String = "",
    )
}