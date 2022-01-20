package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.PointSpriteShader
import hunternif.voxarch.editor.util.resourcePath
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil

/**
 * Renders points as textures, matching texture pixels to screen pixels,
 * centered at the point.
 * The texture must be square.
 */
class PointSpriteModel(texturePath: String) : BaseModel() {

    private val points = mutableListOf<Vector3f>()

    override val shader = PointSpriteShader(resourcePath(texturePath))

    override fun init() {
        super.init()
        initVertexAttributes {
            vector3f(0) // position attribute
        }
        uploadVertexData()
    }

    fun addPoint(pos: Vector3f) {
        points.add(pos)
    }

    fun clear() {
        points.clear()
    }

    fun update() {
        uploadVertexData()
    }

    private fun uploadVertexData() {
        val bufferSize = points.size * 3
        val vertexBuffer = MemoryUtil.memAllocFloat(bufferSize)
        vertexBuffer.run {
            points.forEach { put(it.x).put(it.y).put(it.z) }
            flip()
        }
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
    }

    override fun render() {
        glActiveTexture(GL_TEXTURE0)
        shader.texture.bind()
        glDisable(GL_DEPTH_TEST)

        glEnable(GL_POINT_SPRITE)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glPointSize(shader.texture.width.toFloat())
        glDrawArrays(GL_POINTS, 0, points.size)

        glDisable(GL_POINT_SPRITE)

        glEnable(GL_DEPTH_TEST)
    }
}