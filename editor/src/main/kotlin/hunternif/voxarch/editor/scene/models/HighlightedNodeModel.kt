package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.IModel
import hunternif.voxarch.editor.scene.models.box.BoxFrameModel
import hunternif.voxarch.editor.scene.models.box.BoxMesh
import hunternif.voxarch.editor.scene.models.box.BoxTransparentModel
import hunternif.voxarch.editor.scenegraph.SceneObject
import org.joml.Matrix4f

/**
 * Used to highlight a single node within selection
 */
class HighlightedNodeModel : IModel {
    private val color = Colors.selectedNodeOutline

    private val fillModel = BoxTransparentModel().apply {
        readDepth = false
    }
    private val lineModel = BoxFrameModel(color, 2f).apply {
        readDepth = false
    }

    override fun init() {
        fillModel.init()
        lineModel.init()
    }

    fun add(node: SceneObject) {
        fillModel.add(node, BoxMesh(node.box.center, node.box.size, node.box.angleY, color.copy(a = 0.2f)))
        lineModel.add(node, node.box)
    }

    fun clear() {
        fillModel.clear()
        lineModel.clear()
    }

    operator fun contains(node: SceneObject) = node in lineModel

    fun remove(node: SceneObject) {
        fillModel.remove(node)
        lineModel.remove(node)
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