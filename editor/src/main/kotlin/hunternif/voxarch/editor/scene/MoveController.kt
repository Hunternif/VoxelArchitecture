package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.plan.Node
import hunternif.voxarch.vector.Vec3
import imgui.internal.ImGui
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*

class MoveController(
    private val app: EditorApp,
    private val camera: OrbitalCamera,
) : InputListener {
    // mouse coordinates are relative to window
    private var mouseX = 0f
    private var mouseY = 0f
    private var dragging = false

    private val dragStartWorldPos: Vector3f = Vector3f()
    private val translation: Vector3f = Vector3f()

    /** Selected nodes, excluding children of other selected nodes. */
    private val movingNodes = mutableListOf<Node>()
    /** The Y level we use for horizontal moving. */
    private var floorY = -0.5f
    /** Origins before translation */
    private val origins = mutableMapOf<Node, Vec3>()

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseMove(posX: Double, posY: Double) {
        if (app.currentTool != Tool.MOVE) return
        if (dragging) drag(posX.toFloat(), posY.toFloat())
        mouseX = posX.toFloat()
        mouseY = posY.toFloat()
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseButton(button: Int, action: Int, mods: Int) {
        if (ImGui.isAnyItemHovered()) return
        if (app.currentTool == Tool.MOVE && button == GLFW_MOUSE_BUTTON_1) {
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

        movingNodes.clear()
        app.selectedNodes.filter { !isAnyParentSelected(it) }.forEach {
            movingNodes.add(it)
            origins[it] = it.origin.clone()
        }
        
        if (movingNodes.isEmpty()) return
        var pickedNode = pickClickedNode()
        if (pickedNode == null) {
            movingNodes.sortBy { it.origin.y }
            pickedNode = movingNodes[movingNodes.size / 2]
        }
        floorY = pickedNode.origin.y.toFloat()
        // round() so that it snaps to grid
        dragStartWorldPos.set(camera.projectToFloor(mouseX, mouseY, floorY)).round()
    }

    private fun onMouseUp() {
        dragging = false
    }

    private fun drag(posX: Float, posY: Float) {
        // round() so that it snaps to grid
        val floorPos = camera.projectToFloor(posX, posY, floorY).round()
        translation.set(floorPos.sub(dragStartWorldPos))
        for (node in movingNodes) {
            node.origin.set(origins[node]!! + translation.toVec3())
        }
        app.scene.updateNodeModel()
    }

    private fun pickClickedNode(): Node? {
        val result = Vector2f()
        var minDistance = Float.MAX_VALUE
        var hitNode: Node? = null
        for (node in movingNodes) {
            val inst = app.scene.nodeToInstanceMap[node] ?: continue
            val hit = camera.projectToBox(mouseX, mouseY, inst.start, inst.end, result)
            if (hit && result.x < minDistance) {
                minDistance = result.x
                hitNode = inst.node
            }
        }
        return hitNode
    }

    private fun isAnyParentSelected(node: Node): Boolean {
        var parent = node.parent
        while (parent != null) {
            if (app.selectedNodes.contains(parent)) return true
            parent = parent.parent
        }
        return false
    }
}