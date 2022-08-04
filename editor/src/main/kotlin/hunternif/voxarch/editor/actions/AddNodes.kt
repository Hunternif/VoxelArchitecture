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

    override fun invoke(app: EditorAppImpl) {
        if (!::newSceneNodes.isInitialized) {
            newSceneNodes = LinkedHashSet()
            for (node in nodes) {
                makeSceneNodeRecursive(parent, node)
            }
        }
        app.state.sceneObjects.addAll(newSceneNodes)
        newSceneNodes.forEach { it.parent?.addChild(it) }
        app.redrawNodes()
    }

    override fun revert(app: EditorAppImpl) {
        app.state.sceneObjects.removeAll(newSceneNodes)
        newSceneNodes.forEach { it.parent?.removeChild(it) }
    }

    private fun makeSceneNodeRecursive(parent: SceneNode?, node: Node): SceneNode {
        val sceneNode = SceneNode(node)
        parent?.addChild(sceneNode)
        newSceneNodes.add(sceneNode)
        for (child in node.children) {
            makeSceneNodeRecursive(sceneNode, child)
        }
        return sceneNode
    }
}