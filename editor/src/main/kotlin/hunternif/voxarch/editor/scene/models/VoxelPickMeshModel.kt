package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.VoxelPickShader
import hunternif.voxarch.editor.util.*
import org.joml.Matrix4f
import org.lwjgl.opengl.GL33.*

/**
 * Special model for picking voxel models.
 * Renders the silhouette using a solid color.
 */
class VoxelPickMeshModel(
    override val id: Int,
) : BaseModel(), WithID {
    private var vertBufferSize = 0

    private val vertexBuffer = FloatBufferWrapper()

    /** For quickly moving the whole model without changing its geometry. */
    private val modelMat = Matrix4f()
    var visible = true

    /** Used for hit testing on the selection texture. */
    val pickingColor = ColorRGBa.fromHex(0xffffff - id * 10)

    override val shader = VoxelPickShader(pickingColor)

    fun updateMatrix(modelMat: Matrix4f) {
        this.modelMat.set(modelMat)
        shader.use {
            uploadMat4f("uModel", modelMat)
        }
    }

    override fun init() {
        super.init()
        initVertexAttributes {
            vector3f(0) // position attribute
        }
    }

    fun uploadMesh(mesh: Mesh) {
        val vertices = mesh.iterateTriangleVertices().toList()
        // 3 = 3f pos
        vertBufferSize = vertices.size * 3
        vertexBuffer.prepare(vertBufferSize).run {
            for (v in vertices) {
                put(v.pos)
            }
            flip()
        }
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer.buffer, GL_STATIC_DRAW)
    }

    override fun render() {
        if (!visible) return
        shader.updateColor(pickingColor)
        shader.uploadMat4f("uModel", modelMat)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)
        glDrawArraysInstanced(GL_TRIANGLES, 0, vertBufferSize, 1)
    }
}