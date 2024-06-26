package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.scene.models.box.AABBoxInstancedModel
import hunternif.voxarch.editor.scene.models.box.AABBoxMesh
import hunternif.voxarch.editor.util.ColorRGBa
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*

/** For rendering technical elements that look like voxels, e.g. origin point. */
class VoxelTechnicalModel : AABBoxInstancedModel<AABBoxMesh>() {
    fun addVoxel(
        pos: Vector3f,
        color: ColorRGBa,
        /** Half-width of the cube.
         * At 0.0 it's a dot, at 0.5 it fills the whole cube. */
        w: Float = 0.5f
    ) {
        val start = Vector3f(pos).add(-w, -w, -w)
        val end = Vector3f(pos).add(w, w, w)
        instances.add(AABBoxMesh(start, end.sub(start), color))
    }

    override fun render() {
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glDisable(GL_CULL_FACE)

        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, instances.size)
    }
}