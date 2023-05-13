package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.buildNodesAndVoxels
import hunternif.voxarch.editor.actions.buildVoxels
import hunternif.voxarch.editor.actions.generateNodes
import hunternif.voxarch.editor.actions.setSeed
import imgui.ImGui
import imgui.type.ImBoolean

class GuiBuild(
    private val app: EditorApp,
) {
    private val seedInput = GuiInputLong("seed")

    /**
     * If true, display a single "Build" button.
     * If false, display separate button for nodes and voxels.
     */
    var unifyButtons = ImBoolean(true)

    fun render() {
        seedInput.render(app.state.seed) {
            app.setSeed(newValue)
        }


        if (unifyButtons.get()) {
            accentButton("Build", width = ImGui.getContentRegionAvailX()) {
                app.buildNodesAndVoxels()
            }
            tooltip("Reset all generated content, run Blueprints on all nodes, then build voxels.")
        } else {
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
}