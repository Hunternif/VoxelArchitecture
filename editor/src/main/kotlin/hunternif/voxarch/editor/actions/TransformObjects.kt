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
                        position = data.position
                        start = data.start
                        rotationY = data.rotationY
                        size = data.size
                    }
                    obj.snapOrigin = data.snapOrigin
                }
                is SceneVoxelGroup -> {
                    obj.position.set(data.position)
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
    val position: Vec3,
    val size: Vec3,
    val start: Vec3,
    val rotationY: Double,
    val snapOrigin: SnapOrigin,
)

/** Creates an instance of [TransformData] using current values.
 * Any non-null argument will overwrite the current values. */
fun SceneObject.transformData(
    position: Vec3? = null,
    size: Vec3? = null,
    start: Vec3? = null,
    rotationY: Double? = null,
    snapOrigin: SnapOrigin? = null,
) = when(this) {
    is SceneNode -> TransformData(
        (position ?: node.position).clone(),
        (size ?: node.size).clone(),
        (start ?: node.start).clone(),
        rotationY ?: node.rotationY,
        snapOrigin ?: this.snapOrigin
    )
    is SceneVoxelGroup -> TransformData(
        (position ?: this.position).clone(),
        Vec3.ZERO,
        Vec3.ZERO,
        0.0,
        SnapOrigin.OFF
    )
    else -> TransformData(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, 0.0, SnapOrigin.OFF)
}