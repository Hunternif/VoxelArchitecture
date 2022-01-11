package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.meshes.boxVertices
import hunternif.voxarch.editor.scene.shaders.MagicaVoxelShader
import hunternif.voxarch.editor.util.ColorRGBa
import org.joml.Vector3f
import org.joml.Vector3i
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import kotlin.math.max
import kotlin.math.min

class NodeModel : BaseModel() {
    data class NodeData(
        val start: Vector3f,
        val end: Vector3f,
        val color: ColorRGBa,
    ) {
        val width get() = end.x - start.x
        val height get() = end.y - start.y
        val length get() = end.z - start.z
    }

    private var instanceVboID = 0
    private val nodes = mutableListOf<NodeData>()

    override val shader = MagicaVoxelShader()

    override fun init() = MemoryStack.stackPush().use { stack ->
        super.init()

        val vertexBuffer = stack.mallocFloat(boxVertices.size * 6)
        vertexBuffer.run {
            for (v in boxVertices) {
                put(v.pos.x).put(v.pos.y).put(v.pos.z)
                put(v.normal.x).put(v.normal.y).put(v.normal.z)
            }
            flip()
        }
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

        initVertexAttributes {
            vector3f(0) // position attribute
            vector3f(1) // normal attribute
        }

        // Create VBO for the instances of this model
        instanceVboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)

        initInstanceAttributes {
            vector3f(2) // offset instance attribute
            vector3f(3) // scale instance attribute
            vector4f(4) // color instance attribute
        }
        uploadInstanceData()
    }

    fun addNode(start: Vector3i, end: Vector3i, color: ColorRGBa) {
        nodes.add(
            NodeData(
                Vector3f(
                    -0.5f + min(start.x, end.x),
                    -0.5f + min(start.y, end.y),
                    -0.5f + min(start.z, end.z)
                ),
                Vector3f(
                    0.5f + max(start.x, end.x),
                    0.5f + max(start.y, end.y),
                    0.5f + max(start.z, end.z)
                ),
                color
            )
        )
        uploadInstanceData()
    }

    private fun uploadInstanceData() {
        val instanceVertexBuffer = MemoryUtil.memAllocFloat(nodes.size * 10).run {
            nodes.forEach { it.run {
                put(start.x).put(start.y).put(start.z)
                put(width).put(height).put(length)
                put(color.r).put(color.g).put(color.b).put(color.a)
            }}
            flip()
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glBufferData(GL_ARRAY_BUFFER, instanceVertexBuffer, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    override fun render() {
        glDisable(GL_DEPTH_TEST)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glDisable(GL_CULL_FACE)

        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, nodes.size)
    }
}