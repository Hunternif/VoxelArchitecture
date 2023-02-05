package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.set
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3

class TransformObjects(
    private val oldData: Map<out SceneObject, TransformData>,
    private val newData: Map<out SceneObject, TransformData>,
    description: String = "Transform nodes",
    icon: String = FontAwesomeIcons.SlidersH,
) : HistoryAction(description, icon) {

    private val hasNodes = newData.keys.any { it is SceneNode }
    private val hasVoxels = newData.keys.any { it is SceneVoxelGroup }

    override fun invoke(app: EditorAppImpl) = app.applyTransform(newData)

    override fun revert(app: EditorAppImpl) = app.applyTransform(oldData)

    private fun EditorAppImpl.applyTransform(
        dataMap: Map<out SceneObject, TransformData>
    ) {
        for ((obj, data) in dataMap) {
            when (obj) {
                is SceneNode -> {
                    obj.node.run {
                        origin = data.origin
                        start = data.start
                        rotationY = data.rotationY
                        size = data.size
                        (this as? Room)?.run {
                            setCentered(data.isCentered)
                        }
                    }
                }
                is SceneVoxelGroup -> {
                    obj.origin.set(data.origin)
                }
            }
            obj.update()
        }
        if (hasNodes) redrawNodes()
        if (hasVoxels) redrawVoxels()
        highlightFace(null)
    }
}

data class TransformData(
    val origin: Vec3,
    val size: Vec3,
    val start: Vec3,
    /** If [isCentered] is true, [start] is ignored. */
    val isCentered: Boolean,
    val rotationY: Double,
)

/** Creates an instance of [TransformData] using current values.
 * Any non-null argument will overwrite the current values. */
fun SceneObject.transformData(
    origin: Vec3? = null,
    size: Vec3? = null,
    start: Vec3? = null,
    /** If [isCentered] is true, [start] is ignored. */
    isCentered: Boolean? = null,
    rotationY: Double? = null,
) = when(this) {
    is SceneNode -> TransformData(
        (origin ?: node.origin).clone(),
        (size ?: node.size).clone(),
        (start ?: node.start).clone(),
        isCentered ?: (node as? Room)?.isCentered() ?: false,
        rotationY ?: node.rotationY,
    )
    is SceneVoxelGroup -> TransformData(
        (origin ?: this.origin).clone(),
        Vec3.ZERO,
        Vec3.ZERO,
        false,
        0.0
    )
    else -> TransformData(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, false, 0.0)
}