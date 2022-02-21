package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scene.SceneVoxelGroup
import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.BlockStorageDelegate
import hunternif.voxarch.storage.ChunkedStorage3D

class BuildVoxels : HistoryAction(
    "Build voxels",
    FontAwesomeIcons.Cubes
) {
    private lateinit var oldGenerated: List<SceneVoxelGroup>
    private lateinit var newGenerated: List<SceneVoxelGroup>

    override fun invoke(app: EditorAppImpl) {
        if (!::oldGenerated.isInitialized) {
            oldGenerated = app.state.generatedVoxels.toList()
        }
        app.clearGeneratedVoxels()
        if (!::newGenerated.isInitialized) {
            newGenerated = mutableListOf()
            val world = BlockStorageDelegate(ChunkedStorage3D())
            app.state.run {
                rootNode.node.setNotBuiltRecursive()
                builder.build(rootNode.node, world, buildContext)
            }
            newGenerated = listOf(
                SceneVoxelGroup("Built voxels", world, true).apply {
                    parent = app.state.voxelRoot
                }
            )
        }
        app.state.sceneObjects.addAll(newGenerated)
        app.state.generatedVoxels.addAll(newGenerated)
        newGenerated.forEach { it.parent?.addChild(it) }
        app.redrawVoxels()
    }

    override fun revert(app: EditorAppImpl) {
        app.clearGeneratedVoxels()
        app.state.sceneObjects.addAll(oldGenerated)
        app.state.generatedVoxels.addAll(oldGenerated)
        oldGenerated.forEach { it.parent?.addChild(it) }
        app.redrawVoxels()
    }

    /** Remove all generated voxel groups from the scene. */
    private fun EditorAppImpl.clearGeneratedVoxels() = state.run {
        sceneObjects.removeAll(generatedVoxels)
        hiddenObjects.removeAll(generatedVoxels)
        selectedObjects.removeAll(generatedVoxels)
        generatedVoxels.forEach { it.parent?.removeChild(it) }
        generatedVoxels.clear()
    }

    /** Apply `isBuilt = false` to all nodes in the scene. */
    //TODO: get rid of isBuilt, it's almost useless
    private fun Node.setNotBuiltRecursive() {
        isBuilt = false
        children.forEach { it.setNotBuiltRecursive() }
    }
}