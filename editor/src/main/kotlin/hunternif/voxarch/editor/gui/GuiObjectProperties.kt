package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.plan.Room
import imgui.ImGui
import org.lwjgl.glfw.GLFW

/**
 * Displays properties of a node, and updates them at an interval.
 * TODO: unit test GuiNodeProperties
 */
class GuiObjectProperties(private val app: EditorApp) {
    private val originInput = GuiInputVec3("origin")
    private val sizeInput = GuiInputVec3("size", min = 0f)
    private val startInput = GuiInputVec3("start")
    private val centeredInput = GuiCheckbox("centered")

    // Update timer
    private var lastUpdateTime: Double = GLFW.glfwGetTime()
    private val updateIntervalSeconds: Double = 0.02

    private var text: String = ""

    private var obj: SceneObject? = null

    fun render() {
        checkSelectedNodes()
        ImGui.text(text)

        (obj as? SceneNode)?.let {
            renderNode(it)
            redrawNodesIfNeeded()
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

            if (node.isCentered()) ImGui.beginDisabled()
            startInput.render(node.start) {
                app.transformNodeStart(sceneNode, original, newValue)
            }
            if (node.isCentered()) ImGui.endDisabled()

            centeredInput.render(node.isCentered()) {
                app.transformNodeCentered(sceneNode, it)
            }
        }
    }

    /** Check which nodes are currently selected, and update the state of gui */
    private fun checkSelectedNodes() {
        app.state.selectedObjects.run {
            when (size) {
                0 -> {
                    obj = null
                    text = ""
                }
                1 -> {
                    obj = first()
                    text = obj?.toText() ?: ""
                }
                else -> {
                    obj = null
                    text = "$size nodes"
                }
            }
        }
    }

    private fun SceneObject.toText(): String {
        return if (this is SceneNode) node.javaClass.simpleName else ""
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
}