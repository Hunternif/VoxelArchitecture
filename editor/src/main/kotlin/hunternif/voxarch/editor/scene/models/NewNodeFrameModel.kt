package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.NewNodeFrame
import hunternif.voxarch.editor.scene.shaders.SolidColorShader
import hunternif.voxarch.editor.util.FloatBufferWrapper
import hunternif.voxarch.editor.util.put
import org.lwjgl.opengl.GL33.*

class NewNodeFrameModel : BaseModel() {
    private var bufferSize = 0
    private val vertexBuffer = FloatBufferWrapper()

    override val shader = SolidColorShader(0xcccccc)

    override fun init() {
        super.init()
        initVertexAttributes {
            vector3f(0) // position attribute
        }
    }

    fun updateEdges(frame: NewNodeFrame) {
        val edges = frame.getEdges()
        bufferSize = edges.size * 2 * 3

        vertexBuffer.prepare(bufferSize).run {
            // Store line positions in the vertex buffer
            for (e in edges) {
                put(e.start).put(e.end)
            }
            flip() // rewind
        }

        // Upload the vertex buffer
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer.buffer, GL_STATIC_DRAW)
    }

    override fun render() {
        glDisable(GL_DEPTH_TEST)
        glEnable(GL_LINE_STIPPLE)

        glLineWidth(3f)
        glLineStipple(1, 0x00ff.toShort())
        glDrawArrays(GL_LINES, 0, bufferSize)

        glEnable(GL_DEPTH_TEST)
        glDisable(GL_LINE_STIPPLE)
    }
}