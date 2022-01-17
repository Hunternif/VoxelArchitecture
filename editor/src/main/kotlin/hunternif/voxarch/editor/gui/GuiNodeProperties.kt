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
 * TODO: unit test GuiNodeProperties
 */
class GuiNodeProperties(private val app: EditorApp) {
    private val originArray = FloatArray(3)

    private val roomStartArray = FloatArray(3)
    private val roomSizeArray = FloatArray(3)
    private val updatedStart = Vec3(0, 0, 0)


    // Origin values
    private val origOrigin = Vec3(0, 0, 0)
    private val origRoomSize = Vec3(0, 0, 0)
    private val origRoomStart = Vec3(0, 0, 0)
    private var origCentered = false
    private var dirty = false


    // Update timer
    private var lastUpdateTime: Double = GLFW.glfwGetTime()
    private val updateIntervalSeconds: Double = 0.02

    var text: String = ""

    var node: Node? = Node()
        set(value) {
            if (field != value) {
                field = value
                text = field?.javaClass?.simpleName ?: ""
                updateFloatArrays()
                updateOriginalValues()
            }
        }

    private fun updateFloatArrays() {
        val node = node ?: return
        node.origin.writeToFloatArray(originArray)
        if (node is Room) {
            node.start.writeToFloatArray(roomStartArray)
            node.size.writeToFloatArray(roomSizeArray)
        }
    }

    private fun updateOriginalValues() {
        val node = node ?: return
        origOrigin.set(node.origin)
        if (node is Room) {
            origRoomSize.set(node.size)
            origRoomStart.set(node.start)
            origCentered = node.isCentered()
        }
        dirty = false
    }

    private fun revertToOriginalValues() {
        val node = node ?: return
        node.origin.set(origOrigin)
        if (node is Room) {
            node.size.set(origRoomSize)
            if (origCentered) node.recenter()
            else node.start.set(origRoomStart)
        }
        updateFloatArrays()
        dirty = false
        app.updateNode(node)
    }

    private fun markDirty() { dirty = true }

    fun render() {
        ImGui.text(text)
        val node = node ?: return
        updateIfNeeded()
        if (ImGui.dragFloat3("origin", originArray, 1f)) markDirty()
        if (node is Room) {
            if (ImGui.dragFloat3("size", roomSizeArray, 1f, 0f, 999f)) markDirty()
            if (node.isCentered()) {
                pushStyleColor(ImGuiCol.Text, dynamicTextColor)
                if (ImGui.dragFloat3("start (centered)", roomStartArray, 1f)) markDirty()
                ImGui.popStyleColor()
            } else {
                if (ImGui.dragFloat3("start", roomStartArray, 1f)) markDirty()
            }
        }
        if (dirty) {
            if (ImGui.button("Apply")) updateOriginalValues()
            ImGui.sameLine()
            if (ImGui.button("Revert")) revertToOriginalValues()
        }
    }

    private fun updateIfNeeded() {
        val currentTime = GLFW.glfwGetTime()
        if (currentTime - lastUpdateTime > updateIntervalSeconds) {
            lastUpdateTime = currentTime

            if (dirty) {
                // Update values on the actual node
                val node = node ?: return
                node.origin.readFromFloatArray(originArray)
                if (node is Room) {
                    node.size.readFromFloatArray(roomSizeArray)
                    // Room's start is initially unset and calculated dynamically.
                    // We need check if we need to update it:
                    updatedStart.readFromFloatArray(roomStartArray)
                    if (origRoomStart != updatedStart) {
                        node.start = updatedStart
                    }
                }
                app.updateNode(node)
            } else {
                // Update values in case the node was modified elsewhere
                //TODO: ensure that node size etc can be updated outside this window
                updateFloatArrays()
            }
        }
    }

    companion object {
        private val dynamicTextColor = ColorRGBa.fromHex(0xffffff, 0.5f)
    }
}