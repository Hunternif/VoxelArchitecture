package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.scene.models.NodeModel.NodeData
import hunternif.voxarch.editor.util.AABB2Df
import hunternif.voxarch.editor.util.AABBFace
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.plan.Node
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*
import kotlin.math.max
import kotlin.math.min

class NodeModel : BoxInstancedModel<NodeData>() {
    class NodeData(
        start: Vector3f,
        end: Vector3f,
        color: ColorRGBa,
        val node: Node,
    ) : InstanceData(start, end, color) {
        /** AABB in screen coordinates relative to viewport.
         * Can be updated at any time. */
        val screenAABB: AABB2Df = AABB2Df()
        val faces: Array<AABBFace> by lazy { boxFaces(start, end, 0.1f) }
    }

    fun addNode(
        node: Node,
        start: Vector3f,
        end: Vector3f,
        color: ColorRGBa
    ): NodeData {
        val instance = NodeData(
            Vector3f(
                -0.5f + min(start.x, end.x),
                -0.5f + min(start.y, end.y),
                -0.5f + min(start.z, end.z)
            ),
            Vector3f(
                0.5f + max(start.x, end.x),
                0.5f + max(start.y, end.y),
                0.5f + max(start.z, end.z)
            ),
            color,
            node,
        )
        instances.add(instance)
        return instance
    }

    fun update() {
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