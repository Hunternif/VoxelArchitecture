package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.SolidColorInstancedShader
import hunternif.voxarch.editor.util.put
import hunternif.voxarch.editor.util.safeFlip
import hunternif.voxarch.util.toRadians
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

/** Renders an RGB 3d gizmo at the given coordinates. */
class GizmoModel(
    /** position of the axis intersection point
     * relative to the center of the voxel */
    private val center: Vector3f = Vector3f(0f, 0f, 0f),
    private val lineWidth: Float = 2f,
    private val alpha: Float= 1f,
) : BaseModel() {
    private data class GizmoData(
        val pos: Vector3f,
        val size: Vector3f,
        val angleY: Float,
    )

    private var vertBufferSize = 6 * 7 // 6 vertices, 3f pos + 4f color

    private val colX = Colors.axisX.copy(a = alpha)
    private val colY = Colors.axisY.copy(a = alpha)
    private val colZ = Colors.axisZ.copy(a = alpha)

    private var instanceVboID = 0
    private val instances = linkedMapOf<Any, GizmoData>()

    override val shader = SolidColorInstancedShader()

    override fun init() = MemoryStack.stackPush().use { stack ->
        super.init()

        val vertexBuffer = stack.mallocFloat(vertBufferSize)
        vertexBuffer.run {
            // X
            put(-0.5f).put(center.y).put(center.z).put(colX.toVector4f())
            put( 0.5f).put(center.y).put(center.z).put(colX.toVector4f())
            // Y
            put(center.x).put(-0.5f).put(center.z).put(colY.toVector4f())
            put(center.x).put( 0.5f).put(center.z).put(colY.toVector4f())
            // Z
            put(center.x).put(center.y).put(-0.5f).put(colZ.toVector4f())
            put(center.x).put(center.y).put( 0.5f).put(colZ.toVector4f())
            safeFlip()
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

    fun addPos(ref: Any, pos: Vector3f, size: Vector3f, angleY: Float = 0f) {
        instances[ref] = GizmoData(pos, size, angleY)
    }

    fun remove(ref: Any) {
        instances.remove(ref)
    }

    fun clear() {
        instances.clear()
    }

    fun update() {
        uploadInstanceData()
    }

    private fun uploadInstanceData() {
        // 16f is used by model matrix
        val instanceVertexBuffer = MemoryUtil.memAllocFloat(instances.size * 16)
        instanceVertexBuffer.run {
            instances.values.forEach { it.run {
                put(
                    Matrix4f()
                        .translation(pos)
                        .rotateY(angleY.toRadians())
                        .scale(size)
                )
            }}
            safeFlip()
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glBufferData(GL_ARRAY_BUFFER, instanceVertexBuffer, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        MemoryUtil.memFree(instanceVertexBuffer)
    }

    override fun render() {
//        glDisable(GL_DEPTH_TEST)

        glLineWidth(lineWidth)
        glDrawArraysInstanced(GL_LINES, 0, vertBufferSize, instances.size)

//        glEnable(GL_DEPTH_TEST)
    }
}