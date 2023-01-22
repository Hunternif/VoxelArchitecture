package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.util.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.util.rotateY

class SceneNode(
    id: Int,
    val node: Node,
    color: ColorRGBa = Colors.defaultNodeBox,
    isGenerated: Boolean = false,
) : SceneObject(
    id,
    color = color,
    isGenerated = isGenerated,
) {
    val blueprints = mutableListOf<Blueprint>()

    init {
        update()
    }

    override fun addChild(child: SceneObject) {
        super.addChild(child)
        (child as? SceneNode)?.node?.let {
            // prevent double-adding, especially when generating nodes
            if (it.parent != node) node.addChild(it)
        }
    }

    override fun removeChild(child: SceneObject): Boolean {
        if (super.removeChild(child)) {
            (child as? SceneNode)?.node?.let {
                node.removeChild(it)
            }
            return true
        }
        return false
    }

    override fun update() {
        val globalRotation = node.findGlobalRotation()
        box.angleY = globalRotation.toFloat()
        box.center.set(node.findGlobalPosition() + node.localCenter.rotateY(globalRotation))
        box.size.set(node.size).add(1f, 1f, 1f)
        box.updateMesh()
    }

    val nodeClassName: String = node.javaClass.simpleName
    private val strRepr: String by lazy { "$nodeClassName $id" }
    override fun toString() = strRepr
}