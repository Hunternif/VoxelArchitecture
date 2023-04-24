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
    private val bgLineModel = BoxFrameModel(Colors.selectedNodeOutline.copy(a = 0.1f))

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
    }

    fun add(box: BoxMesh) {
        bgLineModel.add(box)
        lineModel.add(box)
        gizmoModel.addPos(box.center, box.size, box.angleY)
    }

    fun clear() {
        bgLineModel.clear()
        lineModel.clear()
        gizmoModel.clear()
    }

    fun update() {
        bgLineModel.update()
        lineModel.update()
        gizmoModel.update()
    }

    override fun runFrame(viewProj: Matrix4f) {
        bgLineModel.runFrame(viewProj)
        lineModel.runFrame(viewProj)
        gizmoModel.runFrame(viewProj)
    }
}