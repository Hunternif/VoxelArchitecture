package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.DEBUG
import hunternif.voxarch.editor.render.BaseMesh
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.emptyArray3D
import hunternif.voxarch.util.forEachPos
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

class BoxMeshInstanced : BaseMesh() {
    /** Borrowed from LearnOpenGL.com */
    private val vertexArray = floatArrayOf(
        // coordinates        // normals
        // back face
        -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
        -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
         0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
         0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
         0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,

        // front face
        -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
         0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
         0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
         0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
        -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,

        // left face
        -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
        -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
        -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,

        // right face
         0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
         0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
         0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
         0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
         0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
         0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,

        // bottom face
        -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
         0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
         0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
         0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,

        // top face
        -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
        -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
         0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
         0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
         0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
        -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
    )
    private var voxels: IStorage3D<VoxColor?> = emptyArray3D()

    private var instanceVboID = 0

    override fun init() = MemoryStack.stackPush().use { stack ->
        super.init()

        // Create a float buffer of vertices
        val vertexBuffer = stack.mallocFloat(vertexArray.size)
        vertexBuffer.put(vertexArray).flip()

        // Upload the vertex buffer
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

        initVertexAttributes {
            vector3f(ATTR_POS) // position attribute
            vector3f(ATTR_NORMAL) // normal attribute
        }

        // Create VBO for the instances of this model
        instanceVboID = glGenBuffers()
        uploadInstanceData()
    }

    fun setVoxels(voxels: IStorage3D<VoxColor?>) {
        this.voxels = voxels
        uploadInstanceData()
    }

    private fun uploadInstanceData() {
        val instanceVertexBuffer = MemoryUtil.memAllocFloat(voxels.size * 6)
        instanceVertexBuffer.run {
            voxels.forEachPos { x, y, z, v ->
                if (v != null) {
                    put(x.toFloat())
                    put(y.toFloat())
                    put(z.toFloat())
                    val color = ColorRGBa.fromHex(v.color)
                    put(color.r)
                    put(color.g)
                    put(color.b)
                }
            }
            flip()
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glBufferData(GL_ARRAY_BUFFER, instanceVertexBuffer, GL_STATIC_DRAW)

        if (DEBUG) {
            val size = IntArray(1)
            glGetBufferParameteriv(GL_ARRAY_BUFFER, GL_BUFFER_SIZE, size)
            println("Vertex buffer size: ${size[0]}")
        }

        // The instanced attributes come from a separate vertex buffer
        initInstanceAttributes {
            vector3f(ATTR_OFFSET)
            vector3f(ATTR_COLOR)
        }

        // unbind
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    override fun render() {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)
        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, voxels.size)
    }

    companion object {
        private const val ATTR_POS = 0
        private const val ATTR_NORMAL = 1
        private const val ATTR_OFFSET = 2
        private const val ATTR_COLOR = 3
    }
}