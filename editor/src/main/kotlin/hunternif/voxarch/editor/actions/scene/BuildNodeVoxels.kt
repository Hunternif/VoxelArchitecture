package hunternif.voxarch.editor.actions.scene

import hunternif.voxarch.builder.RootBuilder
import hunternif.voxarch.builder.toLocal
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.redrawVoxels
import hunternif.voxarch.editor.actions.scene.BuildVoxels.Companion.BUILT_VOXELS_GROUP_NAME
import hunternif.voxarch.editor.actions.scene.BuildVoxels.Companion.VerboseLogger
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.DetachedObject
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.scenegraph.detached
import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.storage.BlockStorageDelegate
import hunternif.voxarch.storage.ChunkedStorage3D
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.copyTo
import hunternif.voxarch.util.round
import hunternif.voxarch.vector.LinearTransformation

/**
 * Builds voxels for an individual node into the main "built voxels" node.
 */
class BuildNodeVoxels(
    val node: SceneNode,
) : HistoryAction(
    "Build voxels for node",
    FontAwesomeIcons.Cube
) {
    private var oldVoxels: DetachedObject? = null
    private lateinit var newVoxels: DetachedObject

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) = app.state.run {
        if (firstTime) {
            val oldBuiltVoxels = generatedVoxels.firstOrNull {
                it.label == BUILT_VOXELS_GROUP_NAME
            }
            oldVoxels = oldBuiltVoxels?.detached()

            // Create new "Built voxels" by copying the old group:
            val world = BlockStorageDelegate(ChunkedStorage3D())
            val newBuiltVoxels = registry.newVoxelGroup(
                BUILT_VOXELS_GROUP_NAME, world, renderMode, true)
            if (oldBuiltVoxels != null) {
                @Suppress("UNCHECKED_CAST")
                (oldBuiltVoxels.data as? IStorage3D<BlockData?>)?.copyTo(world)
                newBuiltVoxels.origin.set(oldBuiltVoxels.origin)
            } else {
                // move the future voxel group's origin:
                newBuiltVoxels.origin.set(node.node.origin.round())
            }

            val transform = LinearTransformation().translate(-newBuiltVoxels.origin)
            val offsetWorld = world.toLocal(transform)

            val builder = RootBuilder()
            if (verboseBuild) builder.addListener(VerboseLogger(app))
            builder.build(node.node, offsetWorld, buildContext)

            voxelRoot.addChild(newBuiltVoxels)
            newVoxels = newBuiltVoxels.detached()
        }
        oldVoxels?.detach()
        newVoxels.let {
            it.reattach()
            app.state.generatedVoxels.add(it.obj as SceneVoxelGroup)
        }
        app.redrawVoxels()
    }

    override fun revert(app: EditorAppImpl) {
        newVoxels.detach()
        oldVoxels?.let {
            it.reattach()
            app.state.generatedVoxels.add(it.obj as SceneVoxelGroup)
        }
        app.redrawVoxels()
    }
}