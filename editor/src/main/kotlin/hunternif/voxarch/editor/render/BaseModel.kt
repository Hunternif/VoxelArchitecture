package hunternif.voxarch.editor.render

import org.joml.Matrix4f
import org.lwjgl.opengl.GL33.*

interface IModel {
    fun init() {}
    fun runFrame(viewProj: Matrix4f)
}

abstract class BaseModel : IModel {
    protected var vaoID = 0
    protected var vboID = 0

    protected val vertexAttribList = VertexAttribList()
    protected val instanceAttribList = VertexAttribList()

    abstract val shader: Shader

    /** Whether to read from depth buffer */
    var readDepth: Boolean = true

    /** Whether to write to depth buffer.
     * If read is disabled, write doesn't work anyway. */
    var writeDepth: Boolean = true

    override fun init() {
        shader.init()
        vaoID = glGenVertexArrays()
        glBindVertexArray(vaoID)

        vboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
    }

    protected inline fun initVertexAttributes(
        crossinline action: VertexAttribList.() -> Unit
    ) {
        vertexAttribList.action()
        vertexAttribList.upload()
    }

    protected inline fun initInstanceAttributes(
        crossinline action: VertexAttribList.() -> Unit
    ) {
        instanceAttribList.action()
        instanceAttribList.upload()
        for (attr in instanceAttribList.list) {
            glVertexAttribDivisor(attr.id, 1)
        }
    }

    final override fun runFrame(viewProj: Matrix4f) {
        shader.render(viewProj) {
            startFrame()
            render()
            endFrame()
        }
    }

    protected abstract fun render()

    private fun startFrame() {
        // Bind vertex array and attributes
        glBindVertexArray(vaoID)
        for (attr in vertexAttribList.list) {
            glEnableVertexAttribArray(attr.id)
        }
        for (attr in instanceAttribList.list) {
            glEnableVertexAttribArray(attr.id)
        }
        glDepthMask(writeDepth)
        if (readDepth) {
            glEnable(GL_DEPTH_TEST)
        } else {
            glDisable(GL_DEPTH_TEST)
        }
    }

    private fun endFrame() {
        // Unbind everything
        glBindVertexArray(0)
        for (attr in vertexAttribList.list) {
            glDisableVertexAttribArray(attr.id)
        }
        for (attr in instanceAttribList.list) {
            glDisableVertexAttribArray(attr.id)
        }
    }
}