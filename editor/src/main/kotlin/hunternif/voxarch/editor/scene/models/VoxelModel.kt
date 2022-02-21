package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.editor.scene.SceneVoxelGroup
import hunternif.voxarch.editor.scene.models.VoxelModel.VoxelInstance
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.util.forEachPos
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*

/** For rendering final world voxels.
 * TODO: voxels shouldn't be treated as SceneObjects */
class VoxelModel(
    private val voxels: SceneVoxelGroup,
    private val colorMap: (IVoxel) -> ColorRGBa,
) : BoxInstancedModel<VoxelInstance>() {
    class VoxelInstance(x: Float, y: Float, z: Float, color: ColorRGBa) :
        SceneObject(
            Vector3f(-0.5f + x, -0.5f + y, -0.5f + z),
            Vector3f(1f, 1f, 1f),
            color
        )

    private val modelMat = Matrix4f()
    var visible = true

    fun updateVoxels() {
        clear()
        voxels.data.forEachPos { x, y, z, v ->
            if (v != null) {
                instances.add(
                    VoxelInstance(
                        x.toFloat(),
                        y.toFloat(),
                        z.toFloat(),
                        colorMap(v)
                    )
                )
            }
        }
        uploadInstanceData()
    }

    fun updatePosition() {
        modelMat.translation(voxels.origin)
        shader.use {
            uploadMat4f("uModel", modelMat)
        }
    }

    override fun render() {
        if (!visible) return
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)
        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, instances.size)
    }
}