package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.SolidColorShader
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil

class FloorGridModel : BaseModel() {
    private var fromX = 0
    private var fromZ = 0
    private var toX = 0
    private var toZ = 0

    private var bufferSize = 0

    override val shader = SolidColorShader(0x333333)

    override fun init() {
        super.init()
        initVertexAttributes {
            vector3f(0) // position attribute
        }
        setSize(0, 0, 0, 0)
    }

    fun setSize(fromX: Int, fromZ: Int, toX: Int, toZ: Int) {
        this.fromX = fromX
        this.fromZ = fromZ
        this.toX = toX
        this.toZ = toZ
        val width = toX - fromX
        val length = toZ - fromZ
        val vertexCount = (width + 1)*2 + (length + 1)*2
        bufferSize = vertexCount * 3

        val vertexBuffer = MemoryUtil.memAllocFloat(bufferSize)

        // Store line positions in the vertex buffer
        for (x in fromX .. toX) {
            vertexBuffer
                .put(-0.5f + x).put(-0.5f).put(-0.5f + fromZ)
                .put(-0.5f + x).put(-0.5f).put(-0.5f + toZ)
        }
        for (z in fromZ .. toZ) {
            vertexBuffer
                .put(-0.5f + fromX).put(-0.5f).put(-0.5f + z)
                .put(-0.5f + toX).put(-0.5f).put(-0.5f + z)
        }
        vertexBuffer.flip() // rewind

        // Upload the vertex buffer
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
    }

    override fun render() {
        glEnable(GL_DEPTH_TEST)
        glLineWidth(1f)
        glDrawArrays(GL_LINES, 0, bufferSize)
    }
}