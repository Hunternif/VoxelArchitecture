package hunternif.voxarch.editor.actions.scene

import hunternif.voxarch.dom.builder.IDomListener
import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.logInfo
import hunternif.voxarch.editor.actions.redrawNodes
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.PropBlueprint
import hunternif.voxarch.editor.builder.PropEditorBuilder
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.DetachedObject
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.detached
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.plan.Node

/** Creates new nodes from all blueprints attached to nodes in the scene. */
class GenerateNodes : HistoryAction(
    "Generate nodes",
    FontAwesomeIcons.Archway
) {
    private lateinit var oldGenerated: List<DetachedObject>
    private lateinit var newGenerated: MutableList<DetachedObject>

    /** Maps from a node to the BP node that generated it. */
    private val nodeToBpMap = mutableMapOf<Node, BlueprintNode>()

    /** Maps from a node to the color of the BP branch
     * (from the lowest ancestor with a custom color). */
    private val nodeColorMap = mutableMapOf<Node, ColorRGBa>()

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::oldGenerated.isInitialized) {
            oldGenerated = app.state.generatedNodes.map { it.detached() }
        }
        app.clearGeneratedNodes()
        if (!::newGenerated.isInitialized) {
            newGenerated = mutableListOf()
            PropBlueprint.updateBlueprints(app.state.blueprints)
            PropEditorBuilder.updateBuilders(app.state.builderLibrary)
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
        blueprintRegistry.refreshUsagesInNodes(state)
    }

    /** Run all blueprints on the current node, and repeat for all its children.
     * Any new [Node]s created in the process are treated as 'generated',
     * and are added to [newGenerated]. */
    private fun EditorAppImpl.runBlueprintsRecursive(root: SceneNode) {
        val prevChildSet = root.children.filterIsInstance<SceneNode>().map { it.node }.toSet()
        val listeners = mutableListOf<IDomListener>()
        if (state.verboseDom) listeners.add(VerboseLogger(this))
        root.blueprints.forEach {
            it.execute(root.node, state.stylesheet, state.seed, state.maxRecursions,
                state.cleanDummies, state.hinting,
                listeners + BPTrackingListener(it, nodeToBpMap, nodeColorMap),
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
        val color = nodeColorMap[newNode] ?: BlueprintNode.defaultColor
        val sceneNode = state.registry.newNode(newNode, color, true)
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

        /**
         * This listener populates the map of Node to BlueprintNode which created it.
         * Also, colors.
         */
        class BPTrackingListener(
            bp: Blueprint,
            private val nodeToBpMap: MutableMap<Node, BlueprintNode>,
            private val nodeColorMap: MutableMap<Node, ColorRGBa>,
        ) : IDomListener {
            /** Maps domBuilder to its parent BP node */
            private val domBuilderMap = bp.mapDomBuildersToNodes()
            override fun onBeginBuild(element: StyledElement<*>) {}
            override fun onPrepareChildren(parent: StyledElement<*>, children: List<StyledElement<*>>) {}
            override fun onLayoutChildren(parent: StyledElement<*>, children: List<StyledElement<*>>) {}
            override fun onEndBuild(element: StyledElement<*>) {
                if (element !is StyledNode<*>) return
                var bpNode = domBuilderMap[element.domBuilder]
                if (bpNode == null) {
                    // Find the lowest ancestor Blueprint node
                    val ancestor = element.ctx.lineage.lastOrNull {
                        domBuilderMap[it.domBuilder] != null
                    } ?: return
                    bpNode = domBuilderMap[ancestor.domBuilder] ?: return
                }
                var color = bpNode.color
                if (!bpNode.isCustomColor) {
                    // Find the lowest ancestor with a custom color
                    element.ctx.lineage.lastOrNull {
                        domBuilderMap[it.domBuilder]?.isCustomColor == true
                    }?.let {
                        color = domBuilderMap[it.domBuilder]!!.color
                    }
                }
                nodeToBpMap[element.node] = bpNode
                nodeColorMap[element.node] = color
            }
        }
    }
}