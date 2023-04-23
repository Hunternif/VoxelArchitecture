package hunternif.voxarch.editor.actions.scene

import hunternif.voxarch.dom.builder.IDomListener
import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.GenEvent
import hunternif.voxarch.editor.actions.SceneEvent
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.logInfo
import hunternif.voxarch.editor.actions.redrawNodes
import hunternif.voxarch.editor.blueprint.PropBlueprint
import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.DetachedObject
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.detached
import hunternif.voxarch.plan.Node

/** Creates new nodes from all blueprints attached to nodes in the scene. */
class GenerateNodes : HistoryAction(
    "Generate nodes",
    FontAwesomeIcons.Archway
), GenEvent, SceneEvent {
    private lateinit var oldGenerated: List<DetachedObject>
    private lateinit var newGenerated: MutableList<DetachedObject>

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::oldGenerated.isInitialized) {
            oldGenerated = app.state.generatedNodes.map { it.detached() }
        }
        app.clearGeneratedNodes()
        if (!::newGenerated.isInitialized) {
            newGenerated = mutableListOf()
            PropBlueprint.updateBlueprints(app.state.blueprints)
            app.runBlueprintsRecursive(app.state.rootNode)
        }
        newGenerated.forEach {
            it.reattach()
            app.state.generatedNodes.add(it.obj as SceneNode)
        }
        app.redrawNodes()
    }

    override fun revert(app: EditorAppImpl) {
        app.clearGeneratedNodes()
        oldGenerated.forEach {
            it.reattach()
            app.state.generatedNodes.add(it.obj as SceneNode)
        }
        app.redrawNodes()
    }

    /** Remove all generated nodes from the scene. */
    private fun EditorAppImpl.clearGeneratedNodes() = state.run {
        generatedNodes.toList().forEach { it.remove() }
        generatedNodes.clear()
    }

    /** Run all blueprints on the current node, and repeat for all its children.
     * Any new [Node]s created in the process are treated as 'generated',
     * and are added to [newGenerated]. */
    private fun EditorAppImpl.runBlueprintsRecursive(root: SceneNode) {
        val prevChildSet = root.children.filterIsInstance<SceneNode>().map { it.node }.toSet()
        val loggers = mutableListOf<IDomListener>()
        if (state.settings.verboseDom) loggers.add(VerboseLogger(this))
        root.blueprints.forEach {
            it.execute(root.node, state.stylesheet, state.seed, 4,
                state.settings.cleanDummies, state.settings.hinting, loggers,
            )
        }
        // Create SceneNodes for the new nodes
        val newNodes = root.node.children.filter { it !in prevChildSet }
        root.children.filterIsInstance<SceneNode>().forEach { runBlueprintsRecursive(it) }
        newNodes.forEach { createSceneNodesRecursive(root, it) }
    }

    /** Create [SceneNode] for the given [newNode], and all its children,
     * and ste [parent] as its parent. */
    private fun EditorAppImpl.createSceneNodesRecursive(
        parent: SceneNode, newNode: Node
    ) {
        val sceneNode = state.registry.newNode(newNode, Colors.defaultGeneratedNodeBox, true)
        sceneNode.parent = parent
        newGenerated.add(sceneNode.detached())
        newNode.children.forEach { createSceneNodesRecursive(sceneNode, it) }
    }

    companion object {
        class VerboseLogger(val app: EditorApp) : IDomListener {
            override fun onBeginBuild(element: StyledElement<*>) {
                app.logInfo("Building DOM $element")
            }
            override fun onPrepareChildren(parent: StyledElement<*>, children: List<StyledElement<*>>) {}
            override fun onLayoutChildren(parent: StyledElement<*>, children: List<StyledElement<*>>) {}
            override fun onEndBuild(element: StyledElement<*>) {}
        }
    }
}