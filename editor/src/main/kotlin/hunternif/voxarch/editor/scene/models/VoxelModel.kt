package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.forEachPos
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*

class VoxelModel : BoxInstancedModel<Any?>() {
    fun setVoxels(voxels: IStorage3D<VoxColor?>) {
        instances.clear()
        voxels.forEachPos { x, y, z, v ->
            if (v != null) {
                instances.add(
                    InstanceData(
                        Vector3f(-0.5f + x, -0.5f + y, -0.5f + z),
                        Vector3f(1f, 1f, 1f),
                        ColorRGBa.fromHex(v.color),
                        null,
                    )
                )
            }
        }
        uploadInstanceData()
    }

    override fun render() {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)
        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, instances.size)
    }
}