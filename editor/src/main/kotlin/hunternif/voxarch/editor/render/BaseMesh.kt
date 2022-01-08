package hunternif.voxarch.editor.render

import org.lwjgl.opengl.GL33.*

abstract class BaseMesh {
    protected var vaoID = 0
    protected var vboID = 0

    protected val vertexAttribList = VertexAttribList()
    protected val instanceAttribList = VertexAttribList()

    open fun init() {
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

    fun runFrame() {
        startFrame()
        render()
        endFrame()
    }

    abstract protected fun render()

    protected fun startFrame() {
        // Bind vertex array and attributes
        glBindVertexArray(vaoID)
        for (attr in vertexAttribList.list) {
            glEnableVertexAttribArray(attr.id)
        }
        for (attr in instanceAttribList.list) {
            glEnableVertexAttribArray(attr.id)
        }
    }

    protected fun endFrame() {
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