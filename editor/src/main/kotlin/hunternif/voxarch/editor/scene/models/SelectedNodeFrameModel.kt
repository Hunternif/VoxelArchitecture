package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.editor.scene.shaders.SolidColorInstancedShader
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.FloatBufferWrapper
import hunternif.voxarch.editor.util.put
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack

/**
 * Renders the frame outline of a node that's currently selected
 */
class SelectedNodeFrameModel : BaseModel() {
    // 12 edges, 2 vertices per edge, 3f pos + 4f color per vertex
    private var vertBufferSize = 12 * 2 * (3 + 4)

    private val color = ColorRGBa.fromHex(0xffffff)
    override val shader = SolidColorInstancedShader()

    private var instanceVboID = 0
    private val instances = mutableListOf<SceneObject>()

    private val instanceVertexBuffer = FloatBufferWrapper()

    fun addNode(obj: SceneObject) {
        instances.add(obj)
    }

    fun clear() {
        instances.clear()
    }

    fun update() {
        uploadInstanceData()
    }

    override fun init() = MemoryStack.stackPush().use { stack ->
        super.init()

        val vertexBuffer = stack.mallocFloat(vertBufferSize)
        val edges = boxEdges(Vector3f(0f, 0f, 0f), Vector3f(1f, 1f, 1f))
        vertexBuffer.run {
            for (v in edges) {
                put(v.start).put(color.toVector4f())
                put(v.end).put(color.toVector4f())
            }
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

    private fun uploadInstanceData() {
        // 16f is used by model matrix
        instanceVertexBuffer.prepare(instances.size * 16).run {
            instances.forEach { it.run {
                put(Matrix4f().translation(start).scale(size))
            }}
            flip()
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glBufferData(GL_ARRAY_BUFFER, instanceVertexBuffer.buffer, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    override fun render() {
        glDisable(GL_DEPTH_TEST)

        glLineWidth(1f)
        glDrawArraysInstanced(GL_LINES, 0, vertBufferSize, instances.size)

        glEnable(GL_DEPTH_TEST)
    }
}