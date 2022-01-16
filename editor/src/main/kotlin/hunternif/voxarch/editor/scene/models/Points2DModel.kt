package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.SolidColorShader
import org.joml.Vector2f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil

class Points2DModel : BaseModel() {
    private var bufferSize = 0

    override val shader = SolidColorShader(0xffffff)

    val points = mutableListOf<Vector2f>()

    override fun init() {
        super.init()
        initVertexAttributes {
            vector3f(0) // position attribute
        }
        update()
    }

    fun update() {
        bufferSize = points.size * 3
        val vertexBuffer = MemoryUtil.memAllocFloat(bufferSize)

        vertexBuffer.run {
            for (p in points) {
                put(p.x + 0.5f).put(p.y + 0.5f).put(0f)
            }
            flip()
        }

        // Upload the vertex buffer
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
    }

    override fun render() {
        glDisable(GL_DEPTH_TEST)

        glDrawArrays(GL_POINTS, 0, points.size)

        glEnable(GL_DEPTH_TEST)
    }
}