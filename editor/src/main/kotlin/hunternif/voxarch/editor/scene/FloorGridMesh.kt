package hunternif.voxarch.editor.scene

import org.joml.Vector2i
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

class FloorGridMesh {
    private val vertexArray = floatArrayOf(
        -0.5f, -0.5f, -0.5f,
         0.5f, -0.5f, -0.5f,
         0.5f, -0.5f,  0.5f,
        -0.5f, -0.5f,  0.5f,
        -0.5f, -0.5f, -0.5f,
    )

    private var offsets: List<Vector2i> = listOf(Vector2i(0, 0))

    private var vaoID = 0
    private var vboID = 0
    private var instanceVboID = 0

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

        // position attribute
        val stride = 3 * Float.SIZE_BYTES // vec3 pos
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0)

        // Create VBO for the instances of this model
        instanceVboID = glGenBuffers()
        uploadInstanceData()
    }

    fun setSize(fromX: Int, fromY: Int, toX: Int, toY: Int) {
        val offsets = mutableListOf<Vector2i>()
        for (x in fromX .. toX) {
            for (y in fromY .. toY) {
                offsets.add(Vector2i(x, y))
            }
        }
        this.offsets = offsets
        uploadInstanceData()
    }

    private fun uploadInstanceData() {
        val instanceVertexBuffer = MemoryUtil.memAllocInt(offsets.size * 2)
        instanceVertexBuffer.run {
            offsets.forEach {
                put(it.x)
                put(it.y)
            }
            flip()
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glBufferData(GL_ARRAY_BUFFER, instanceVertexBuffer, GL_STATIC_DRAW)

        // The instanced offset attribute comes from a separate vertex buffer
        val instanceStride = 2 * Int.SIZE_BYTES // vec2 offset
        glEnableVertexAttribArray(1)
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glVertexAttribPointer(1, 2, GL_INT, false, instanceStride, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glVertexAttribDivisor(1, 1)
    }

    fun render() {
        // Bind vertex array and attributes
        glBindVertexArray(vaoID)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)

        glDrawArraysInstanced(GL_LINE_STRIP, 0, 5, offsets.size)

        // Unbind everything
        glBindVertexArray(0)
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
    }
}