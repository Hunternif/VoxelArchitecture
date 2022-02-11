package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.redrawNodes
import hunternif.voxarch.editor.actions.redrawVoxels
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
    /** 2D offset applied to cursor position on screen, when calculating drag.
     * It's used when starting drag outside a node, to not accidentally drag it
     * to the horizon. */
    private var cursorOffset = Vector2f()

    private var movingNodes = false
    private var movingVoxels = false

    override fun onMouseDown(mods: Int) {
        dragging = true
        movingNodes = false
        movingVoxels = false
        movingList.clear()
        app.state.selectedObjects.filter { !isAnyParentSelected(it) }.forEach {
            movingList.add(it)
            origins[it] = when (it) {
                is SceneNode -> {
                    movingNodes = true
                    it.node.origin.clone()
                }
                is SceneVoxelGroup -> {
                    movingVoxels = true
                    it.origin.toVec3()
                }
                else -> it.start.toVec3()
            }
        }
        
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
    }

    override fun onMouseUp(mods: Int) {
        dragging = false
        cursorOffset.set(0f, 0f)
    }

    override fun drag(posX: Float, posY: Float) {
        val floorPos = projectToFloorWithOffset(posX, posY)
        translation.set(floorPos.sub(dragStartWorldPos))
        for (obj in movingList) {
            val newOrigin = origins[obj]!! + translation.toVec3()
            // TODO: update origin on the actual Node on mouse-up.
            when (obj) {
                is SceneNode -> obj.node.origin.set(newOrigin)
                is SceneVoxelGroup -> obj.origin.set(newOrigin)
            }
            obj.update()
        }
        if (movingNodes) app.redrawNodes()
        if (movingVoxels) app.redrawVoxels()
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
}