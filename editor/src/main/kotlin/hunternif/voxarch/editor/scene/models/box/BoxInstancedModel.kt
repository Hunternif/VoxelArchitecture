package hunternif.voxarch.editor.scene.models.box

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.scene.shaders.MagicaVoxelShader
import hunternif.voxarch.editor.util.FloatBufferWrapper
import hunternif.voxarch.editor.util.put
import hunternif.voxarch.util.toRadians
import org.joml.Matrix4f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack

/** Renders instances of colored oriented boxes (not axis-aligned). */
open class BoxInstancedModel<T : BoxMesh> : BaseModel() {
    private var instanceVboID = 0
    val instances = mutableListOf<T>()

    private val instanceVertexBuffer = FloatBufferWrapper()

    override val shader: Shader = MagicaVoxelShader()

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
            vector4f(2) // color instance attribute
            mat4f(3) // model matrix instance attribute, uses ids 3-6
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
        // 20 = 4f color + 16f matrix
        instanceVertexBuffer.prepare(instances.size * 20).run {
            instances.forEach {
                it.run {
                    put(color.toVector4f())
                    put(
                        Matrix4f()
                            .translation(center)
                            .rotateY(angleY.toRadians())
                            .scale(size)
                            .translate(-0.5f, -0.5f, -0.5f)
                    )
                }
            }
            flip()
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboID)
        glBufferData(GL_ARRAY_BUFFER, instanceVertexBuffer.buffer, GL_STATIC_DRAW)
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