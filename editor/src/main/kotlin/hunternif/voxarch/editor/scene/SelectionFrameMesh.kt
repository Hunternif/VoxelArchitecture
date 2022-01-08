package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.render.BaseMesh
import hunternif.voxarch.editor.render.SelectionFrame
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil

class SelectionFrameMesh : BaseMesh() {
    private var selection: SelectionFrame? = null
    private var bufferSize = 0

    override fun init() {
        super.init()
        initVertexAttributes {
            vector3f(0) // position attribute
        }
    }

    fun setSelection(selection: SelectionFrame?) {
        this.selection = selection
        updateEdges()
    }

    fun updateEdges() {
        val edges = selection?.getEdges() ?: emptyList()
        bufferSize = edges.size * 2 * 3

        val vertexBuffer = MemoryUtil.memAllocFloat(bufferSize)

        // Store line positions in the vertex buffer
        selection.run {
            for (e in edges) {
                vertexBuffer
                    .put(e.start.x).put(e.start.y).put(e.start.z)
                    .put(e.end.x).put(e.end.y).put(e.end.z)
            }
        }
        vertexBuffer.flip() // rewind

        // Upload the vertex buffer
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
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