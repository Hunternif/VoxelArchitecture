package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.util.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.util.SnapOrigin
import hunternif.voxarch.util.rotateY
import hunternif.voxarch.util.snapOrigin
import java.util.*

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
    private val _blueprints = mutableListOf<Blueprint>()
    val blueprints: List<Blueprint> get() = _blueprints

    fun addBlueprint(bp: Blueprint, index: Int = -1) {
        if (index > -1) _blueprints.add(index, bp)
        else _blueprints.add(bp)
    }

    fun removeBlueprint(bp: Blueprint) {
        _blueprints.remove(bp)
    }

    // TODO: store Style declarations and apply them to the node
    var snapOrigin: SnapOrigin = SnapOrigin.CORNER
        set(value) {
            field = value
            node.snapOrigin(value)
        }

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

    /** Finds children in the subtree with nodes matching the given class and tags. */
    inline fun <reified N : Node> query(vararg tags: String): Sequence<SceneNode> = sequence {
        val tagSet = tags.toSet()
        val queue = LinkedList<SceneNode>()
        queue.add(this@SceneNode)
        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            if (next.node is N && (tagSet.isEmpty() || next.node.tags.containsAll(tagSet))) {
                yield(next)
            }
            queue.addAll(next.children.filterIsInstance<SceneNode>())
        }
    }
}