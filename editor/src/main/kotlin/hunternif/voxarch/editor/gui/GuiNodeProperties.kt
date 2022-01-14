package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.updateNode
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.pushStyleColor
import hunternif.voxarch.editor.util.readFromFloatArray
import hunternif.voxarch.editor.util.writeToFloatArray
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3
import imgui.ImGui
import imgui.flag.ImGuiCol
import org.lwjgl.glfw.GLFW

/**
 * Displays properties of a node, and updates them at an interval.
 */
class GuiNodeProperties(private val app: EditorApp) {
    private var className = ""

    private val originArray = FloatArray(3)

    private val roomStartArray = FloatArray(3)
    private val roomSizeArray = FloatArray(3)
    private val updatedStart = Vec3(0, 0, 0)


    // Update logic
    private var lastUpdateTime: Double = GLFW.glfwGetTime()
    private val updateIntervalSeconds: Double = 0.02


    var node: Node = Node()
        set(value) {
            if (field != value) {
                field = value
                className = value.javaClass.simpleName
                node.origin.writeToFloatArray(originArray)
                if (value is Room) {
                    value.start.writeToFloatArray(roomStartArray)
                    value.size.writeToFloatArray(roomSizeArray)
                }
            }
        }

    fun render() {
        updateIfNeeded()
        ImGui.text(className)
        ImGui.dragFloat3("origin", originArray, 1f)
        val node = node
        if (node is Room) {
            ImGui.dragFloat3("size", roomSizeArray, 1f, 0f, 999f)
            if (node.isCentered()) {
                pushStyleColor(ImGuiCol.Text, dynamicTextColor)
                ImGui.dragFloat3("start (centered)", roomStartArray, 1f)
                ImGui.popStyleColor()
            } else {
                ImGui.dragFloat3("start", roomStartArray, 1f)
            }
        }
    }

    private fun updateIfNeeded() {
        val currentTime = GLFW.glfwGetTime()
        if (currentTime - lastUpdateTime > updateIntervalSeconds) {
            lastUpdateTime = currentTime

            // Perform update
            val node = node
            node.origin.readFromFloatArray(originArray)
            if (node is Room) {
                node.size.readFromFloatArray(roomSizeArray)
                // Room's start is initially unset and calculated dynamically.
                // We need check if we need to update it:
                updatedStart.readFromFloatArray(roomStartArray)
                if (node.start != updatedStart) {
                    node.start = updatedStart
                }
            }

            app.updateNode(node)
        }
    }

    companion object {
        private val dynamicTextColor = ColorRGBa.fromHex(0xffffff, 0.5f)
    }
}