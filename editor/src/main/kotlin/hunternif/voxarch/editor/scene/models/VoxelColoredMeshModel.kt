package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.scene.shaders.MagicaVoxelShader
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.*
import hunternif.voxarch.storage.IVoxel
import org.joml.Matrix4f
import org.lwjgl.opengl.GL33.*

/**
 * Renders colored voxels (like in MagicaVoxel),
 * using a unified triangle mesh.
 */
class VoxelColoredMeshModel(
    private val voxels: SceneVoxelGroup,
    private val colorMap: (IVoxel) -> ColorRGBa,
) : BaseModel() {
    private var instanceVboID = 0
    private var vertBufferSize = 0

    private val vertexBuffer = FloatBufferWrapper()
    private val instanceVertexBuffer = FloatBufferWrapper()

    /** For quickly moving the whole model without changing its geometry. */
    private val modelMat = Matrix4f()
    var visible = true

    override val shader: Shader = MagicaVoxelShader()

    fun updateVoxels() {
        val mesh = meshFromVoxels(voxels.data, colorMap)
        uploadMesh(mesh)
    }

    fun updatePosition() {
        modelMat.translation(voxels.origin)
        shader.use {
            uploadMat4f("uModel", modelMat)
        }
    }

    override fun init() {
        super.init()
        initVertexAttributes {
            vector3f(0) // position attribute
            vector3f(1) // normal attribute
            vector4f(2) // color instance attribute
        }

        // The shader is instanced, but it will only render 1 instance:
        instanceVboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        initInstanceAttributes {
            mat4f(3) // model matrix instance attribute, uses ids 3-6
        }
        uploadInstanceData()
    }

    // A left-over from the instanced shader, uploads the Model matrix
    private fun uploadInstanceData() {
        instanceVertexBuffer.prepare(16).run {
            put(Matrix4f())
            flip()
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glBufferData(GL_ARRAY_BUFFER, instanceVertexBuffer.buffer, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    private fun uploadMesh(mesh: Mesh) {
        val vertices = mesh.vertices.toList()
        // 10 = 3f pos + 3f normal + 4f color
        vertBufferSize = vertices.size * 10
        vertexBuffer.prepare(vertBufferSize).run {
            for (v in vertices) {
                put(v.pos)
                put(v.normal)
                put(v.color.toVector4f())
            }
            flip()
        }
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer.buffer, GL_STATIC_DRAW)
    }

    override fun render() {
        if (!visible) return
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)
        glDrawArraysInstanced(GL_TRIANGLES, 0, vertBufferSize, 1)
    }
}