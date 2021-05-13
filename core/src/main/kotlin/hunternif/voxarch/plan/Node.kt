package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

/**
 * @param origin coordinates of nodes inside this Node are counted
 *               from this origin.
 */
abstract class Node(
    origin: Vec3
) {
    open var origin: Vec3 = origin.clone()
    var parent: Node? = null
    /** Rotation around Y axis in degrees */
    open var rotationY = 0.0

    private val _children = mutableListOf<Node>()
    val children: List<Node> get() = _children.toList()

    /**
     * Describes the purpose of this node. It can be used to find
     * a Builder with a particular architectural style or materials to it.
     *
     * When set, will recursively apply to all children that don't have a type.
     */
    var type: String? = null
        set(value) {
            children.filter { (it.type ?: field) == field }.forEach { it.type = value }
            field = value
        }

    /** For use with incremental building. True means that this node will not be re-built.  */
    var isBuilt = false

    fun addChild(child: Node) {
        child.parent?.removeChild(child)
        child.parent = this
        child.type = child.type ?: type
        _children.add(child)
    }

    fun addChild(child: Node, position: Vec3) {
        addChild(child)
        child.origin = position
    }

    fun removeChild(child: Node) {
        if (_children.remove(child)) child.parent = null
    }

}
