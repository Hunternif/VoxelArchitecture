package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.scene.models.BoxInstancedModel.InstanceData
import hunternif.voxarch.editor.util.ColorRGBa
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*

/** For rendering technical elements that look like voxels, e.g. origin point. */
class VoxelTechnicalModel : BoxInstancedModel<InstanceData>() {
    fun addVoxel(
        pos: Vector3f,
        color: ColorRGBa,
        /** Half-width of the cube.
         * At 0.0 it's a dot, at 0.5 it fills the whole cube. */
        w: Float = 0.5f
    ) {
        instances.add(
            InstanceData(
                Vector3f(pos).add(-w, -w, -w),
                Vector3f(pos).add(w, w, w),
                color,
            )
        )
    }

    fun update() {
        uploadInstanceData()
    }

    fun clear() {
        instances.clear()
    }

    override fun render() {
        glDisable(GL_DEPTH_TEST)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glDisable(GL_CULL_FACE)

        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, instances.size)
    }
}