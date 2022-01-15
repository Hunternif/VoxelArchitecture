package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.plan.Node
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*
import kotlin.math.abs
import kotlin.math.min

class NodeModel : BoxInstancedModel<Node>() {
    fun addNode(node: Node, start: Vector3f, end: Vector3f, color: ColorRGBa) {
        instances.add(
            InstanceData(
                Vector3f(
                    -0.5f + min(start.x, end.x),
                    -0.5f + min(start.y, end.y),
                    -0.5f + min(start.z, end.z)
                ),
                Vector3f(
                    1f + abs(end.x - start.x),
                    1f + abs(end.y - start.y),
                    1f + abs(end.z - start.z),
                ),
                color,
                node,
            )
        )
        uploadInstanceData()
    }

    fun clear() {
        instances.clear()
    }

    override fun render() {
        glDisable(GL_DEPTH_TEST)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glDisable(GL_CULL_FACE)

        glDrawArraysInstanced(GL_TRIANGLES, 0, 36, instances.size)
    }
}