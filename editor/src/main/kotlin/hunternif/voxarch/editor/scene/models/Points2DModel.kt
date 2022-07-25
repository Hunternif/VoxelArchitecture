package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.SolidColorShader
import hunternif.voxarch.editor.util.FloatBufferWrapper
import org.joml.Vector2f
import org.lwjgl.opengl.GL33.*
import kotlin.math.round

class Points2DModel : BaseModel() {
    private var bufferSize = 0

    override val shader = SolidColorShader(0xffffff)

    val points = mutableListOf<Vector2f>()

    private val vertexBuffer = FloatBufferWrapper()

    override fun init() {
        super.init()
        initVertexAttributes {
            vector3f(0) // position attribute
        }
        update()
    }

    fun update() {
        bufferSize = points.size * 3
        vertexBuffer.prepare(bufferSize).run {
            for (p in points) {
                // round() + 0.5 to make it snap exactly to pixel position
                put(round(p.x) + 0.5f).put(round(p.y) + 0.5f).put(0f)
            }
            flip()
        }

        // Upload the vertex buffer
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer.buffer, GL_STATIC_DRAW)
    }

    override fun render() {
        glDisable(GL_DEPTH_TEST)

        glDrawArrays(GL_POINTS, 0, points.size)

        glEnable(GL_DEPTH_TEST)
    }
}