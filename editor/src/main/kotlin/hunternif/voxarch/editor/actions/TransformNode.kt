package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3

class TransformNode(
    private val obj: SceneNode,
    private val oldData: NodeTransformData,
    private val newData: NodeTransformData,
    description: String = "Transform node",
) : HistoryAction(description, FontAwesomeIcons.SlidersH) {

    override fun invoke(app: EditorAppImpl) = app.applyTransform(newData)

    override fun revert(app: EditorAppImpl) = app.applyTransform(oldData)

    private fun EditorAppImpl.applyTransform(data: NodeTransformData) {
        obj.node.run {
            origin = data.origin
            size = data.size
            (this as? Room)?.run {
                setCentered(data.isCentered)
                if (!data.isCentered) start = data.start
            }
        }
        redrawNodes()
        highlightFace(null)
    }
}

data class NodeTransformData(
    val origin: Vec3,
    val size: Vec3,
    val start: Vec3,
    /** If [isCentered] is true, [start] is ignored. */
    val isCentered: Boolean,
)

/** Creates an instance of [NodeTransformData] using current values.
 * Any non-null argument will overwrite the current values. */
fun SceneNode.transformData(
    origin: Vec3? = null,
    size: Vec3? = null,
    start: Vec3? = null,
    /** If [isCentered] is true, [start] is ignored. */
    isCentered: Boolean? = null,
) = NodeTransformData(
    (origin ?: node.origin).clone(),
    (size ?: node.size).clone(),
    (start ?: (node as? Room)?.start ?: Vec3.ZERO).clone(),
    isCentered ?: (node as? Room)?.isCentered() ?: false,
)