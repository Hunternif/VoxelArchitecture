package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.util.AADirection3D
import hunternif.voxarch.editor.util.AADirection3D.*
import hunternif.voxarch.plan.Room
import hunternif.voxarch.util.max
import hunternif.voxarch.vector.Vec3
import kotlin.collections.Collection
import kotlin.collections.LinkedHashMap
import kotlin.collections.any
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class ResizeNodesBuilder(
    app: EditorAppImpl,
    objs: Collection<SceneObject>,
) : HistoryActionBuilder(app) {

    private val oldData = LinkedHashMap<SceneNode, TransformData>()
    private val newData = LinkedHashMap<SceneNode, TransformData>()

    init {
        for (obj in objs) {
            if (obj is SceneNode && obj.node is Room) {
                oldData[obj] = obj.transformData()
                newData[obj] = obj.transformData()
            }
        }
    }

    /**
     * Called when dragging one of the room's faces along its normal.
     * @param dir determines the face that was dragged.
     * @param delta how far it was dragged (in the direction [dir])
     *              vs the initial position.
     * @param symmetric whether to resize the opposite side symmetrically.
     */
    fun dragFace(dir: AADirection3D, delta: Float, symmetric: Boolean = false) {
        val deltaVec = when (dir) {
            POS_X, NEG_X -> Vec3(delta, 0f, 0f)
            POS_Y, NEG_Y -> Vec3(0f, delta, 0f)
            POS_Z, NEG_Z -> Vec3(0f, 0f, delta)
        }
        for ((obj, data) in oldData) {
            val room = obj.node as Room
            if (symmetric) {
                room.size = max(Vec3.ZERO, data.size + deltaVec * 2)
                if (room.rotationY == 0.0) {
                    //TODO: should we move origin for rotated boxes too?
                    val center = data.origin + data.start + data.size / 2
                    if (room.isCentered()) {
                        // Need to move origin when resizing vertically
                        room.origin = center.addY(-room.size.y / 2)
                    } else {
                        // Move [origin] to keep the center in place
                        room.origin = center - (room.size) / 2 - data.start
                    }
                }
            } else {
                room.size = max(Vec3.ZERO, data.size + deltaVec)
                if (room.rotationY == 0.0) {
                    if (room.isCentered()) {
                        // Move [origin] to keep the opposite face in place.
                        when (dir) {
                            POS_X, POS_Y, POS_Z -> room.origin
                                .set(data.origin)
                                .addLocal(data.start)
                                .addLocal(room.size.x / 2, 0.0, room.size.z / 2)
                            NEG_X, NEG_Y, NEG_Z -> room.origin
                                .set(data.origin)
                                .addLocal(data.size.x / 2, data.size.y, data.size.z / 2)
                                .addLocal(-room.size.x / 2, -room.size.y, -room.size.z / 2)
                        }
                    } else {
                        // Move [origin] to keep the opposite face in place.
                        when (dir) {
                            POS_X, POS_Y, POS_Z -> {}
                            NEG_X, NEG_Y, NEG_Z -> room.origin =
                                data.origin + data.size - room.size
                        }
                    }
                }
            }
            newData[obj]!!.run {
                origin.set(room.origin)
                size.set(room.size)
                start.set(room.start)
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