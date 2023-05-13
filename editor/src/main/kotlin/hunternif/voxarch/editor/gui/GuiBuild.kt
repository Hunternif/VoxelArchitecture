package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.buildVoxels
import hunternif.voxarch.editor.actions.generateNodes
import imgui.ImGui

class GuiBuild(
    private val app: EditorApp,
) {
    fun render() {
        val width = (ImGui.getContentRegionAvailX() - 2*4) / 2
        accentButton("Build voxels", width = width) {
            app.buildVoxels()
        }
        tooltip("Reset \"build voxels\" and build them again, from the root node.")

        ImGui.sameLine()
        accentButton("Generate nodes", width = width) {
            app.generateNodes()
        }
        tooltip("Reset generated nodes and run Blueprints for all nodes that have them.")
    }
}