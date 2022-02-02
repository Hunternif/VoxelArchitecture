package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.redrawNodes
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scene.models.ResizeNodeModel
import hunternif.voxarch.editor.util.AABBFace
import hunternif.voxarch.editor.util.AADirection3D.*
import hunternif.voxarch.plan.Room
import hunternif.voxarch.util.max
import hunternif.voxarch.vector.Vec3
import org.joml.Vector2f
import org.joml.Vector3f

/** Allows resizing selected Rooms by dragging on their faces.
 * Doesn't handle any other types of objects. */
class ResizeController(
    private val app: EditorApp,
    private val camera: OrbitalCamera,
) : BaseSelectionController(app, camera, Tool.RESIZE) {
    val model = ResizeNodeModel()

    /** Selected nodes that can be resized */
    private val resizingRooms = mutableListOf<SceneNode>()
    /** Starts before resize.
     * Start will be updated instead of size when resized in NEG direction. */
    private val origStarts = mutableMapOf<SceneNode, Vec3>()
    /** Sizes before resize */
    private val origSizes = mutableMapOf<SceneNode, Vec3>()

    private var pickedNode: SceneNode? = null
    var pickedFace: AABBFace? = null

    private val dragStartWorldPos: Vector3f = Vector3f()
    private val dragWorldPos: Vector3f = Vector3f()
    private val translation: Vector3f = Vector3f()

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseMove(posX: Double, posY: Double) {
        super.onMouseMove(posX, posY)
        if (app.state.currentTool == Tool.RESIZE && !dragging)
            hitTest(mouseX, mouseY)
    }

    override fun onMouseDown(mods: Int) {
        resizingRooms.clear()
        app.state.selectedObjects.forEach {
            if (it is SceneNode && it.node is Room) {
                resizingRooms.add(it)
                origSizes[it] = it.node.size.clone()
                origStarts[it] = it.node.start.clone()
            }
        }

        if (resizingRooms.isEmpty()) {
            selectOneObjectIfEmpty()
            return
        }

        pickedFace?.let {
            dragging = true
            // set start position:
            camera.projectToBox(mouseX, mouseY, it.min, it.max, Vector2f(),
                dragStartWorldPos
            )
        }
    }

    override fun onMouseUp(mods: Int) {
        dragging = false
    }

    /** Find if the cursor is hitting any node, and which face on it. */
    private fun hitTest(posX: Float, posY: Float) {
        // 1. Test if we hit any node
        val result = Vector2f()
        var minDistance = Float.MAX_VALUE
        pickedNode = null
        for (obj in app.state.selectedObjects) {
            if (obj !is SceneNode) continue
            val hit = camera.projectToBox(posX, posY, obj.start, obj.end, result)
            if (hit && result.x < minDistance) {
                minDistance = result.x
                pickedNode = obj
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
        if (pickedNode == null)
            pickedFace = null
        model.face = pickedFace
    }

    override fun drag(posX: Float, posY: Float) {
        pickedFace?.let { face ->
            when (face.dir) {
                POS_X, NEG_X -> dragWorldPos.set(
                    camera.projectToX(posX, posY, dragStartWorldPos)
                )
                POS_Y, NEG_Y -> dragWorldPos.set(
                    camera.projectToY(posX, posY, dragStartWorldPos)
                )
                POS_Z, NEG_Z -> dragWorldPos.set(
                    camera.projectToZ(posX, posY, dragStartWorldPos)
                )
            }
            // round() so that it snaps to grid
            translation.set(dragWorldPos).sub(dragStartWorldPos).round()
            for (obj in resizingRooms) {
                val origSize = origSizes[obj]!!
                val delta = Vec3(0, 0, 0)
                when (face.dir) {
                    POS_X -> delta.x = translation.x.toDouble()
                    POS_Y -> delta.y = translation.y.toDouble()
                    POS_Z -> delta.z = translation.z.toDouble()
                    NEG_X -> delta.x = -translation.x.toDouble()
                    NEG_Y -> delta.y = -translation.y.toDouble()
                    NEG_Z -> delta.z = -translation.z.toDouble()
                }
                // TODO: update size on the actual Node on mouse-up.
                val room = obj.node as Room
                if (room.isCentered()) {
                    delta.x *= 2
                    delta.z *= 2
                    room.size = max(Vec3.ZERO, origSize + delta)
                } else {
                    room.size = max(Vec3.ZERO, origSize + delta)
                    when (face.dir) {
                        POS_X, POS_Y, POS_Z -> {}
                        NEG_X, NEG_Y, NEG_Z -> {
                            room.start = origStarts[obj]!! + origSize - room.size
                        }
                    }
                }
                obj.update()
            }
            app.redrawNodes()
            pickedNode?.run {
                // update face instance
                updateFaces()
                pickedFace = faces[face.dir.ordinal]
                model.face = pickedFace
            }
        }
    }
}