package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.SolidColorInstancedShader
import hunternif.voxarch.editor.util.FloatBufferWrapper
import hunternif.voxarch.editor.util.put
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack

/** Renders an RGB 3d gizmo at the given coordinates. */
class GizmoModel : BaseModel() {
    private data class GizmoData(
        val pos: Vector3f,
        /** length of axis line */
        val length: Float,
    )

    private var vertBufferSize = 6 * 7 // 6 vertices, 3f pos + 4f color
    private val instanceVertexBuffer = FloatBufferWrapper()

    private val colX = Colors.axisX
    private val colY = Colors.axisY
    private val colZ = Colors.axisZ

    private var instanceVboID = 0
    private val instances = mutableListOf<GizmoData>()

    override val shader = SolidColorInstancedShader()

    override fun init() = MemoryStack.stackPush().use { stack ->
        super.init()

        val vertexBuffer = stack.mallocFloat(vertBufferSize)
        vertexBuffer.run {
            // X
            put(-0.5f).put(0f).put(0f).put(colX.toVector4f())
            put( 0.5f).put(0f).put(0f).put(colX.toVector4f())
            // Y
            put(0f).put(-0.5f).put(0f).put(colY.toVector4f())
            put(0f).put( 0.5f).put(0f).put(colY.toVector4f())
            // Z
            put(0f).put(0f).put(-0.5f).put(colZ.toVector4f())
            put(0f).put(0f).put( 0.5f).put(colZ.toVector4f())
            flip()
        }
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

        initVertexAttributes {
            vector3f(0) // position attribute
            vector4f(1) // color attribute
        }

        // Create VBO for the instances of this model
        instanceVboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)

        initInstanceAttributes {
            mat4f(2) // model matrix instance attribute, uses ids 2-5
        }
        uploadInstanceData()
    }

    fun addPos(pos: Vector3f, length: Float = 1f) {
        instances.add(GizmoData(pos, length))
    }

    fun clear() {
        instances.clear()
    }

    fun update() {
        uploadInstanceData()
    }

    private fun uploadInstanceData() {
        // 16f is used by model matrix
        instanceVertexBuffer.prepare(instances.size * 16).run {
            instances.forEach { it.run {
                put(Matrix4f().translation(pos).scale(length, length, length))
            }}
            flip()
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glBufferData(GL_ARRAY_BUFFER, instanceVertexBuffer.buffer, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    override fun render() {
        glDisable(GL_DEPTH_TEST)

        glLineWidth(2f)
        glDrawArraysInstanced(GL_LINES, 0, vertBufferSize, instances.size)

        glEnable(GL_DEPTH_TEST)
    }
}