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
    description: String = "Resize",
) : HistoryAction(description, Tool.RESIZE.icon) {

    override fun invoke(app: EditorAppImpl) =
        app.applyResize(newSizes, newStarts)

    override fun revert(app: EditorAppImpl) =
        app.applyResize(oldSizes, oldStarts)

    private fun EditorAppImpl.applyResize(
        sizes: Map<SceneNode, Vec3>,
        starts: Map<SceneNode, Vec3>,
    ) {
        for ((obj, size) in sizes.entries) {
            obj.node.let {
                it.size = size
                if (it is Room) it.start = starts[obj]!!
            }
            obj.update()
        }
        redrawNodes()
        highlightFace(null)
    }
}