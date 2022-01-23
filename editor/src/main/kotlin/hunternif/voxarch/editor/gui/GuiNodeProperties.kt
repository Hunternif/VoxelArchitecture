package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.redrawNodes
import hunternif.voxarch.editor.updateNode
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.pushStyleColor
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import imgui.ImGui
import imgui.flag.ImGuiCol
import org.lwjgl.glfw.GLFW

/**
 * Displays properties of a node, and updates them at an interval.
 * TODO: unit test GuiNodeProperties
 */
class GuiNodeProperties(private val app: EditorApp) {
    private val originInput = GuiInputVec3("origin")
    private val sizeInput = GuiInputVec3("size", min = 0f)
    private val startInput = GuiInputVec3("start")

    // Update timer
    private var lastUpdateTime: Double = GLFW.glfwGetTime()
    private val updateIntervalSeconds: Double = 0.02

    private var text: String = ""

    private var node: Node? = Node()

    fun render() {
        checkSelectedNodes()
        ImGui.text(text)
        val node = node ?: return
        redrawNodesIfNeeded()

        originInput.render(node.origin) { app.updateNode(node) }

        if (node is Room) {
            sizeInput.render(node.size) { app.updateNode(node) }

            if (node.isCentered()) {
                pushStyleColor(ImGuiCol.Text, dynamicTextColor)
                startInput.render(node.start) { app.updateNode(node) }
                ImGui.popStyleColor()
            } else {
                startInput.render(node.start) { app.updateNode(node) }
            }
        }
    }

    /** Check which nodes are currently selected, and update the state of gui */
    private fun checkSelectedNodes() {
        app.selectedNodes.run {
            when (size) {
                0 -> {
                    node = null
                    text = ""
                }
                1 -> {
                    node = first()
                    text = node?.javaClass?.simpleName ?: ""
                }
                else -> {
                    node = null
                    text = "$size nodes"
                }
            }
        }
    }

    /** Apply the modified values to the node. */
    private fun redrawNodesIfNeeded() {
        val currentTime = GLFW.glfwGetTime()
        if (currentTime - lastUpdateTime > updateIntervalSeconds) {
            lastUpdateTime = currentTime

            if (originInput.dirty || sizeInput.dirty || startInput.dirty) {
                app.redrawNodes()
            }
        }
    }

    companion object {
        private val dynamicTextColor = ColorRGBa.fromHex(0xffffff, 0.5f)
    }
}