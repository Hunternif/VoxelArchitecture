package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.SolidColorShader
import hunternif.voxarch.editor.util.AABBFace
import hunternif.voxarch.editor.util.FloatBufferWrapper
import hunternif.voxarch.editor.util.put
import org.lwjgl.opengl.GL33.*

/**
 * Renders a single highlighted face.
 */
class ResizeNodeModel : BaseModel() {
    private var bufferSize = 0
    private val vertexBuffer = FloatBufferWrapper()

    override val shader = SolidColorShader(0xffffff, 0.2f)

    var face: AABBFace? = null
        set(value) {
            if (field != value) {
                field = value
                uploadVertexData()
            }
        }

    override fun init() {
        super.init()
        initVertexAttributes {
            vector3f(0) // position attribute
        }
    }

    private fun uploadVertexData() {
        bufferSize = if (face != null) 4 * 3 else 0

        vertexBuffer.prepare(bufferSize).run {
            // Store line positions in the vertex buffer
            face?.apply {
                vertices.forEach { put(it) }
                flip() // rewind
            }
        }

        // Upload the vertex buffer
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer.buffer, GL_STATIC_DRAW)
    }

    override fun render() {
        glDisable(GL_DEPTH_TEST)

        glDrawArrays(GL_QUADS, 0, bufferSize)

        glEnable(GL_DEPTH_TEST)
    }
}