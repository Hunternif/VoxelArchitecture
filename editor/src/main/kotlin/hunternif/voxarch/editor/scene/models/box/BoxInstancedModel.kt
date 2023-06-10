package hunternif.voxarch.editor.scene.models.box

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.MagicaVoxelShader
import hunternif.voxarch.editor.util.put
import hunternif.voxarch.editor.util.safeFlip
import hunternif.voxarch.util.toRadians
import org.joml.Matrix4f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

/** Renders instances of colored oriented boxes (not axis-aligned). */
open class BoxInstancedModel<T : BoxMesh> : BaseModel() {
    private var instanceVboID = 0
    val instances = linkedMapOf<Any, T>()

    override val shader = MagicaVoxelShader()

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

    fun add(ref: Any, instance: T) {
        instances[ref] = instance
    }

    fun remove(ref: Any) {
        instances.remove(ref)
    }

    operator fun contains(ref: Any) = ref in instances

    fun clear() {
        instances.clear()
    }

    fun uploadInstanceData() {
        // 22 = 4f color + 2f + 16f matrix
        val instanceVertexBuffer = MemoryUtil.memAllocFloat(instances.size * 22)
        instanceVertexBuffer.run {
            instances.values.forEach {
                it.run {
                    put(color.toVector4f())
                    put(0f).put(0f) // AO UV, unused
                    put(
                        Matrix4f()
                            .translation(center)
                            .rotateY(angleY.toRadians())
                            .scale(size)
                            .translate(-0.5f, -0.5f, -0.5f)
                    )
                }
            }
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