package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.IModel
import hunternif.voxarch.editor.scenegraph.SceneNode
import org.joml.Matrix4f

class NodeModel : IModel {
    val fillModel = TransparentBoxModel()
    val lineModel = BoxFrameModel()

    override fun init() {
        fillModel.init()
        lineModel.init()
    }

    fun add(node: SceneNode) {
        fillModel.add(Box(node.start, node.size, node.color.copy(a = 0.1f)))
        lineModel.add(node)
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