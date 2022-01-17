package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scene.models.NodeModel.NodeData
import hunternif.voxarch.editor.scene.models.ResizeNodeModel
import hunternif.voxarch.editor.scene.models.boxFaces
import hunternif.voxarch.editor.util.AABBFace
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3
import imgui.internal.ImGui
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*

class ResizeController(
    private val app: EditorApp,
    private val camera: OrbitalCamera,
) : InputListener {
    val model = ResizeNodeModel()

    // mouse coordinates are relative to window
    private var mouseX = 0f
    private var mouseY = 0f
    private var dragging = false

    /** Selected nodes that can be resized */
    private val resizingRooms = mutableListOf<Room>()
    /** Sizes before resize */
    private val originSizes = mutableMapOf<Node, Vec3>()

    private var pickedNode: NodeData? = null
    var pickedFace: AABBFace? = null

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseMove(posX: Double, posY: Double) {
        if (app.currentTool != Tool.RESIZE) return
        if (dragging) drag(posX.toFloat(), posY.toFloat())
        else hitTest(posX.toFloat(), posY.toFloat())
        mouseX = posX.toFloat()
        mouseY = posY.toFloat()
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseButton(button: Int, action: Int, mods: Int) {
        if (ImGui.isAnyItemHovered()) return
        if (app.currentTool == Tool.RESIZE && button == GLFW_MOUSE_BUTTON_1) {
            if (action == GLFW_PRESS && camera.vp.contains(mouseX, mouseY)
            ) {
                onMouseDown()
            } else if (action == GLFW_RELEASE) {
                onMouseUp()
            }
        }
    }

    private fun onMouseDown() {
        dragging = true

        resizingRooms.clear()
        app.selectedNodes.filterIsInstance<Room>().forEach {
            resizingRooms.add(it)
            originSizes[it] = it.origin.clone()
        }

        if (resizingRooms.isEmpty()) return
    }

    private fun onMouseUp() {
        dragging = false
    }

    /** Find if the cursor is hitting any node, and which face on it. */
    private fun hitTest(posX: Float, posY: Float) {
        // 1. Test if we hit any node
        val result = Vector2f()
        var minDistance = Float.MAX_VALUE
        pickedNode = null
        for (node in app.selectedNodes) {
            val inst = app.scene.nodeToInstanceMap[node] ?: continue
            val hit = camera.projectToBox(posX, posY, inst.start, inst.end, result)
            if (hit && result.x < minDistance) {
                minDistance = result.x
                pickedNode = inst
            }
        }

        // 2. Test which face we hit on it
        pickedNode?.apply {
            minDistance = Float.MAX_VALUE
            for (face in faces) {
                val hit = camera.projectToBox(posX, posY, face.min, face.max, result)
                if (hit && result.x < minDistance) {
                    minDistance = result.x
                    pickedFace = face
                }
            }
        }
        model.face = pickedFace
        if (pickedNode == null)
            pickedFace = null
    }

    private fun drag(posX: Float, posY: Float) {
    }

    private fun NodeData.updateFaces() {
        val newFaces = boxFaces(start, end, 0.5f)
        newFaces.forEachIndexed { i, face -> faces[i] = face }
    }
}