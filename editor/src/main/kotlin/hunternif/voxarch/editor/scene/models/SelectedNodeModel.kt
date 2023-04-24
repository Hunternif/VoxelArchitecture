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

    override fun init() {
        lineModel.apply {
            init()
            writeDepth = false
        }
        gizmoModel.apply {
            init()
            writeDepth = false
        }
    }

    fun add(ref: Any, box: BoxMesh) {
        lineModel.add(ref, box)
        gizmoModel.addPos(ref, box.center, box.size, box.angleY)
    }

    fun remove(ref: Any) {
        lineModel.remove(ref)
        gizmoModel.remove(ref)
    }

    fun clear() {
        lineModel.clear()
        gizmoModel.clear()
    }

    fun update() {
        lineModel.update()
        gizmoModel.update()
    }

    override fun runFrame(viewProj: Matrix4f) {
        lineModel.runFrame(viewProj)
        gizmoModel.runFrame(viewProj)
    }
}