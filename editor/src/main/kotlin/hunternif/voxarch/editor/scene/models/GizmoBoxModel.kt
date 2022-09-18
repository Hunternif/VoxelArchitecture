package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.IModel
import hunternif.voxarch.editor.util.ColorRGBa
import org.joml.Matrix4f
import org.joml.Vector3f

/**
 * Renders a box with outline and highlighted axes.
 */
class GizmoBoxModel : IModel {
    private lateinit var gizmoModel: GizmoModel
    private lateinit var outlineModel: BoxFrameModel
    private lateinit var boxModel: BoxInstancedModel<Box>

    override fun init() {
        gizmoModel = GizmoModel(
            Vector3f(-0.5f, -0.5f, -0.5f),
            1f,
            lineWidth = 2f
        ).apply {
            addPos(Vector3f(0f, 0f, 0f))
            init()
        }
        outlineModel = BoxFrameModel(
            lineWidth = 2f
        ).apply {
            add(Box(
                Vector3f(-0.5f, -0.5f, -0.5f),
                Vector3f(1f, 1f, 1f),
                ColorRGBa.fromHex(0xffffff, 0.3f)
            ))
            init()
        }
        boxModel = BoxInstancedModel<Box>().apply {
            add(Box(
                Vector3f(-0.5f, -0.5f, -0.5f),
                Vector3f(1f, 1f, 1f),
                ColorRGBa.fromHex(0xffffff, 0.1f)
            ))
            init()
        }
    }

    override fun runFrame(viewProj: Matrix4f) {
        outlineModel.runFrame(viewProj)
        boxModel.runFrame(viewProj)
        gizmoModel.runFrame(viewProj)
    }
}