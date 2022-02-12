package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.MoveObjectsBuilder
import hunternif.voxarch.editor.actions.moveBuilder
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scene.MoveController.Direction.*
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.round

/** Moves selected objects horizontally. */
class MoveController(
    private val app: EditorApp,
    private val camera: OrbitalCamera,
) : BaseSelectionController(app, camera, Tool.MOVE) {
    private enum class Direction {
        XZ, Y
    }

    private val dragStartWorldPos: Vector3f = Vector3f()
    private val translation: Vector3f = Vector3f()

    /** Selected objects, excluding children of other selected nodes. */
    private val movingList = mutableListOf<SceneObject>()
    /** The Y level we use for horizontal moving. */
    private var floorY = -0.5f
    /** 2D offset applied to cursor position on screen, when calculating drag.
     * It's used when starting drag outside a node, to not accidentally drag it
     * to the horizon. */
    private var cursorOffset = Vector2f()
    /** Directions in which the objects are allowed to move. */
    private var direction = XZ
    /** Builder for the action that will be written to history. */
    private var moveBuilder: MoveObjectsBuilder? = null

    override fun onMouseDown(mods: Int) {
        dragging = true
        movingList.clear()
        movingList.addAll(app.state.selectedObjects.filter { !isAnyParentSelected(it) })

        if (movingList.isEmpty()) {
            selectOneObjectIfEmpty()
            return
        }
        var pickedNode = pickClickedObject()
        if (pickedNode == null) {
            movingList.sortBy { it.start.y }
            val midNode = movingList[movingList.size / 2]
            // set 2D offset
            cursorOffset
                .set(camera.projectToViewport(midNode.floorCenter))
                .sub(mouseX, mouseY)
            pickedNode = midNode
        }
        floorY = round(pickedNode.start.y)
        dragStartWorldPos.set(projectToFloorWithOffset(mouseX, mouseY))
        if (mods and GLFW_MOD_ALT != 0) {
            direction = Y
        }
        moveBuilder = app.moveBuilder(movingList)
    }

    override fun onMouseUp(mods: Int) {
        dragging = false
        cursorOffset.set(0f, 0f)
        direction = XZ
        movingList.clear()
        moveBuilder?.commit()
        moveBuilder = null
    }

    override fun drag(posX: Float, posY: Float) {
        val newPos = when (direction) {
            XZ -> projectToFloorWithOffset(posX, posY)
            Y -> projectToYWithOffset(posX, posY)
        }
        translation.set(newPos.sub(dragStartWorldPos))
        moveBuilder?.setMove(translation)
    }

    private fun pickClickedObject(): SceneObject? {
        val result = Vector2f()
        var minDistance = Float.MAX_VALUE
        var hitObj: SceneObject? = null
        for (obj in movingList) {
            val hit = camera.projectToBox(mouseX, mouseY, obj.start, obj.end, result)
            if (hit && result.x < minDistance) {
                minDistance = result.x
                hitObj = obj
            }
        }
        return hitObj
    }

    private fun isAnyParentSelected(obj: SceneObject): Boolean {
        if (obj !is INested<*>) return false
        var parent = obj.parent
        while (parent != null && parent is SceneObject) {
            if (parent in app.state.selectedObjects) return true
            parent = parent.parent
        }
        return false
    }

    /** See [cursorOffset]. */
    private fun projectToFloorWithOffset(posX: Float, posY: Float): Vector3f =
        camera.projectToFloor(
            posX + cursorOffset.x,
            posY + cursorOffset.y,
            floorY
        ).round() // round() so that it snaps to grid

    /** See [cursorOffset]. */
    private fun projectToYWithOffset(posX: Float, posY: Float): Vector3f =
        camera.projectToY(
            posX + cursorOffset.x,
            posY + cursorOffset.y,
            dragStartWorldPos
        ).apply {
            // Reset X & Z so that the movement is strictly vertical
            x = dragStartWorldPos.x
            z = dragStartWorldPos.z
            round() // round() so that it snaps to grid
        }
}