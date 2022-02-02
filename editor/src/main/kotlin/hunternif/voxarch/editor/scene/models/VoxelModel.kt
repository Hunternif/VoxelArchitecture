package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.forEachPos
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*

/** For rendering final world voxels.
 * TODO: voxels shouldn't be treated as SceneObjects */
class VoxelModel : BoxInstancedModel<SceneObject>() {
    fun setVoxels(voxels: IStorage3D<VoxColor?>) {
        instances.clear()
        voxels.forEachPos { x, y, z, v ->
            if (v != null) {
                instances.add(
                    SceneObject(
                        Vector3f(-0.5f + x, -0.5f + y, -0.5f + z),
                        Vector3f(0.5f + x, 0.5f + y, 0.5f + z),
                        ColorRGBa.fromHex(v.color),
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