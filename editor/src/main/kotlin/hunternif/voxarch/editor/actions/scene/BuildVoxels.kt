package hunternif.voxarch.editor.actions.scene

import hunternif.voxarch.builder.IBuildListener
import hunternif.voxarch.builder.RootBuilder
import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.GenEvent
import hunternif.voxarch.editor.actions.SceneEvent
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.logInfo
import hunternif.voxarch.editor.actions.redrawVoxels
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.DetachedObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.scenegraph.detached
import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.BlockStorageDelegate
import hunternif.voxarch.storage.ChunkedStorage3D

class BuildVoxels : HistoryAction(
    "Build voxels",
    FontAwesomeIcons.Cubes
), GenEvent, SceneEvent {
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
                if (settings.verboseBuild) builder.addListener(VerboseLogger(app))
                builder.build(rootNode.node, world, buildContext)
                val builtVoxels = registry.newVoxelGroup(
                    "Built voxels", world, settings.renderMode, true)
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

    companion object {
        class VerboseLogger(val app: EditorApp) : IBuildListener {
            override fun onBeginBuild(node: Node) {
                app.logInfo("Building Node $node")
            }

            override fun onPrepareChildren(parent: Node, children: List<Node>) {}
        }
    }
}