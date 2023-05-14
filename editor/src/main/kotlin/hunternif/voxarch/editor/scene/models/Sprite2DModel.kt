package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.TextureShader
import hunternif.voxarch.editor.util.resourcePath
import hunternif.voxarch.editor.util.safeFlip
import org.joml.Vector2f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack

class Sprite2DModel(texturePath: String) : BaseModel() {
    private val bufferSize = 4 * 5 // 4 vertices, 3f position and 2f uv

    override val shader = TextureShader(resourcePath(texturePath))

    /** Position on screen */
    var pos = Vector2f()

    var visible = true

    /** Whether to center the texture, i.e. make the center of the image
     * correspond to the given position. */
    var centered = false

    override fun init() {
        super.init()
        readDepth = false
        initVertexAttributes {
            vector3f(0) // position attribute
            vector2f(1) // texture uv attribute
        }
        update()
    }

    fun update() = MemoryStack.stackPush().use { stack ->
        val vertexBuffer = stack.mallocFloat(bufferSize)
        val size = Vector2f(
            shader.texture.width.toFloat(),
            shader.texture.height.toFloat()
        )
        val min = if (centered) Vector2f(size).mul(-0.5f).add(pos) else pos
        val max = Vector2f(min).add(size)

        vertexBuffer.run {
            // x y z u v
            put(min.x).put(min.y).put(0f).put(0f).put(1f)
            put(max.x).put(min.y).put(0f).put(1f).put(1f)
            put(max.x).put(max.y).put(0f).put(1f).put(0f)
            put(min.x).put(max.y).put(0f).put(0f).put(0f)
            safeFlip()
        }

        // Upload the vertex buffer
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
    }

    override fun render() {
        if (!visible) return
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glDrawArrays(GL_QUADS, 0, bufferSize)
    }
}