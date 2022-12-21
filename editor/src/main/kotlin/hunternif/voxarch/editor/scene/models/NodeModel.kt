package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.IModel
import hunternif.voxarch.editor.scenegraph.SceneNode
import org.joml.Matrix4f

class NodeModel : IModel {
    val fillModel = AABBoxTransparentModel()
    val lineModel = AABBoxFrameModel()

    override fun init() {
        fillModel.init()
        lineModel.init()
    }

    fun add(node: SceneNode) {
        fillModel.add(AABBoxMesh(node.aabb.start, node.aabb.size, node.color.copy(a = 0.1f)))
        lineModel.add(node.aabb)
    }

    fun clear() {
        fillModel.clear()
        lineModel.clear()
    }

    fun update() {
        fillModel.uploadInstanceData()
        lineModel.update()
    }

    override fun runFrame(viewProj: Matrix4f) {
        fillModel.runFrame(viewProj)
        lineModel.runFrame(viewProj)
    }
}