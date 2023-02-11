package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.util.SnapOrigin
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
                    }
                    obj.snapOrigin = data.snapOrigin
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
    /** Centric size */
    val start: Vec3,
    val rotationY: Double,
    val snapOrigin: SnapOrigin,
)

/** Creates an instance of [TransformData] using current values.
 * Any non-null argument will overwrite the current values. */
fun SceneObject.transformData(
    origin: Vec3? = null,
    size: Vec3? = null, // "centric" size
    start: Vec3? = null,
    rotationY: Double? = null,
    snapOrigin: SnapOrigin? = null,
) = when(this) {
    is SceneNode -> TransformData(
        (origin ?: node.origin).clone(),
        (size ?: node.size).clone(),
        (start ?: node.start).clone(),
        rotationY ?: node.rotationY,
        snapOrigin ?: this.snapOrigin
    )
    is SceneVoxelGroup -> TransformData(
        (origin ?: this.origin).clone(),
        Vec3.ZERO,
        Vec3.ZERO,
        0.0,
        SnapOrigin.OFF
    )
    else -> TransformData(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, 0.0, SnapOrigin.OFF)
}