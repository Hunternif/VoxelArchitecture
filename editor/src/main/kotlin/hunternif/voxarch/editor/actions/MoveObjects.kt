package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.editor.scene.SceneVoxelGroup
import hunternif.voxarch.editor.util.add
import hunternif.voxarch.editor.util.sub
import hunternif.voxarch.vector.Vec3

class MoveObjects(
    private val objs: Collection<SceneObject>,
    private val move: Vec3,
) : HistoryAction("Move", Tool.MOVE.icon) {

    private val movingNodes = objs.any { it is SceneNode }
    private val movingVoxels = objs.any { it is SceneVoxelGroup }

    override fun invoke(app: EditorAppImpl) {
        for (obj in objs) {
            when (obj) {
                is SceneNode -> obj.node.origin.addLocal(move)
                is SceneVoxelGroup -> obj.origin.add(move)
            }
            obj.update()
        }
        if (movingNodes) app.redrawNodes()
        if (movingVoxels) app.redrawVoxels()
    }

    override fun revert(app: EditorAppImpl) {
        for (obj in objs) {
            when (obj) {
                is SceneNode -> obj.node.origin.subtractLocal(move)
                is SceneVoxelGroup -> obj.origin.sub(move)
            }
            obj.update()
        }
        if (movingNodes) app.redrawNodes()
        if (movingVoxels) app.redrawVoxels()
    }
}