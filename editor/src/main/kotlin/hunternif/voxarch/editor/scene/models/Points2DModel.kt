package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.SolidColorShader
import hunternif.voxarch.editor.util.ColorRGBa
import org.joml.Vector2f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil
import kotlin.math.round

class Points2DModel(color: ColorRGBa = Colors.debug) : BaseModel() {
    private var bufferSize = 0

    override val shader = SolidColorShader(color)

    private var isDirty = false
    private val points = mutableListOf<Vector2f>()

    override fun init() {
        super.init()
        readDepth = false
        initVertexAttributes {
            vector3f(0) // position attribute
        }
        update()
    }

    fun add(point: Vector2f) {
        points.add(Vector2f(point))
        isDirty = true
    }

    fun clear() {
        points.clear()
        update()
    }

    fun update() {
        bufferSize = points.size * 3
        val vertexBuffer = MemoryUtil.memAllocFloat(bufferSize)
        vertexBuffer.run {
            for (p in points) {
                // round() + 0.5 to make it snap exactly to pixel position
                put(round(p.x) + 0.5f).put(round(p.y) + 0.5f).put(0f)
            }
            flip()
        }

        // Upload the vertex buffer
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
        isDirty = false
        MemoryUtil.memFree(vertexBuffer)
    }

    override fun render() {
        if (isDirty) update()
        glPointSize(4f)
        glDrawArrays(GL_POINTS, 0, points.size)
    }
}