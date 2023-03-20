package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.plan.Node

/**
 * Adds a hierarchy of nodes to the scene, with all their children.
 * Creates new scenes objects for them.
 */
class AddNodes(
    private val parent: SceneNode,
    private val nodes: List<Node>
) : HistoryAction(
    "Add nodes",
    FontAwesomeIcons.Vihara,
) {
    private lateinit var newSceneNodes: MutableSet<SceneNode>

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::newSceneNodes.isInitialized) {
            newSceneNodes = LinkedHashSet()
            for (node in nodes) {
                newSceneNodes.add(app.state.registry.createNodes(node))
            }
        }
        parent.attachAll(newSceneNodes)
        app.redrawNodes()
    }

    override fun revert(app: EditorAppImpl) {
        newSceneNodes.forEach { it.remove() }
    }
}