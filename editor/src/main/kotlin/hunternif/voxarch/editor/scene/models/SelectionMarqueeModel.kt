package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.SolidColorShader
import org.joml.Vector2f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack

class SelectionMarqueeModel : BaseModel() {
    override val shader = SolidColorShader(0xffffff)

    var start = Vector2f()
    var end = Vector2f()

    var visible = true

    override fun init() {
        super.init()
        readDepth = false
        initVertexAttributes {
            vector3f(0) // position attribute
        }
        update()
    }

    fun update() = MemoryStack.stackPush().use { stack ->
        val vertexBuffer = stack.mallocFloat(4 * 3)
        vertexBuffer.run {
            put(start.x + 0.5f).put(start.y + 0.5f).put(0f)
            put(end.x + 0.5f).put(start.y + 0.5f).put(0f)
            put(end.x + 0.5f).put(end.y + 0.5f).put(0f)
            put(start.x + 0.5f).put(end.y + 0.5f).put(0f)
            flip()
        }

        // Upload the vertex buffer
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
    }

    override fun render() {
        if (!visible) return
        glEnable(GL_LINE_STIPPLE)

        glLineWidth(1f)
        glLineStipple(1, 0x0f0f.toShort())
        glDrawArrays(GL_LINE_LOOP, 0, 4)

        glDisable(GL_LINE_STIPPLE)
    }
}