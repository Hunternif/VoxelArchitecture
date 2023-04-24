package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.setSelectedObject
import hunternif.voxarch.editor.scenegraph.SceneObject
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*

/**
 * Contains common methods for controllers of tools operating on selected nodes.
 * E.g. when you click on a node, you automatically select it.
 */
abstract class BaseSelectionController(
    private val app: EditorApp,
    val hitTester: HitTester,
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
        if (app.state.currentTool == tool && button == GLFW_MOUSE_BUTTON_1) {
            if (action == GLFW_PRESS) {
                onMouseDown(mods)
                hitTester.snapshotVoxelTexture()
            } else if (action == GLFW_RELEASE && dragging) {
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
    /** coordinates passed are relative to window */
    protected open fun drag(posX: Float, posY: Float) {}

    /** If no objects are selected, select one that the cursor is hovering. */
    protected fun selectOneObjectIfEmpty() {
        if (app.state.selectedObjects.isEmpty())
            hitTest()?.let { app.setSelectedObject(it) }
    }

    /** Returns the closest object under cursor,
     * or null if the cursor is hovering above empty space*/
    protected fun hitTest(): SceneObject? {
        val result = Vector2f()
        var minDistance = Float.MAX_VALUE
        var hitObj: SceneObject? = null
        for (obj in app.state.sceneObjects) {
            if (obj in app.state.hiddenObjects) continue
            val hit = hitTester.hitTest(obj, mouseX, mouseY, result)
            if (hit && result.x < minDistance) {
                minDistance = result.x
                hitObj = obj
            }
        }
        return hitObj
    }
}