package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.blueprint.BlueprintNode
import imgui.ImGui

/**
 * Renders Blueprint node style in Style text editor.
 */
class GuiBlueprintNodeStyleAsText(
    private val app: EditorApp,
    private val node: BlueprintNode,
) {
    fun render() {
        ImGui.text("Style text editor goes here")
    }
}