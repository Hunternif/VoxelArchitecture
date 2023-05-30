package hunternif.voxarch.editor.actions.scene

import hunternif.voxarch.builder.IBuildListener
import hunternif.voxarch.builder.RootBuilder
import hunternif.voxarch.builder.toLocal
import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.logInfo
import hunternif.voxarch.editor.actions.redrawVoxels
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.BlockStorageDelegate
import hunternif.voxarch.storage.ChunkedStorage3D
import hunternif.voxarch.util.round
import hunternif.voxarch.vector.LinearTransformation
import hunternif.voxarch.vector.Vec3

class BuildVoxels : HistoryAction(
    "Build voxels",
    FontAwesomeIcons.Cubes
) {
    private lateinit var oldGenerated: List<DetachedObject>
    private lateinit var newGenerated: List<DetachedObject>

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::oldGenerated.isInitialized) {
            oldGenerated = app.state.generatedVoxels.map { it.detached() }
        }
        app.clearGeneratedVoxels()
        if (!::newGenerated.isInitialized) {
            newGenerated = mutableListOf()
            val world = BlockStorageDelegate(ChunkedStorage3D())
            val builder = RootBuilder()
            app.state.run {
                if (verboseBuild) builder.addListener(VerboseLogger(app))

                // move the future voxel group's origin:
                val newOrigin = findAvgOrigin(rootNode).round()
                val transform = LinearTransformation().translate(-newOrigin)
                val offsetWorld = world.toLocal(transform)

                builder.build(rootNode.node, offsetWorld, buildContext)
                val builtVoxels = registry.newVoxelGroup(
                    "Built voxels", world, renderMode, true)
                builtVoxels.origin.set(newOrigin)

                voxelRoot.addChild(builtVoxels)
                newGenerated = listOf(builtVoxels.detached())
            }
        }
        newGenerated.forEach {
            it.reattach()
            app.state.generatedVoxels.add(it.obj as SceneVoxelGroup)
        }
        app.redrawVoxels()
    }

    override fun revert(app: EditorAppImpl) {
        app.clearGeneratedVoxels()
        oldGenerated.forEach {
            it.reattach()
            app.state.generatedVoxels.add(it.obj as SceneVoxelGroup)
        }
        app.redrawVoxels()
    }

    /** Remove all generated voxel groups from the scene. */
    private fun EditorAppImpl.clearGeneratedVoxels() = state.run {
        generatedVoxels.toList().forEach { it.remove() }
        generatedVoxels.clear()
    }

    /**
     * Determines where to place the new "built voxels" group, so that
     * its origin is roughly in the middle of all nodes.
     */
    private fun findAvgOrigin(root: SceneNode): Vec3 {
        // Only count nodes that will produce voxels:
        val nodesWithBlueprints = root.children.filterIsInstance<SceneNode>()
            .filter { it.blueprints.isNotEmpty() }
        if (nodesWithBlueprints.isEmpty()) return root.node.origin

        // root node is usually at (0, 0, 0), but its children are offset
        val avgChildPos = nodesWithBlueprints.fold(Vec3(0, 0, 0)) { vec, child ->
            vec + child.node.origin
        } / nodesWithBlueprints.size

        return avgChildPos + root.node.origin
    }

    companion object {
        class VerboseLogger(val app: EditorApp) : IBuildListener {
            override fun onBeginBuild(node: Node) {
                app.logInfo("Building Node $node")
            }

            override fun onPrepareChildren(parent: Node, children: Collection<Node>) {}
        }
    }
}