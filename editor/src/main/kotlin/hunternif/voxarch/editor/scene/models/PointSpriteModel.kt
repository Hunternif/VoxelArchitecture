package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.PointSpriteShader
import hunternif.voxarch.editor.util.put
import hunternif.voxarch.editor.util.resourcePath
import hunternif.voxarch.editor.util.safeFlip
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil

/**
 * Renders points as textures, matching texture pixels to screen pixels,
 * centered at the point.
 * The texture must be square.
 */
class PointSpriteModel(texturePath: String) : BaseModel() {

    private val points = linkedMapOf<Any, Vector3f>()

    override val shader = PointSpriteShader(resourcePath(texturePath))

    override fun init() {
        super.init()
        initVertexAttributes {
            vector3f(0) // position attribute
        }
        uploadVertexData()
    }

    fun addPoint(ref: Any, pos: Vector3f) {
        points[ref] = pos
    }

    fun removePoint(ref: Any) {
        points.remove(ref)
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
            points.values.forEach { put(it) }
            safeFlip()
        }
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
        MemoryUtil.memFree(vertexBuffer)
    }

    override fun render() {
        glEnable(GL_POINT_SPRITE)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glPointSize(shader.texture.width.toFloat())
        glDrawArrays(GL_POINTS, 0, points.size)

        glDisable(GL_POINT_SPRITE)
    }
}