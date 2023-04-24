package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.IModel
import hunternif.voxarch.editor.scene.models.box.BoxFrameModel
import hunternif.voxarch.editor.scene.models.box.BoxMesh
import org.joml.Matrix4f
import org.joml.Vector3f

class SelectedNodeModel : IModel {
    private val gizmoModel = GizmoModel(
        Vector3f(-0.5f, -0.5f, -0.5f),
        lineWidth = 2f
    )
    private val lineModel = BoxFrameModel(Colors.selectedNodeOutline)

    // Rendered through voxels
    private val bgLineModel = BoxFrameModel(Colors.selectedNodeOutline.copy(a = 0.3f))
    private val bgGizmoModel = GizmoModel(
        Vector3f(-0.5f, -0.5f, -0.5f),
        lineWidth = 2f,
        alpha = 0.3f
    )

    override fun init() {
        lineModel.apply {
            init()
            writeDepth = false
            shader.depthOffset = -0.001f
        }
        gizmoModel.apply {
            init()
            writeDepth = false
            shader.depthOffset = -0.001f
        }
        bgLineModel.apply {
            init()
            readDepth = false
            shader.depthOffset = 0.001f
        }
        bgGizmoModel.apply {
            init()
            readDepth = false
            shader.depthOffset = 0.001f
        }
    }

    fun add(ref: Any, box: BoxMesh) {
        bgLineModel.add(ref, box)
        lineModel.add(ref, box)
        gizmoModel.addPos(ref, box.center, box.size, box.angleY)
        bgGizmoModel.addPos(ref, box.center, box.size, box.angleY)
    }

    fun remove(ref: Any) {
        lineModel.remove(ref)
        bgLineModel.remove(ref)
        gizmoModel.remove(ref)
        bgGizmoModel.remove(ref)
    }

    fun clear() {
        bgLineModel.clear()
        lineModel.clear()
        gizmoModel.clear()
        bgGizmoModel.clear()
    }

    fun update() {
        bgLineModel.update()
        lineModel.update()
        gizmoModel.update()
        bgGizmoModel.update()
    }

    override fun runFrame(viewProj: Matrix4f) {
        bgLineModel.runFrame(viewProj)
        bgGizmoModel.runFrame(viewProj)
        lineModel.runFrame(viewProj)
        gizmoModel.runFrame(viewProj)
    }
}