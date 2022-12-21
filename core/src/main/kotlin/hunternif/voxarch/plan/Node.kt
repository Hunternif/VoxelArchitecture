package hunternif.voxarch.plan

import hunternif.voxarch.util.INested
import hunternif.voxarch.vector.Vec3

/**
 * **Coordinate axes** are like in Minecraft, i.e. Y is up.
 *  * X: length - the longer side, defines direction.
 *  * Y: height (up)
 *  * Z: width
 *
 *  **Rotations** are counterclockwise, 0 degrees is East. (Minecraft rotations
 *  are clockwise.) This was done for consistency with mainstream vector math.
 *
 * ```
 * Top-down view of the reference frame:
 * Y
 *  +---->X (East)
 *  |
 *  V
 *  Z
 * ```
 *
 * At the planning stage, coordinates are floating-point 3d vectors.
 * But ultimately all structures will be built into a voxel world where
 * coordinates are _integer_ 3d vectors. Bridging this gap can be confusing.
 * I use 2 conventions for measuring the voxel space: **"centric"** and
 * **"natural"**.
 *
 *
 * ## 1. "Centric" position and distance
 * Think of voxel blocks as points in 3d space, where every integer point
 * corresponds to a center of a block. This convention defines how to position
 * blocks in space and a how to measure distance between them.
 * It should be used by default, unless otherwise specified.
 *
 * * Distance between adjacent blocks is 1.
 * * A room of "centric" size 0 occupies 1 block.
 * * A wall of "centric" length 2 will occupy 3 points, i.e. 3 blocks.
 * * In a room of "centric" size 2, its cross-section will look like this:
 * 1 block of wall, 1 block of empty space, 1 block of wall.
 *
 *
 * ## 2. "Natural" distance
 * This is how a normal sane person would measure buildings in Minecraft:
 * "if it uses 2 blocks to build, its size is 2". This convention should only
 * used to measure distances, not position! It's useful at the building stage,
 * when describing the size of details like crenellations on a castle wall.
 *
 * * The room of "centric" size 2 (described above) will have a "natural" size 3.
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
