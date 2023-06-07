package hunternif.voxarch.editor.actions.transform

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.history.HistoryActionBuilder
import hunternif.voxarch.editor.actions.redrawNodes
import hunternif.voxarch.editor.actions.redrawVoxels
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.plan.findGlobalRotation
import hunternif.voxarch.util.rotateY
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

    fun setMove(vec: Vector3f){
        val delta = vec.toVec3()
        for ((obj, data) in oldData) {
            val newOrigin = when (obj) {
                is SceneNode -> {
                    val angle = obj.node.parent?.findGlobalRotation() ?: 0.0
                    obj.node.origin.set(data.origin + delta.rotateY(-angle))
                }
                is SceneVoxelGroup -> obj.origin.set(data.origin + delta)
                else -> data.origin + delta
            }
            newData[obj]!!.run {
                origin.set(newOrigin)
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