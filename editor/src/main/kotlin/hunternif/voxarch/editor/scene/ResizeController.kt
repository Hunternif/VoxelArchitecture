package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.ResizeNodesBuilder
import hunternif.voxarch.editor.actions.highlightFace
import hunternif.voxarch.editor.actions.resizeBuilder
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.util.AABBFace
import hunternif.voxarch.editor.util.AADirection3D.*
import hunternif.voxarch.plan.Room
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*

/** Allows resizing selected Rooms by dragging on their faces.
 * Doesn't handle any other types of objects. */
class ResizeController(
    private val app: EditorApp,
    private val camera: OrbitalCamera,
) : BaseSelectionController(app, camera, Tool.RESIZE) {

    /** Builder for the action that will be written to history. */
    private var resizeBuilder: ResizeNodesBuilder? = null
    /** If true, the node will be resized symmetrically around its center. */
    private var symmetric = false

    private var pickedNode: SceneNode? = null
    var pickedFace: AABBFace? = null

    private val dragStartWorldPos: Vector3f = Vector3f()
    private val dragWorldPos: Vector3f = Vector3f()
    private val translation: Vector3f = Vector3f()

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseMove(posX: Double, posY: Double) {
        super.onMouseMove(posX, posY)
        if (app.state.currentTool == Tool.RESIZE) {
            if (!dragging) hitTest(mouseX, mouseY)
        } else {
            app.highlightFace(null)
        }
    }

    override fun onMouseDown(mods: Int) {
        val resizingRooms = app.state.selectedObjects
            .filter { it is SceneNode && it.node is Room }

        if (resizingRooms.isEmpty()) {
            selectOneObjectIfEmpty()
            return
        }

        pickedFace?.let { face ->
            dragging = true
            // set start position:
            camera.projectToAABox(mouseX, mouseY, face.min, face.max, Vector2f(),
                dragStartWorldPos
            )
            resizeBuilder = app.resizeBuilder(resizingRooms)
        }

        if (mods and GLFW_MOD_ALT != 0) {
            symmetric = true
        }
    }

    override fun onMouseUp(mods: Int) {
        dragging = false
        resizeBuilder?.commit()
        resizeBuilder = null
        symmetric = false
    }

    /** Find if the cursor is hitting any node, and which face on it. */
    private fun hitTest(posX: Float, posY: Float) {
        // 1. Test if we hit any node
        val result = Vector2f()
        var minDistance = Float.MAX_VALUE
        pickedNode = null
        for (obj in app.state.selectedObjects) {
            if (obj !is SceneNode) continue
            val hit = camera.projectToBox(posX, posY, obj.box, result)
            if (hit && result.x < minDistance) {
                minDistance = result.x
                pickedNode = obj
            }
        }

        // 2. Test which face we hit on it
        pickedNode?.apply {
            minDistance = Float.MAX_VALUE
            for (face in aabb.faces) {
                val hit = camera.projectToAABox(posX, posY, face.min, face.max, result)
                if (hit && result.x < minDistance) {
                    minDistance = result.x
                    pickedFace = face
                }
            }
        }
        if (pickedNode == null)
            pickedFace = null
        app.highlightFace(pickedFace)
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
            val delta = translation.run {
                when (face.dir) {
                    POS_X -> x
                    POS_Y -> y
                    POS_Z -> z
                    NEG_X -> -x
                    NEG_Y -> -y
                    NEG_Z -> -z
                }
            }
            resizeBuilder?.dragFace(face.dir, delta, symmetric)
            pickedNode?.run {
                // update face instance
                aabb.updateFaces()
                pickedFace = aabb.faces[face.dir.ordinal]
                app.highlightFace(pickedFace)
            }
        }
    }
}