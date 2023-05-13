package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.models.box.BoxFace
import hunternif.voxarch.editor.scene.shaders.SolidColorShader
import hunternif.voxarch.editor.util.put
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack

/**
 * Renders a single highlighted face.
 */
class ResizeNodeModel : BaseModel() {
    private var bufferSize = 0

    override val shader = SolidColorShader(0xffffff, 0.2f)

    var face: BoxFace? = null
        set(value) {
            if (field != value) {
                field = value
                uploadVertexData()
            }
        }

    override fun init() {
        super.init()
        shader.depthOffset = -0.002f
        initVertexAttributes {
            vector3f(0) // position attribute
        }
    }

    fun update() {
        uploadVertexData()
    }

    private fun uploadVertexData() = MemoryStack.stackPush().use { stack ->
        bufferSize = if (face != null) 4 * 3 else 0
        val vertexBuffer = stack.mallocFloat(bufferSize)
        vertexBuffer.run {
            // Store line positions in the vertex buffer
            face?.apply {
                quadVertices.forEach { put(it) }
                flip() // rewind
            }
        }

        // Upload the vertex buffer
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
    }

    override fun render() {
//        glDisable(GL_DEPTH_TEST)

        glDrawArrays(GL_QUADS, 0, bufferSize)

//        glEnable(GL_DEPTH_TEST)
    }
}