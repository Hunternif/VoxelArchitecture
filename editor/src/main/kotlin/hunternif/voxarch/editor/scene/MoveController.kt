package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.redrawNodes
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.util.set
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.vector.Vec3
import org.joml.Vector2f
import org.joml.Vector3f
import kotlin.math.round

/** Moves selected objects horizontally. */
class MoveController(
    private val app: EditorApp,
    private val camera: OrbitalCamera,
) : BaseSelectionController(app, camera, Tool.MOVE) {

    private val dragStartWorldPos: Vector3f = Vector3f()
    private val translation: Vector3f = Vector3f()

    /** Selected objects, excluding children of other selected nodes. */
    private val movingList = mutableListOf<SceneObject>()
    /** The Y level we use for horizontal moving. */
    private var floorY = -0.5f
    /** Origins (starts) before translation */
    private val origins = mutableMapOf<SceneObject, Vec3>()

    override fun onMouseDown(mods: Int) {
        dragging = true

        movingList.clear()
        app.state.selectedObjects.filter { !isAnyParentSelected(it) }.forEach {
            movingList.add(it)
            if (it is SceneNode) {
                origins[it] = it.node.origin.clone()
            } else {
                origins[it] = it.start.toVec3()
            }
        }
        
        if (movingList.isEmpty()) {
            selectOneObjectIfEmpty()
            return
        }
        var pickedNode = pickClickedObject()
        if (pickedNode == null) {
            movingList.sortBy { it.start.y }
            pickedNode = movingList[movingList.size / 2]
        }
        floorY = round(pickedNode.start.y)
        // round() so that it snaps to grid
        dragStartWorldPos.set(camera.projectToFloor(mouseX, mouseY, floorY)).round()
    }

    override fun onMouseUp(mods: Int) {
        dragging = false
    }

    override fun drag(posX: Float, posY: Float) {
        // round() so that it snaps to grid
        val floorPos = camera.projectToFloor(posX, posY, floorY).round()
        translation.set(floorPos.sub(dragStartWorldPos))
        for (obj in movingList) {
            val newOrigin = origins[obj]!! + translation.toVec3()
            // TODO: update origin on the actual Node on mouse-up.
            if (obj is SceneNode) {
                obj.node.origin.set(newOrigin)
                obj.update()
            } else {
                obj.start.set(newOrigin)
            }
        }
        app.redrawNodes()
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
        if (obj !is SceneNode) return false
        var parent = obj.parent
        while (parent != null) {
            if (parent in app.state.selectedObjects) return true
            parent = parent.parent
        }
        return false
    }
}