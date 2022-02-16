package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.IModel
import hunternif.voxarch.editor.scene.SceneVoxelGroup
import org.joml.Matrix4f

/** Contains all models for individual [SceneVoxelGroup] instances. */
class VoxelGroupsModel : IModel {
    private val models = LinkedHashMap<SceneVoxelGroup, VoxelModel>()

    private fun getModel(group: SceneVoxelGroup) = models.getOrPut(group) {
        VoxelModel(group).apply {
            init()
            updateVoxels()
        }
    }

    fun updateModel(group: SceneVoxelGroup) {
        getModel(group).apply {
            if (!visible) visible = true
            updatePosition()
        }
    }

    fun updateVisible(groups: Collection<SceneVoxelGroup>) {
        groups.forEach { updateModel(it) }
        val visibleSet = groups.toSet()
        for ((group, model) in models) {
            if (group !in visibleSet) model.visible = false
        }
    }

    override fun runFrame(viewProj: Matrix4f) {
        for ((_, model) in models) {
            model.runFrame(viewProj)
        }
    }

}