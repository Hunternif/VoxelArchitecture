package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.SolidColorShader
import hunternif.voxarch.editor.util.toVector3f
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.findGlobalPosition
import hunternif.voxarch.vector.Vec3
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil

/**
 * Renders the frame outline of a node that's currently selected
 */
class SelectedNodeFrameModel : BaseModel() {
    private var bufferSize = 0

    override val shader = SolidColorShader(0xffffff)

    private val nodes = mutableListOf<Node>()

    fun addNode(node: Node) {
        nodes.add(node)
        updateEdges()
    }

    fun clear() {
        nodes.clear()
        updateEdges()
    }

    override fun init() {
        super.init()
        initVertexAttributes {
            vector3f(0) // position attribute
        }
    }

    private fun updateEdges() {
        // 12 edges per node, 2 vertices per edge, 3 floats per vertex
        bufferSize = nodes.size * 12 * 2 * 3

        val vertexBuffer = MemoryUtil.memAllocFloat(bufferSize)

        // Store line positions in the vertex buffer
        for (node in nodes) {
            val start = node.findGlobalPosition() - Vec3(0.5, 0.5, 0.5)
            val end = start + Vec3(1, 1, 1)
            if (node is Room) {
                start.addLocal(node.start)
                end.addLocal(node.start).addLocal(node.size)
            }
            val edges = boxEdges(start.toVector3f(), end.toVector3f())
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

        glLineWidth(1f)
        glDrawArrays(GL_LINES, 0, bufferSize)

        glEnable(GL_DEPTH_TEST)
    }
}