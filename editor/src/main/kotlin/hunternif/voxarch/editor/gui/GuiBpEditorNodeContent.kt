package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.blueprint.BlueprintNode
import imgui.ImGui

class GuiBpEditorNodeContent(
    val node: BlueprintNode,
) {
    private val styleMenu = GuiBlueprintNodeStyle(node)

    fun render() {
        styleMenu.items.forEach { item ->
            if (item.enabled) {
                ImGui.bulletText(item.stringRepr)
            }
        }
    }

    fun renderStyleMenu() {
        ImGui.pushItemWidth(150f)
        text("Style Rules")
        ImGui.separator()
        styleMenu.items.forEach { it.render() }
    }
}