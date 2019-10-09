package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3


/**
 * @param origin will be copied
 */
abstract class Node(
    origin: Vec3
) {
    var origin: Vec3 = origin.clone()
    var parent: Node? = null
    /** Rotation around Y axis in degrees */
    open var rotationY = 0.0

    private val _children = mutableListOf<Node>()
    val children: List<Node>
        get() = _children.toList()

    /** The purpose of this node. It can be used by a generator
     * to assign a particular architectural style or materials to it. */
    var type: String? = null

    /** For use with incremental building. True means that this node will not be re-built.  */
    var isBuilt = false

    fun addChild(child: Node) {
        child.parent?.removeChild(child)
        child.parent = this
        _children.add(child)
    }

    fun removeChild(child: Node) {
        if (_children.remove(child)) child.parent = null
    }

}
