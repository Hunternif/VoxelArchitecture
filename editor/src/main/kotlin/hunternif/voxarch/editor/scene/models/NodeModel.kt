package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.scenegraph.SceneNode
import org.lwjgl.opengl.GL33.*

class NodeModel : BoxInstancedModel<SceneNode>() {
    override fun render() {
        glDisable(GL_DEPTH_TEST)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glDisable(GL_CULL_FACE)

        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, instances.size)
    }
}