package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.AppStateImpl
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.blueprint.nodeFactoryByName
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.util.max
import hunternif.voxarch.editor.util.min
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.plan.*
import hunternif.voxarch.util.SnapOrigin
import org.joml.Vector3i

class CreateNode(
    private val start: Vector3i,
    private val end: Vector3i,
    private val centered: Boolean = false,
    private val type: String = "Node",
) : HistoryAction("Create node", Tool.ADD_NODE.icon) {

    lateinit var node: SceneNode
        private set
    lateinit var parent: SceneNode

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) = app.state.run {
        if (!::node.isInitialized) {
            createNode()
        }
        parent.addChild(node)
        app.redrawNodes()
    }

    override fun revert(app: EditorAppImpl) = app.state.run {
        node.remove()
        app.redrawNodes()
    }

    private fun AppStateImpl.createNode() {
        // ensure size is positive
        val min = min(start, end).toVec3()
        val max = max(start, end).toVec3()
        val node = nodeFactoryByName[type]?.invoke() ?: Node()
        val globalPos = parentNode.node.findGlobalPosition()
        parentNode.node.addChild(node)
        node.minPoint = min - globalPos
        node.size = max - min
        this@CreateNode.node = registry.newNode(node).also {
            if (centered) it.snapOrigin = SnapOrigin.FLOOR_CENTER
        }
        parent = parentNode
    }
}