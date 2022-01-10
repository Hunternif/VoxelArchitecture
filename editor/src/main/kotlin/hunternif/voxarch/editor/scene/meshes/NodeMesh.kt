package hunternif.voxarch.editor.scene.meshes

import hunternif.voxarch.editor.render.BaseMesh
import hunternif.voxarch.editor.util.ColorRGBa
import org.joml.Vector3f
import org.joml.Vector3i
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import kotlin.math.max
import kotlin.math.min

class NodeMesh(
    private val color: ColorRGBa,
) : BaseMesh() {
    private var start = Vector3f()
    private var end = Vector3f()
    private var instanceVboID = 0

    override fun init() = MemoryStack.stackPush().use { stack ->
        super.init()
        setPosition(Vector3i(), Vector3i())
        initVertexAttributes {
            vector3f(0) // position attribute
            vector3f(1) // normal attribute
        }

        // I'm reusing the instanced Magica Voxel shader, but with only 1 instance
        instanceVboID = glGenBuffers()
        val instanceVertexBuffer = stack.mallocFloat(7).run {
            put(0f).put(0f).put(0f) // single instance
            put(color.r).put(color.g).put(color.b).put(color.a)
            flip()
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glBufferData(GL_ARRAY_BUFFER, instanceVertexBuffer, GL_STATIC_DRAW)
        initInstanceAttributes {
            vector3f(2) // offset instance attribute
            vector4f(3) // color instance attribute
        }
    }

    fun setPosition(start: Vector3i, end: Vector3i) {
        this.start = Vector3f(
            -0.5f + min(start.x, end.x),
            -0.5f + min(start.y, end.y),
            -0.5f + min(start.z, end.z)
        )
        this.end = Vector3f(
            0.5f + max(start.x, end.x),
            0.5f + max(start.y, end.y),
            0.5f + max(start.z, end.z)
        )
        uploadVertexData()
    }

    private fun uploadVertexData() = MemoryStack.stackPush().use { stack ->
        val width = end.x - start.x
        val height = end.y - start.y
        val length = end.z - start.z

        // Create a float buffer of vertices
        val vertexBuffer = stack.mallocFloat(boxVertices.size * 6)
        vertexBuffer.run {
            for (v in boxVertices) {
                put(start.x + v.pos.x * width)
                put(start.y + v.pos.y * height)
                put(start.z + v.pos.z * length)
                put(v.normal.x)
                put(v.normal.y)
                put(v.normal.z)
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

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)

        glDrawArrays(GL_TRIANGLES, 0, 36)
    }
}