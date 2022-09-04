package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.AppStateImpl
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.util.max
import hunternif.voxarch.editor.util.min
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.plan.centeredRoom
import hunternif.voxarch.plan.findGlobalPosition
import hunternif.voxarch.plan.room
import org.joml.Vector3i

class CreateRoom(
    private val start: Vector3i,
    private val end: Vector3i,
    private val centered: Boolean = false,
) : HistoryAction("Create room", Tool.ADD_NODE.icon) {

    lateinit var node: SceneNode
        private set
    lateinit var parent: SceneNode

    override fun invoke(app: EditorAppImpl) = app.state.run {
        if (!::node.isInitialized) {
            createRoom()
        }
        sceneTree.attach(parent, node)
        app.redrawNodes()
    }

    override fun revert(app: EditorAppImpl) = app.state.run {
        sceneTree.detach(node)
        app.redrawNodes()
    }

    private fun AppStateImpl.createRoom() {
        // ensure size is positive
        val min = min(start, end).toVec3()
        val max = max(start, end).toVec3()
        val size = max - min
        val mid = min.add(size.x / 2, 0.0, size.z / 2)
        val room = parentNode.node.run {
            val globalPos = findGlobalPosition()
            if (centered) {
                centeredRoom(mid - globalPos, size)
            } else {
                room(min - globalPos, max - globalPos)
            }
        }
        node = SceneNode(room)
        parent = parentNode
    }
}