package hunternif.voxarch.editor.scene

import org.joml.Vector3f
import org.joml.Vector3i
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
    private var offsets: List<Vector3i> = listOf(Vector3i(0, 0, 0))

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

    fun setInstances(offsets: List<Vector3i>) {
        this.offsets = offsets
        uploadInstanceData()
    }

    private fun uploadInstanceData() {
        val instanceVertexBuffer = MemoryUtil.memAllocInt(offsets.size * 3)
        instanceVertexBuffer.run {
            offsets.forEach {
                put(it.x)
                put(it.y)
                put(it.z)
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

        // The instanced offset attribute comes from a separate vertex buffer
        val instanceStride = 3 * Int.SIZE_BYTES // vec3 offset
        glEnableVertexAttribArray(ATTR_OFFSET)
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glVertexAttribPointer(ATTR_OFFSET, 3, GL_INT, false, instanceStride, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glVertexAttribDivisor(ATTR_OFFSET, 1)
    }

    fun render() {
        // Bind vertex array and attributes
        glBindVertexArray(vaoID)
        glEnableVertexAttribArray(ATTR_POS)
        glEnableVertexAttribArray(ATTR_NORMAL)
        glEnableVertexAttribArray(ATTR_OFFSET)

        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)
        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, offsets.size)

        // Unbind everything
        glBindVertexArray(0)
        glDisableVertexAttribArray(ATTR_POS)
        glDisableVertexAttribArray(ATTR_NORMAL)
        glDisableVertexAttribArray(ATTR_OFFSET)
    }

    companion object {
        private const val ATTR_POS = 0
        private const val ATTR_NORMAL = 1
        private const val ATTR_OFFSET = 2
    }
}