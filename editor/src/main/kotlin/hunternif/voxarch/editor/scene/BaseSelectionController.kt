package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.actions.setSelectedNode
import hunternif.voxarch.plan.Node
import imgui.ImGui
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*

/**
 * Contains common methods for controllers of tools operating on selected nodes.
 * E.g. when you click on a node, you automatically select it.
 */
abstract class BaseSelectionController(
    private val app: EditorApp,
    private val camera: OrbitalCamera,
    private val tool: Tool,
) : MouseListener {
    // mouse coordinates are relative to window
    protected var mouseX = 0f
    protected var mouseY = 0f
    protected var dragging = false

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseMove(posX: Double, posY: Double) {
        if (app.state.currentTool != tool) return
        if (dragging) drag(posX.toFloat(), posY.toFloat())
        mouseX = posX.toFloat()
        mouseY = posY.toFloat()
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseButton(button: Int, action: Int, mods: Int) {
        if (ImGui.isAnyItemHovered()) return
        if (app.state.currentTool == tool && button == GLFW_MOUSE_BUTTON_1 &&
            camera.vp.contains(mouseX, mouseY)
        ) {
            if (action == GLFW_PRESS) {
                onMouseDown(mods)
            } else if (action == GLFW_RELEASE) {
                onMouseUp(mods)
            }
        }
    }

    protected open fun onMouseDown(mods: Int) {
        dragging = true
    }
    protected open fun onMouseUp(mods: Int) {
        dragging = false
    }
    protected open fun drag(posX: Float, posY: Float) {}

    /** If no nodes are selected, select one that the cursor is hovering. */
    protected fun selectNodeIfEmpty() {
        if (app.state.selectedNodes.isEmpty())
            app.setSelectedNode(hitTestNode())
    }

    /** Returns the closest node under cursor,
     * or null if the cursor is hovering above empty space*/
    protected fun hitTestNode(): Node? {
        val result = Vector2f()
        var minDistance = Float.MAX_VALUE
        var hitNode: Node? = null
        for (inst in app.state.nodeDataMap.values) {
            val hit = camera.projectToBox(mouseX, mouseY, inst.start, inst.end, result)
            if (hit && result.x < minDistance) {
                minDistance = result.x
                hitNode = inst.node
            }
        }
        return hitNode
    }
}