package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.IModel
import hunternif.voxarch.editor.scene.models.box.AABBoxFrameModel
import hunternif.voxarch.editor.scene.models.box.AABBoxInstancedModel
import hunternif.voxarch.editor.scene.models.box.AABBoxMesh
import hunternif.voxarch.editor.util.ColorRGBa
import org.joml.Matrix4f
import org.joml.Vector3f

/**
 * Renders a box with outline and highlighted axes.
 */
class GizmoBoxModel : IModel {
    private lateinit var gizmoModel: GizmoModel
    private lateinit var outlineModel: AABBoxFrameModel
    private lateinit var boxModel: AABBoxInstancedModel<AABBoxMesh>

    override fun init() {
        gizmoModel = GizmoModel(
            Vector3f(-0.5f, -0.5f, -0.5f),
            lineWidth = 2f
        ).apply {
            addPos(this, Vector3f(0f, 0f, 0f), Vector3f(1f, 1f, 1f))
            init()
        }
        outlineModel = AABBoxFrameModel(
            lineWidth = 2f
        ).apply {
            add(AABBoxMesh(
                Vector3f(-0.5f, -0.5f, -0.5f),
                Vector3f(1f, 1f, 1f),
                ColorRGBa.fromHex(0xffffff, 0.3f)
            ))
            init()
        }
        boxModel = AABBoxInstancedModel<AABBoxMesh>().apply {
            add(AABBoxMesh(
                Vector3f(-0.5f, -0.5f, -0.5f),
                Vector3f(1f, 1f, 1f),
                ColorRGBa.fromHex(0xffffff, 0.1f)
            ))
            init()
        }
        gizmoModel.readDepth = false
        outlineModel.readDepth = false
    }

    override fun runFrame(viewProj: Matrix4f) {
        outlineModel.runFrame(viewProj)
        boxModel.runFrame(viewProj)
        gizmoModel.runFrame(viewProj)
    }
}