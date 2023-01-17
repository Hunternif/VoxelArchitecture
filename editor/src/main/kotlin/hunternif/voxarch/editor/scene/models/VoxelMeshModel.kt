package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.meshFromVoxels
import hunternif.voxarch.storage.IVoxel
import org.joml.Matrix4f

/** For rendering final world voxels, using a unified mesh. */
class VoxelMeshModel(
    private val voxels: SceneVoxelGroup,
    private val colorMap: (IVoxel) -> ColorRGBa,
) : MeshModel() {

    private val modelMat = Matrix4f()
    var visible = true

    fun updateVoxels() {
        mesh = meshFromVoxels(voxels.data, colorMap)
    }

    fun updatePosition() {
        modelMat.translation(voxels.origin)
        shader.use {
            uploadMat4f("uModel", modelMat)
        }
    }

    override fun render() {
        if (visible) super.render()
    }
}