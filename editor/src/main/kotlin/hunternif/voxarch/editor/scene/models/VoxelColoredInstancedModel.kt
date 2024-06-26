package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.scene.models.VoxelColoredInstancedModel.VoxelInstance
import hunternif.voxarch.editor.scene.models.box.AABBoxInstancedModel
import hunternif.voxarch.editor.scene.models.box.AABBoxMesh
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.toVector3f
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.util.forEachPos
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*

/** For rendering final world voxels. */
class VoxelColoredInstancedModel(
    private val voxels: SceneVoxelGroup,
    private val colorMap: (IVoxel) -> ColorRGBa,
) : AABBoxInstancedModel<VoxelInstance>() {
    class VoxelInstance(x: Float, y: Float, z: Float, color: ColorRGBa) :
        AABBoxMesh(
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
        modelMat.translation(voxels.findGlobalPosition().toVector3f())
        shader.use {
            uploadMat4f("uModel", modelMat)
        }
    }

    override fun render() {
        if (!visible) return
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)
        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, instances.size)
    }
}