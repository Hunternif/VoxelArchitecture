package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.editor.scene.SceneVoxelGroup
import hunternif.voxarch.editor.util.set
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3f

class MoveObjectsBuilder(
    app: EditorAppImpl,
    private val objs: Collection<SceneObject>,
) : HistoryActionBuilder(app) {

    private val move = Vec3(0, 0, 0)

    /** Origins (starts) before translation */
    private val origins: Map<SceneObject, Vec3> = objs.associateWith {
        when(it) {
            is SceneNode -> it.node.origin.clone()
            is SceneVoxelGroup -> it.origin.toVec3()
            else -> it.start.toVec3()
        }
    }

    private val movingNodes = objs.any { it is SceneNode }
    private val movingVoxels = objs.any { it is SceneVoxelGroup }

    fun setMove(vec: Vector3f) = setMove(
        vec.x.toDouble(),
        vec.y.toDouble(),
        vec.z.toDouble()
    )

    private fun setMove(x: Double, y: Double, z: Double) {
        move.set(x, y, z)
        for (obj in objs) {
            val newOrigin = origins[obj]!!.add(x, y, z)
            when (obj) {
                is SceneNode -> obj.node.origin.set(newOrigin)
                is SceneVoxelGroup -> obj.origin.set(newOrigin)
            }
            obj.update()
        }
        if (movingNodes) app.redrawNodes()
        if (movingVoxels) app.redrawVoxels()
    }

    override fun build() = MoveObjects(objs, move)

    override fun commit() {
        // only commit if the move is non-zero
        if (move != Vec3.ZERO) super.commit()
    }
}