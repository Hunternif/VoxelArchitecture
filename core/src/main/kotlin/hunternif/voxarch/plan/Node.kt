package hunternif.voxarch.plan

import hunternif.voxarch.util.INested
import hunternif.voxarch.vector.Vec3

/**
 * @param origin coordinates of nodes inside this Node are counted
 *               from this origin.
 */
open class Node(
    origin: Vec3
) : INested<Node> {
    var origin: Vec3 = origin.clone()
        set(value) {
            field.set(value) // keep the same instance
        }
    override var parent: Node? = null
    /** Rotation around Y axis in degrees */
    open var rotationY = 0.0

    override val children: MutableList<Node> = mutableListOf()

    open var size: Vec3 = Vec3(0, 0, 0)
        set(value) { field.set(value) } // keep the same instance
    // By default, individual dimensions are read-only
    open var width: Double get() = size.x
        set(value) {}
    open var height: Double get() = size.y
        set(value) {}
    open var length: Double get() = size.z
        set(value) {}

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

    override fun addChild(child: Node) {
        super.addChild(child)
        child.type = child.type ?: type
    }

    fun addChild(child: Node, position: Vec3) {
        addChild(child)
        child.origin = position
    }

    constructor() : this(Vec3.ZERO)
}
