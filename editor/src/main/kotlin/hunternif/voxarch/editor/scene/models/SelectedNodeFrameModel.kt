package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.editor.scene.shaders.SolidColorShader
import hunternif.voxarch.editor.util.put
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil

/**
 * Renders the frame outline of a node that's currently selected
 */
class SelectedNodeFrameModel : BaseModel() {
    private var bufferSize = 0

    override val shader = SolidColorShader(0xffffff)

    private val sceneObjects = mutableListOf<SceneObject>()

    fun addNode(obj: SceneObject) {
        sceneObjects.add(obj)
        updateEdges()
    }

    fun clear() {
        sceneObjects.clear()
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
        bufferSize = sceneObjects.size * 12 * 2 * 3

        val vertexBuffer = MemoryUtil.memAllocFloat(bufferSize)

        // Store line positions in the vertex buffer
        for (obj in sceneObjects) {
            val edges = boxEdges(obj.start, obj.end)
            for (e in edges) {
                vertexBuffer.put(e.start).put(e.end)
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