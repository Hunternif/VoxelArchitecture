package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import org.joml.Vector3f

class MoveObjectsBuilder(
    app: EditorAppImpl,
    objs: Collection<SceneObject>,
) : HistoryActionBuilder(app) {

    private val oldData = LinkedHashMap<SceneObject, TransformData>()
    private val newData = LinkedHashMap<SceneObject, TransformData>()

    init {
        for (obj in objs) {
            oldData[obj] = obj.transformData()
            newData[obj] = obj.transformData()
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
        for ((obj, data) in oldData) {
            val newPos = data.position.add(x, y, z)
            when (obj) {
                is SceneNode -> obj.node.position.set(newPos)
                is SceneVoxelGroup -> obj.position.set(newPos)
            }
            newData[obj]!!.run {
                position.set(newPos)
            }
            obj.update()
        }
        if (movingNodes) app.redrawNodes()
        if (movingVoxels) app.redrawVoxels()
    }

    private fun makeDescription(): String = when (newData.size) {
        1 -> "Move"
        else -> "Move ${newData.size} objects"
    }

    override fun build() = TransformObjects(
        oldData, newData, makeDescription(), Tool.MOVE.icon
    )

    override fun commit() {
        // only commit if the move is non-zero
        if (newData.any { (obj, data) -> oldData[obj] != data }) super.commit()
    }
}