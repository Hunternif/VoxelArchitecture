package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.BaseModel
import hunternif.voxarch.editor.scene.shaders.InfiniteGridShader
import org.lwjgl.opengl.GL33.*

class InfiniteGridModel : BaseModel() {
    override val shader = InfiniteGridShader()

    init {
        readDepth = false
    }

    override fun render() {
        glDisable(GL_CULL_FACE)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glDrawArrays(GL_TRIANGLES, 0, 6)
    }
}