package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.NewNodeFrame
import hunternif.voxarch.editor.scene.shaders.SolidColorShader
import hunternif.voxarch.editor.util.put
import hunternif.voxarch.editor.util.safeFlip
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack

class NewNodeFrameModel : BaseModel() {
    private var bufferSize = 0

    override val shader = SolidColorShader(0xcccccc)

    override fun init() {
        super.init()
        readDepth = false
        initVertexAttributes {
            vector3f(0) // position attribute
        }
    }

    fun updateEdges(frame: NewNodeFrame) = MemoryStack.stackPush().use { stack ->
        val edges = frame.getEdges()
        bufferSize = edges.size * 2 * 3

        val vertexBuffer = stack.mallocFloat(bufferSize)
        vertexBuffer.run {
            // Store line positions in the vertex buffer
            for (e in edges) {
                put(e.start).put(e.end)
            }
            safeFlip() // rewind
        }

        // Upload the vertex buffer
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
    }

    override fun render() {
        glEnable(GL_LINE_STIPPLE)

        glLineWidth(3f)
        glLineStipple(1, 0x00ff.toShort())
        glDrawArrays(GL_LINES, 0, bufferSize)

        glDisable(GL_LINE_STIPPLE)
    }
}