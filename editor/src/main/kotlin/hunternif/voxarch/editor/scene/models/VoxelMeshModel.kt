package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.VoxelRenderMode
import hunternif.voxarch.editor.scene.shaders.VoxelShader
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.*
import hunternif.voxarch.storage.IVoxel
import org.joml.Matrix4f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

/**
 * Renders colored or textured voxels,
 * using a unified triangle mesh.
 */
class VoxelMeshModel(
    private val voxels: SceneVoxelGroup,
    private val colorMap: (IVoxel) -> ColorRGBa,
    override var shader: VoxelShader
) : BaseModel() {
    private var instanceVboID = 0
    private var vertBufferSize = 0

    /** For quickly moving the whole model without changing its geometry. */
    private val modelMat = Matrix4f()
    var visible = true

    var mesh: Mesh = Mesh()

    val pickModel = VoxelPickMeshModel(voxels.id)

    fun updateVoxels() {
        mesh = when (voxels.renderMode) {
            VoxelRenderMode.COLORED -> coloredMeshFromVoxels(voxels.data, colorMap)
            VoxelRenderMode.TEXTURED -> texturedMeshFromVoxels(voxels.data)
        }
        uploadMesh(mesh)
        pickModel.uploadMesh(mesh)
    }

    fun updatePosition() {
        modelMat.translation(voxels.findGlobalPosition().toVector3f())
        shader.use {
            uploadMat4f("uModel", modelMat)
        }
        pickModel.updateMatrix(modelMat)
    }

    override fun init() {
        super.init()
        initVertexAttributes {
            vector3f(0) // position attribute
            vector3f(1) // normal attribute
            vector4f(2) // color or UV attribute
        }

        // The shader is instanced, but it will only render 1 instance:
        instanceVboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        initInstanceAttributes {
            mat4f(3) // model matrix instance attribute, uses ids 3-6
        }
        uploadInstanceData()
        pickModel.init()
    }

    // A left-over from the instanced shader, uploads the Model matrix
    private fun uploadInstanceData() = MemoryStack.stackPush().use { stack ->
        val instanceVertexBuffer = stack.mallocFloat(16)
        instanceVertexBuffer.run {
            put(Matrix4f())
            flip()
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glBufferData(GL_ARRAY_BUFFER, instanceVertexBuffer, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    private fun uploadMesh(mesh: Mesh) {
        val vertices = mesh.iterateTriangleVertices().toList()
        // 10 = 3f pos + 3f normal + 4f color or UV
        vertBufferSize = vertices.size * 10
        val vertexBuffer = MemoryUtil.memAllocFloat(vertBufferSize)
        vertexBuffer.run {
            for (v in vertices) {
                put(v.pos)
                put(v.normal)
                when (voxels.renderMode) {
                    VoxelRenderMode.COLORED -> put(v.color.toVector4f())
                    VoxelRenderMode.TEXTURED -> put(v.uv).put(0f).put(0f)
                }
            }
            flip()
        }
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
        MemoryUtil.memFree(vertexBuffer)
    }

    override fun render() {
        if (!visible) return
        // Other voxel models could be reusing this shader and change render mode:
        shader.renderMode = voxels.renderMode
        shader.uploadMat4f("uModel", modelMat)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)
        glDrawArraysInstanced(GL_TRIANGLES, 0, vertBufferSize, 1)
    }
}