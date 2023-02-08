package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.util.AADirection3D
import hunternif.voxarch.editor.util.AADirection3D.*
import hunternif.voxarch.util.max
import hunternif.voxarch.util.rotateY
import hunternif.voxarch.util.snapOrigin
import hunternif.voxarch.vector.Vec3
import kotlin.collections.Collection
import kotlin.collections.LinkedHashMap
import kotlin.collections.any
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.math.abs

class ResizeNodesBuilder(
    app: EditorAppImpl,
    objs: Collection<SceneObject>,
) : HistoryActionBuilder(app) {

    // TODO: include children in old data because snap origin can change their origins
    private val oldData = LinkedHashMap<SceneNode, TransformData>()
    private val newData = LinkedHashMap<SceneNode, TransformData>()

    init {
        for (obj in objs) {
            if (obj is SceneNode) {
                oldData[obj] = obj.transformData()
                newData[obj] = obj.transformData()
            }
        }
    }

    /**
     * Called when dragging one of the node's faces along its normal.
     * @param dir determines the face that was dragged.
     * @param delta how far it was dragged (in the direction [dir])
     *              vs the initial position.
     * @param symmetric whether to resize the opposite side symmetrically.
     */
    fun dragFace(dir: AADirection3D, delta: Float, symmetric: Boolean = false) {
        if (abs(delta) < 1f) return
        val deltaVec = when (dir) {
            POS_X, NEG_X -> Vec3(delta, 0f, 0f)
            POS_Y, NEG_Y -> Vec3(0f, delta, 0f)
            POS_Z, NEG_Z -> Vec3(0f, 0f, delta)
        }
        for ((obj, data) in oldData) {
            val node = obj.node
            // in local space:
            val posDelta = (node.position - data.position).rotateY(-node.rotationY)
            if (symmetric) {
                node.size = max(Vec3.ZERO, data.size + deltaVec * 2)
                val localCenter = data.start + data.size / 2
                // Move [start] to keep the center in place
                node.start = localCenter - (node.size) / 2  - posDelta
            } else {
                node.size = max(Vec3.ZERO, data.size + deltaVec)
                // Move [start] to keep the opposite face in place.
                when (dir) {
                    POS_X, POS_Y, POS_Z -> {}
                    NEG_X, NEG_Y, NEG_Z -> node.start =
                        data.start + data.size - node.size - posDelta
                }
            }
            node.snapOrigin(data.snapOrigin)
            newData[obj]!!.run {
                position.set(node.position)
                size.set(node.size)
                start.set(node.start)
            }
            obj.update()
        }
        app.redrawNodes()
    }

    private fun makeDescription(): String = when (newData.size) {
        1 -> "Resize"
        else -> "Resize ${newData.size} objects"
    }

    override fun build() = TransformObjects(
        oldData, newData, makeDescription(), Tool.RESIZE.icon
    )

    override fun commit() {
        // only commit if the resize is non-zero
        if (newData.any { (obj, data) -> oldData[obj] != data }) super.commit()
    }
}