package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.forEachPos
import hunternif.voxarch.vector.Array3D
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

class BoxMeshInstanced {
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
    private var voxels: IStorage3D<VoxColor?> = Array3D(0, 0, 0, null)

    private var vaoID = 0
    private var vboID = 0
    private var instanceVboID = 0

    var debug = true

    fun init() = MemoryStack.stackPush().use { stack ->
        vaoID = glGenVertexArrays()
        glBindVertexArray(vaoID)


        // Create a float buffer of vertices
        val vertexBuffer = stack.mallocFloat(vertexArray.size)
        vertexBuffer.put(vertexArray).flip()

        // Create VBO & upload the vertex buffer
        vboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

        val stride = 6 * Float.SIZE_BYTES // vec3 pos, vec3 normal
        // position attribute
        glEnableVertexAttribArray(ATTR_POS)
        glVertexAttribPointer(ATTR_POS, 3, GL_FLOAT, false, stride, 0)
        // normal attribute
        glEnableVertexAttribArray(ATTR_NORMAL)
        glVertexAttribPointer(ATTR_NORMAL, 3, GL_FLOAT, false, stride, 3L * Float.SIZE_BYTES)

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
        if (debug) {
            val size = IntArray(1)
            glGetBufferParameteriv(GL_ARRAY_BUFFER, GL_BUFFER_SIZE, size)
            println("Vertex buffer size: ${size[0]}")
        }

        // The instanced attributes come from a separate vertex buffer
        val stride = 6 * Float.SIZE_BYTES // vec3 offset, vec3 color
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)

        // offsets
        glEnableVertexAttribArray(ATTR_OFFSET)
        glVertexAttribPointer(ATTR_OFFSET, 3, GL_FLOAT, false, stride, 0)
        glVertexAttribDivisor(ATTR_OFFSET, 1)

        // color
        glEnableVertexAttribArray(ATTR_COLOR)
        glVertexAttribPointer(ATTR_COLOR, 3, GL_FLOAT, false, stride, 3L * Float.SIZE_BYTES)
        glVertexAttribDivisor(ATTR_COLOR, 1)

        // unbind
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    fun render() {
        // Bind vertex array and attributes
        glBindVertexArray(vaoID)
        glEnableVertexAttribArray(ATTR_POS)
        glEnableVertexAttribArray(ATTR_NORMAL)
        glEnableVertexAttribArray(ATTR_OFFSET)
        glEnableVertexAttribArray(ATTR_COLOR)

        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)
        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, voxels.size)

        // Unbind everything
        glBindVertexArray(0)
        glDisableVertexAttribArray(ATTR_POS)
        glDisableVertexAttribArray(ATTR_NORMAL)
        glDisableVertexAttribArray(ATTR_OFFSET)
        glDisableVertexAttribArray(ATTR_COLOR)
    }

    companion object {
        private const val ATTR_POS = 0
        private const val ATTR_NORMAL = 1
        private const val ATTR_OFFSET = 2
        private const val ATTR_COLOR = 3
    }
}