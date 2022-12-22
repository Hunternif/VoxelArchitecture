package hunternif.voxarch.editor.scene.models.box

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.SolidColorInstancedShader
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.FloatBufferWrapper
import hunternif.voxarch.editor.util.put
import hunternif.voxarch.util.toRadians
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack

/**
 * Renders the frame outline of a node that's currently selected
 */
class BoxFrameModel(
    private val singleColor: ColorRGBa? = null,
    private val lineWidth: Float = 1f,
) : BaseModel() {
    // 12 edges, 2 vertices per edge, 3f pos per vertex
    private var vertBufferSize = 12 * 2 * 3

    override val shader = SolidColorInstancedShader()

    private var instanceVboID = 0
    private val instances = mutableListOf<BoxMesh>()

    private val instanceVertexBuffer = FloatBufferWrapper()

    fun add(box: BoxMesh) {
        if (singleColor != null) {
            instances.add(BoxMesh(box.center, box.size, box.angleY, singleColor))
        } else {
            instances.add(box)
        }
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
                put(v.start)
                put(v.end)
            }
            flip()
        }
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

        initVertexAttributes {
            vector3f(0) // position attribute
        }

        // Create VBO for the instances of this model
        instanceVboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)

        initInstanceAttributes {
            vector4f(1) // color attribute
            mat4f(2) // model matrix instance attribute, uses ids 2-5
        }
        uploadInstanceData()
    }

    private fun uploadInstanceData() {
        // 16f is used by model matrix
        instanceVertexBuffer.prepare(instances.size * (4 + 16)).run {
            instances.forEach { it.run {
                put(color.toVector4f())
                put(
                    Matrix4f()
                        .translation(center)
                        .rotateY(angleY.toRadians())
                        .scale(size)
                        .translate(-0.5f, -0.5f, -0.5f)
                )
            }}
            flip()
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glBufferData(GL_ARRAY_BUFFER, instanceVertexBuffer.buffer, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    override fun render() {
        glDisable(GL_DEPTH_TEST)

        glLineWidth(lineWidth)
        glDrawArraysInstanced(GL_LINES, 0, vertBufferSize, instances.size)

        glEnable(GL_DEPTH_TEST)
    }
}