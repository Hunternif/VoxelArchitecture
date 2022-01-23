package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.models.BoxInstancedModel.InstanceData
import hunternif.voxarch.editor.scene.shaders.MagicaVoxelShader
import hunternif.voxarch.editor.util.ColorRGBa
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

abstract class BoxInstancedModel<T : InstanceData> : BaseModel() {
    open class InstanceData(
        val start: Vector3f,
        val end: Vector3f,
        var color: ColorRGBa,
    ) {
        val size: Vector3f = Vector3f()
            get() = field.set(end).sub(start)
    }

    private var instanceVboID = 0
    val instances = mutableListOf<T>()

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

    protected fun uploadInstanceData() {
        val instanceVertexBuffer = MemoryUtil.memAllocFloat(instances.size * 10).run {
            instances.forEach { it.run {
                put(start.x).put(start.y).put(start.z)
                put(size.x).put(size.y).put(size.z)
                put(color.r).put(color.g).put(color.b).put(color.a)
            }}
            flip()
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glBufferData(GL_ARRAY_BUFFER, instanceVertexBuffer, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    override fun render() {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)
        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, instances.size)
    }
}