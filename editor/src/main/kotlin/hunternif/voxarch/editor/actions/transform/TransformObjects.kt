package hunternif.voxarch.editor.actions.transform

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.highlightFace
import hunternif.voxarch.editor.actions.redrawNodes
import hunternif.voxarch.editor.actions.redrawVoxels
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.ColorRGBa
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

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) = app.applyTransform(newData)

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
                    obj.color.set(data.color)
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
    val color: ColorRGBa,
)

/** Creates an instance of [TransformData] using current values.
 * Any non-null argument will overwrite the current values. */
fun SceneObject.transformData(
    origin: Vec3? = null,
    size: Vec3? = null, // "centric" size
    start: Vec3? = null,
    rotationY: Double? = null,
    snapOrigin: SnapOrigin? = null,
    color: ColorRGBa? = null,
) = when(this) {
    is SceneNode -> TransformData(
        (origin ?: node.origin).clone(),
        (size ?: node.size).clone(),
        (start ?: node.start).clone(),
        rotationY ?: node.rotationY,
        snapOrigin ?: this.snapOrigin,
        (color ?: this.color).copy(),
    )
    is SceneVoxelGroup -> TransformData(
        (origin ?: this.origin).clone(),
        Vec3.ZERO,
        Vec3.ZERO,
        0.0,
        SnapOrigin.OFF,
        this.color.copy(),
    )
    else -> TransformData(
        Vec3.ZERO,
        Vec3.ZERO,
        Vec3.ZERO,
        0.0,
        SnapOrigin.OFF,
        this.color.copy(),
    )
}