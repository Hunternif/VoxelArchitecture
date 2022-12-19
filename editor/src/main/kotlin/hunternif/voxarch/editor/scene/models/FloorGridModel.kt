package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.SolidColorShader
import hunternif.voxarch.editor.util.FloatBufferWrapper
import org.lwjgl.opengl.GL33.*
import java.lang.Integer.min
import kotlin.math.max

class FloorGridModel : BaseModel() {
    private var fromX = 0
    private var fromZ = 0
    private var toX = 0
    private var toZ = 0

    private var bufferSize = 0
    private val vertexBuffer = FloatBufferWrapper()

    override val shader = SolidColorShader(0x333333)

    override fun init() {
        super.init()
        initVertexAttributes {
            vector3f(0) // position attribute
        }
        uploadVertexData()
    }

    fun setSize(fromX: Int, fromZ: Int, toX: Int, toZ: Int) {
        this.fromX = min(fromX, toX)
        this.fromZ = min(fromZ, toX)
        // + 1 because we need to draw 1 more line than there are voxels,
        // i.e. start _before_ the 1st voxel and finish _after_ the last voxel.
        this.toX = max(fromX, toX) + 1
        this.toZ = max(fromZ, toZ) + 1
        uploadVertexData()
    }

    private fun uploadVertexData() {
        val length = toX - fromX
        val width = toZ - fromZ
        val vertexCount = (width + 1)*2 + (length + 1)*2
        bufferSize = vertexCount * 3

        vertexBuffer.prepare(bufferSize).run {
            // Store line positions in the vertex buffer
            for (x in fromX..toX) {
                put(-0.5f + x).put(-0.5f).put(-0.5f + fromZ)
                put(-0.5f + x).put(-0.5f).put(-0.5f + toZ)
            }
            for (z in fromZ..toZ) {
                put(-0.5f + fromX).put(-0.5f).put(-0.5f + z)
                    .put(-0.5f + toX).put(-0.5f).put(-0.5f + z)
            }
            flip() // rewind
        }

        // Upload the vertex buffer
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer.buffer, GL_STATIC_DRAW)
    }

    override fun render() {
        glEnable(GL_DEPTH_TEST)
        glLineWidth(1f)
        glDrawArrays(GL_LINES, 0, bufferSize)
    }
}