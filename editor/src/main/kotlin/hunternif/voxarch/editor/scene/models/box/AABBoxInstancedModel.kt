package hunternif.voxarch.editor.scene.models.box

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.scene.shaders.MagicaVoxelShader
import hunternif.voxarch.editor.util.put
import hunternif.voxarch.editor.util.safeFlip
import org.joml.Matrix4f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

/** Renders instances of colored axis-aligned boxes. */
open class AABBoxInstancedModel<T : AABBoxMesh> : BaseModel() {
    private var instanceVboID = 0
    val instances = mutableListOf<T>()

    override val shader: Shader = MagicaVoxelShader()

    override fun init() = MemoryStack.stackPush().use { stack ->
        super.init()

        val vertexBuffer = stack.mallocFloat(boxVertices.size * 6)
        vertexBuffer.run {
            for (v in boxVertices) {
                put(v.pos.x).put(v.pos.y).put(v.pos.z)
                put(v.normal.x).put(v.normal.y).put(v.normal.z)
            }
            safeFlip()
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
            vector4f(2) // color instance attribute
            vector2f(3) // AO UV, unused
            mat4f(4) // model matrix instance attribute, uses ids 4-7
        }
        uploadInstanceData()
    }

    fun add(instance: T) {
        instances.add(instance)
    }

    fun clear() {
        instances.clear()
    }

    fun uploadInstanceData() {
        val instanceVertexBuffer = MemoryUtil.memAllocFloat(instances.size * 22)
        // 22 = 4f color + 2f + 16f matrix
        instanceVertexBuffer.run {
            instances.forEach { it.run {
                put(color.toVector4f())
                put(0f).put(0f) // AO UV, unused
                put(Matrix4f().translation(start).scale(size))
            }}
            safeFlip()
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glBufferData(GL_ARRAY_BUFFER, instanceVertexBuffer, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        MemoryUtil.memFree(instanceVertexBuffer)
    }

    override fun render() {
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)
        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, instances.size)
    }
}