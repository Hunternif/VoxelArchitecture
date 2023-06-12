package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.*
import imgui.ImGui
import imgui.flag.ImGuiStyleVar
import imgui.type.ImBoolean
import kotlin.random.Random

class GuiBuild(
    private val app: EditorApp,
) {
    private val seedInput = GuiInputLong("##seed", speed = 1f)
    private val maxRecInput = GuiInputIntCount("max recursions")

    /**
     * If true, display a single "Build" button.
     * If false, display separate button for nodes and voxels.
     */
    var unifyButtons = ImBoolean(true)

    fun render() {
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 4f, 4f)

        withWidth(100f) {
            seedInput.render(app.state.seed) {
                app.setSeed(newValue)
            }
            ImGui.sameLine()
            smallIconButton(FontAwesomeIcons.DiceThree) {
                app.setSeed(Random.nextInt(0, Int.MAX_VALUE).toLong())
            }
            tooltip("Randomize seed")
            ImGui.sameLine()
            ImGui.text("seed")
        }
        withWidth(124f) {
            maxRecInput.render(app.state.maxRecursions) {
                app.setMaxRecursions(newValue)
            }
        }
        ImGui.popStyleVar()


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