package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3

/**
 * Rooms can have size and start modified at the same time.
 */
class ResizeNodes(
    private val oldSizes: Map<SceneNode, Vec3>,
    private val oldStarts: Map<SceneNode, Vec3>,
    private val newSizes: Map<SceneNode, Vec3>,
    private val newStarts: Map<SceneNode, Vec3>,
) : HistoryAction("Resize", Tool.RESIZE.icon) {
    override fun invoke(app: EditorAppImpl) {
        for ((obj, size) in newSizes.entries) {
            obj.node.let {
                it.size = size
                if (it is Room) it.start = newStarts[obj]!!
            }
        }
        app.redrawNodes()
    }

    override fun revert(app: EditorAppImpl) {
        for ((obj, size) in oldSizes.entries) {
            obj.node.let {
                it.size = size
                if (it is Room) it.start = oldStarts[obj]!!
            }
        }
        app.redrawNodes()
    }
}