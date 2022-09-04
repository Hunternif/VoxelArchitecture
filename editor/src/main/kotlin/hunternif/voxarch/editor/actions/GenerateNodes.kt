package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.DetachedObject
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.detached
import hunternif.voxarch.plan.Node

/** Creates new nodes from all generators attached to nodes in the scene. */
class GenerateNodes : HistoryAction(
    "Generate nodes",
    FontAwesomeIcons.Archway
) {
    private lateinit var oldGenerated: List<DetachedObject>
    private lateinit var newGenerated: MutableList<DetachedObject>

    override fun invoke(app: EditorAppImpl) {
        if (!::oldGenerated.isInitialized) {
            oldGenerated = app.state.generatedNodes.map { it.detached() }
        }
        app.clearGeneratedNodes()
        if (!::newGenerated.isInitialized) {
            newGenerated = mutableListOf()
            app.runGeneratorsRecursive(app.state.rootNode)
        }
        app.state.sceneTree.attachAll(newGenerated)
        newGenerated.forEach { app.state.generatedNodes.add(it.node as SceneNode) }
        app.redrawNodes()
    }

    override fun revert(app: EditorAppImpl) {
        app.clearGeneratedNodes()
        app.state.sceneTree.attachAll(oldGenerated)
        oldGenerated.forEach { app.state.generatedNodes.add(it.node as SceneNode) }
        app.redrawNodes()
    }

    /** Remove all generated nodes from the scene. */
    private fun EditorAppImpl.clearGeneratedNodes() = state.run {
        sceneTree.detachAll(generatedNodes)
        hiddenObjects.removeAll(generatedNodes)
        manuallyHiddenObjects.removeAll(generatedNodes)
        selectedObjects.removeAll(generatedNodes)
        generatedNodes.forEach { it.parent?.removeChild(it) }
        generatedNodes.clear()
    }

    /** Run all generators on the current node, and repeat for all its children.
     * Any new [Node]s created in the process are treated as 'generated',
     * and are added to [newGenerated]. */
    private fun EditorAppImpl.runGeneratorsRecursive(root: SceneNode) {
        val prevChildSet = root.children.filterIsInstance<SceneNode>().map { it.node }.toSet()
        root.generators.forEach { it.generate(root.node) }
        // Create SceneNodes for the new nodes
        val newNodes = root.node.children.filter { it !in prevChildSet }
        root.children.filterIsInstance<SceneNode>().forEach { runGeneratorsRecursive(it) }
        newNodes.forEach { createSceneNodesRecursive(root, it) }
    }

    /** Create [SceneNode] for the given [newNode], and all its children,
     * and ste [parent] as its parent. */
    private fun EditorAppImpl.createSceneNodesRecursive(
        parent: SceneNode, newNode: Node
    ) {
        val sceneNode = SceneNode(newNode, Colors.defaultGeneratedNodeBox, true)
        sceneNode.update()
        sceneNode.parent = parent
        newGenerated.add(sceneNode.detached())
        newNode.children.forEach { createSceneNodesRecursive(sceneNode, it) }
    }
}