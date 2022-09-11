package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.Room
import imgui.ImGui
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTableFlags
import imgui.type.ImInt
import org.lwjgl.glfw.GLFW

/**
 * Displays properties of a node, and updates them at an interval.
 * TODO: unit test GuiNodeProperties
 */
class GuiObjectProperties(
    private val app: EditorApp,
    private val gui: GuiBase,
) {
    private val originInput = GuiInputVec3("origin")
    private val sizeInput = GuiInputVec3("size", min = 0f)
    private val startInput = GuiInputVec3("start")
    private val centeredInput = GuiCheckbox("centered")

    // Update timer
    private var currentTime: Double = GLFW.glfwGetTime()
    private var lastUpdateTime: Double = GLFW.glfwGetTime()
    private val updateIntervalSeconds: Double = 0.02

    private var obj: SceneObject? = null

    private val generatorIndex = ImInt(-1)
    private val allGenerators by lazy { app.state.generatorNames.toTypedArray() }
    private val curGenerators = mutableListOf<IGenerator>()

    fun render() {
        currentTime = GLFW.glfwGetTime()
        checkSelectedNodes()
        renderHeaderText()

        (obj as? SceneNode)?.let {
            renderNode(it)
            redrawNodesIfNeeded()
        }
        if (currentTime - lastUpdateTime > updateIntervalSeconds) {
            lastUpdateTime = currentTime
        }
    }
    private fun renderNode(sceneNode: SceneNode) {
        val node = sceneNode.node
        // By passing the original Vec3 ref into render(), its value will be
        // updated in real time.
        originInput.render(node.origin) {
            app.transformNodeOrigin(sceneNode, original, newValue)
        }

        if (node is Room) {
            sizeInput.render(node.size) {
                app.transformNodeSize(sceneNode, original, newValue)
            }

            disabled(node.isCentered()) {
                startInput.render(node.start) {
                    app.transformNodeStart(sceneNode, original, newValue)
                }
            }

            centeredInput.render(node.isCentered()) {
                app.transformNodeCentered(sceneNode, it)
            }
        }

        ImGui.separator()
        ImGui.text("Generators")
        ImGui.combo("##Add", generatorIndex, allGenerators)
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 4f, 0f)
        ImGui.sameLine()
        ImGui.popStyleVar()
        button("Add") {
            val i = generatorIndex.get()
            if (i >= 0) {
                app.addGenerator(sceneNode, allGenerators[i])
                generatorIndex.set(-1) // clear to prevent too many generators
            }
        }
        renderGeneratorTable(sceneNode)
    }

    private fun renderGeneratorTable(sceneNode: SceneNode) {
        updateCurrentGenerators(sceneNode)
        if (ImGui.beginTable("generators_table", 2, ImGuiTableFlags.PadOuterX)) {
            ImGui.tableSetupColumn("name")
            // it's not actually 10px wide, selectable makes it wider
            ImGui.tableSetupColumn("remove", ImGuiTableColumnFlags.WidthFixed, 10f)
            curGenerators.forEachIndexed { i, gen ->
                ImGui.tableNextRow()
                ImGui.tableNextColumn()
                ImGui.selectable(gen.javaClass.simpleName)
                ImGui.tableNextColumn()
                gui.inlineIconButton(memoStrWithIndex(FontAwesomeIcons.Times, i)) {
                    app.removeGenerator(sceneNode, gen)
                }
            }
            ImGui.endTable()
        }
    }

    private fun updateCurrentGenerators(sceneNode: SceneNode) = runAtInterval {
        curGenerators.clear()
        curGenerators.addAll(sceneNode.generators)
    }

    /** Check which nodes are currently selected, and update the state of gui */
    private fun checkSelectedNodes() = runAtInterval {
        app.state.selectedObjects.run {
            obj = when (size) {
                0 -> null
                1 -> first()
                else -> null
            }
        }
    }

    private fun renderHeaderText() {
        app.state.selectedObjects.run {
            when (size) {
                0 -> {}
                1 -> {
                    val obj = obj
                    when (obj) {
                        is SceneNode -> ImGui.text(obj.nodeClassName)
                        else -> ImGui.text(obj.toString())
                    }
                    if (obj?.isGenerated == true) {
                        ImGui.sameLine()
                        ImGui.text("(generated)")
                    }
                }
                else -> {
                    ImGui.text(size.toString())
                    ImGui.sameLine()
                    ImGui.text("nodes")
                }
            }
        }
    }

    /** Apply the modified values to the node. */
    private fun redrawNodesIfNeeded() = runAtInterval {
        if (originInput.dirty || sizeInput.dirty || startInput.dirty) {
            app.redrawNodes()
        }
    }

    /** A mechanism to throttle expensive operations to happen less often than
     * every frame. */
    private inline fun runAtInterval(crossinline action: () -> Unit) {
        if (currentTime - lastUpdateTime > updateIntervalSeconds) action()
    }
}