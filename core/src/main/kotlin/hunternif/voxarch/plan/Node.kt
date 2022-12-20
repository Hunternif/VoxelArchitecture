package hunternif.voxarch.plan

import hunternif.voxarch.util.INested
import hunternif.voxarch.vector.Vec3

/**
 * Coordinate system:
 * ```
 * Y
 *  +--> X (East)
 *  |
 *  V
 *  Z
 *  (South)
 * ```
 * - X: length - the longer side, defines direction.
 * - Y: height (up)
 * - Z: width
 *
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
    final override var parent: Node? = null

    /** Rotation around Y axis CCW in degrees */
    open var rotationY: Double = 0.0

    override val children: MutableList<Node> = mutableListOf()

    /**
     * Vector (length, height, width), doesn't take rotation into account.
     */
    var size: Vec3 = Vec3(0, 0, 0)
        set(value) { field.set(value) } // keep the same instance
    var length: Double
        get() = size.x
        set(value) { size.x = value }
    var height: Double
        get() = size.y
        set(value) { size.y = value }
    var width: Double
        get() = size.z
        set(value) { size.z = value }

    /**
     * Tags can be Style class names, or building part names etc.
     * They will be used to find a Builder with a particular architectural style,
     * or building materials.
     * Does not automatically apply to children; if you need inheritance, make sure
     * to add the tags to children manually.
     */
    val tags = LinkedHashSet<String>()

    /** For use with incremental building. True means that this node will not be re-built.  */
    var isBuilt = false

    fun addChild(child: Node, position: Vec3) {
        addChild(child)
        child.origin = position
    }

    constructor() : this(Vec3.ZERO)
}
