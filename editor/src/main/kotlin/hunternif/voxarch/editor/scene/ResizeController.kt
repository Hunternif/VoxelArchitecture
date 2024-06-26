package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.transform.ResizeNodesBuilder
import hunternif.voxarch.editor.actions.highlightFace
import hunternif.voxarch.editor.actions.resizeBuilder
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scene.models.box.BoxFace
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.plan.findGlobalRotation
import hunternif.voxarch.util.toRadians
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*

/** Allows resizing selected Rooms by dragging on their faces.
 * Doesn't handle any other types of objects. */
class ResizeController(
    private val app: EditorApp,
    private val camera: OrbitalCamera,
    hitTester: HitTester,
) : BaseSelectionController(app, hitTester, Tool.RESIZE) {

    /** Builder for the action that will be written to history. */
    private var resizeBuilder: ResizeNodesBuilder? = null
    /** If true, the node will be resized symmetrically around its center. */
    private var symmetric = false

    private var pickedNode: SceneNode? = null
    /** Global rotation of picked node in radians */
    private var nodeAngleRad: Float = 0f
    var pickedFace: BoxFace? = null

    // Temporary vectors
    private val dragStartWorldPos: Vector3f = Vector3f()
    private val dragWorldPos: Vector3f = Vector3f()
    private val translation: Vector3f = Vector3f()
    /** Holds the normal to the picked face in world coordinates */
    private val faceNormal: Vector3f = Vector3f()

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
            .filterIsInstance<SceneNode>()

        if (resizingRooms.isEmpty()) {
            selectOneObjectIfEmpty()
            return
        }

        pickedFace?.let { face ->
            dragging = true
            // set start position:
            camera.projectToBox(mouseX, mouseY, face, Vector2f(),
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
            val hit = hitTester.hitTest(obj, posX, posY, result)
            if (hit && result.x < minDistance) {
                minDistance = result.x
                pickedNode = obj
                nodeAngleRad = obj.node.findGlobalRotation().toRadians().toFloat()
            }
        }

        // 2. Test which face we hit on it
        pickedNode?.apply {
            minDistance = Float.MAX_VALUE
            for (face in box.faces) {
                val hit = camera.projectToBox(posX, posY, face, result)
                if (hit && result.x < minDistance) {
                    minDistance = result.x
                    pickedFace = face
                    face.dir.vec.rotateY(nodeAngleRad, faceNormal)
                }
            }
        }
        if (pickedNode == null)
            pickedFace = null
        app.highlightFace(pickedFace)
    }

    override fun drag(posX: Float, posY: Float) {
        pickedFace?.let { face ->
            dragWorldPos.set(
                camera.projectToAxis(posX, posY, face.normal, dragStartWorldPos)
            )
            // round() so that it snaps to grid
            translation.set(dragWorldPos).sub(dragStartWorldPos).round()
            // find how far we've moved away from the face:
            val delta = translation.dot(faceNormal)
            resizeBuilder?.dragFace(face.dir, delta, symmetric)
            pickedNode?.run {
                // update the highlight position
                app.highlightFace(pickedFace)
            }
        }
    }
}