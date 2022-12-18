package hunternif.voxarch.editor.scene.models

import org.lwjgl.opengl.GL33.*

class TransparentBoxModel : BoxInstancedModel<Box>() {
    override fun render() {
        glDisable(GL_DEPTH_TEST)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glDisable(GL_CULL_FACE)

        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, instances.size)
    }
}