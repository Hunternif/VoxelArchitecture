package hunternif.voxarch.editor.scene.models.box

import org.lwjgl.opengl.GL33.*

class AABBoxTransparentModel : AABBoxInstancedModel<AABBoxMesh>() {
    override fun render() {
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glDisable(GL_CULL_FACE)

        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, instances.size)
    }
}