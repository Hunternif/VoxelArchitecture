package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
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
) {
    private lateinit var oldGenerated: List<DetachedObject>
    private lateinit var newGenerated: List<DetachedObject>

    override fun invoke(app: EditorAppImpl) {
        if (!::oldGenerated.isInitialized) {
            oldGenerated = app.state.generatedVoxels.map { it.detached() }
        }
        app.clearGeneratedVoxels()
        if (!::newGenerated.isInitialized) {
            newGenerated = mutableListOf()
            val world = BlockStorageDelegate(ChunkedStorage3D())
            app.state.run {
                rootNode.node.setNotBuiltRecursive()
                builder.build(rootNode.node, world, buildContext)
                val builtVoxels = registry.newVoxelGroup(
                    "Built voxels", world, renderMode, true)
                app.state.voxelRoot.addChild(builtVoxels)
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

    /** Apply `isBuilt = false` to all nodes in the scene. */
    //TODO: get rid of isBuilt, it's almost useless
    private fun Node.setNotBuiltRecursive() {
        isBuilt = false
        children.forEach { it.setNotBuiltRecursive() }
    }
}