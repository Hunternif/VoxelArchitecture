package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.IModel
import hunternif.voxarch.editor.scene.models.box.BoxMesh
import hunternif.voxarch.editor.scene.models.box.BoxTransparentModel
import hunternif.voxarch.editor.scene.models.box.BoxFrameModel
import hunternif.voxarch.editor.scenegraph.SceneNode
import org.joml.Matrix4f

class NodeModel : IModel {
    val fillModel = BoxTransparentModel()
    val lineModel = BoxFrameModel()

    override fun init() {
        fillModel.init()
        lineModel.init()
    }

    fun add(node: SceneNode) {
        fillModel.add(BoxMesh(node.box.center, node.box.size, node.box.angleY, node.color.copy(a = 0.1f)))
        lineModel.add(node.box)
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