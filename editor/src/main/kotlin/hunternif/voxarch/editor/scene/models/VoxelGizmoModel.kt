package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.SolidColorInstancedShader
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

/** Renders an RGB 3d gizmo at the centers of voxels. */
class VoxelGizmoModel : BaseModel() {
    private var bufferSize = 6 * 7 // 6 vertices, 3f pos + 4f color

    // half-length of axis line
    private val w = 0.4f
    private val colX = Colors.axisX
    private val colY = Colors.axisY
    private val colZ = Colors.axisZ

    private var instanceVboID = 0
    private val instances = mutableListOf<Vector3f>()

    override val shader = SolidColorInstancedShader()

    override fun init() = MemoryStack.stackPush().use { stack ->
        super.init()

        val vertexBuffer = stack.mallocFloat(bufferSize)
        vertexBuffer.run {
            // X
            put(-w).put(0f).put(0f).put(colX.r).put(colX.g).put(colX.b).put(colX.a)
            put( w).put(0f).put(0f).put(colX.r).put(colX.g).put(colX.b).put(colX.a)
            // Y
            put(0f).put(-w).put(0f).put(colY.r).put(colY.g).put(colY.b).put(colY.a)
            put(0f).put( w).put(0f).put(colY.r).put(colY.g).put(colY.b).put(colY.a)
            // Z
            put(0f).put(0f).put(-w).put(colZ.r).put(colZ.g).put(colZ.b).put(colZ.a)
            put(0f).put(0f).put( w).put(colZ.r).put(colZ.g).put(colZ.b).put(colZ.a)
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
            vector3f(2) // offset instance attribute
        }
        uploadInstanceData()
    }

    fun addPos(pos: Vector3f) {
        instances.add(pos)
    }

    fun clear() {
        instances.clear()
    }

    fun update() {
        uploadInstanceData()
    }

    private fun uploadInstanceData() {
        val instanceVertexBuffer = MemoryUtil.memAllocFloat(instances.size * 3).run {
            instances.forEach { it.run {
                put(it.x).put(it.y).put(it.z)
            }}
            flip()
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glBufferData(GL_ARRAY_BUFFER, instanceVertexBuffer, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    override fun render() {
        glDisable(GL_DEPTH_TEST)

        glLineWidth(3f)
        glDrawArraysInstanced(GL_LINES, 0, bufferSize, instances.size)

        glEnable(GL_DEPTH_TEST)
    }
}