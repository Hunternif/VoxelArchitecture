package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.IModel
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.storage.IVoxel
import org.joml.Matrix4f

/** Contains all models for individual [SceneVoxelGroup] instances. */
class VoxelGroupsModel(
    private val colorMap: (IVoxel) -> ColorRGBa,
) : IModel {
    private val models = LinkedHashMap<SceneVoxelGroup, VoxelMeshModel>()

    /** Creates model and voxel mesh. */
    private fun getModel(group: SceneVoxelGroup) = models.getOrPut(group) {
        VoxelMeshModel(group, colorMap).apply {
            init()
            updateVoxels()
        }
    }

    /** Updates high-level properties like position, size, visibility.
     * Doesn't update the voxel meshes. */
    fun updateVisible(groups: Collection<SceneVoxelGroup>) {
        groups.forEach { group ->
            getModel(group).apply {
                if (!visible) visible = true
                updatePosition()
            }
        }
        val visibleSet = groups.toSet()
        for ((group, model) in models) {
            if (group !in visibleSet) model.visible = false
        }
    }

    /** Re-creates voxel mesh. */
    fun updateVoxels(groups: Collection<SceneVoxelGroup>) {
        groups.forEach {
            getModel(it).updateVoxels()
        }
    }

    override fun runFrame(viewProj: Matrix4f) {
        for ((_, model) in models) {
            model.runFrame(viewProj)
        }
    }

}