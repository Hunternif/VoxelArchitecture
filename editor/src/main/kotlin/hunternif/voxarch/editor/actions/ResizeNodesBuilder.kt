package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.scene.SceneObject
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
import kotlin.math.max

class ResizeNodesBuilder(
    app: EditorAppImpl,
    objs: Collection<SceneObject>,
) : HistoryActionBuilder(app) {

    private val oldSizes = LinkedHashMap<SceneNode, Vec3>()
    private val oldStarts = LinkedHashMap<SceneNode, Vec3>()
    private val newSizes = LinkedHashMap<SceneNode, Vec3>()
    private val newStarts = LinkedHashMap<SceneNode, Vec3>()

    init {
        for (obj in objs) {
            if (obj is SceneNode && obj.node is Room) {
                oldSizes[obj] = obj.node.size.clone()
                newSizes[obj] = obj.node.size.clone()
                oldStarts[obj] = obj.node.start.clone()
                newStarts[obj] = obj.node.start.clone()
            }
        }
    }

    /**
     * Called when dragging one of the room's faces along its normal.
     * @param dir determines the face that was dragged.
     * @param delta how far it was dragged (in the direction [dir])
     *              vs the initial position.
     */
    fun dragFace(dir: AADirection3D, delta: Float) {
        val deltaVec = when (dir) {
            POS_X, NEG_X -> Vec3(delta, 0f, 0f)
            POS_Y, NEG_Y -> Vec3(0f, delta, 0f)
            POS_Z, NEG_Z -> Vec3(0f, 0f, delta)
        }
        for ((obj, oldSize) in oldSizes) {
            val room = obj.node as Room
            if (room.isCentered()) {
                // For a centered room, multiply XZ size deltas by 2,
                // because the opposite face is also moving.
                room.size.apply {
                    x = max(0.0, oldSize.x + deltaVec.x * 2)
                    y = max(0.0, oldSize.y + deltaVec.y)
                    z = max(0.0, oldSize.z + deltaVec.z * 2)
                }
            } else {
                room.size = max(Vec3.ZERO, oldSize + deltaVec)
                // For non-centered rooms, may need to update [start]
                when (dir) {
                    POS_X, POS_Y, POS_Z -> {}
                    NEG_X, NEG_Y, NEG_Z -> {
                        room.start = oldStarts[obj]!! + oldSize - room.size
                    }
                }
            }
            newSizes[obj]!!.set(room.size)
            newStarts[obj]!!.set(room.start)
            obj.update()
        }
        app.redrawNodes()
    }

    override fun build() = ResizeNodes(oldSizes, oldStarts, newSizes, newStarts)

    override fun commit() {
        // only commit if the resize is non-zero
        if (newSizes.any { (obj, size) -> oldSizes[obj] != size }) super.commit()
    }
}