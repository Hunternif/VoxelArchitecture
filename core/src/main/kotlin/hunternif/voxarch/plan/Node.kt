package hunternif.voxarch.plan

import hunternif.voxarch.util.INested
import hunternif.voxarch.vector.Vec3

/**
 * An architectural plan is build out of nested 3d boxes.
 * This is the base class for a 3d box.
 *
 *
 * **Coordinate axes** are like in Minecraft, i.e. Y is up.
 *  * X: width - the longer side, defines direction.
 *  * Y: height (up)
 *  * Z: depth
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
 * * A wall of "centric" width 2 will occupy 3 points, i.e. 3 blocks.
 * * In a room of "centric" size 2, its cross-section will look like this:
 * 1 block of wall, 1 block of empty space, 1 block of wall.
 *
 *
 * ## 2. "Natural" distance
 * This is how a normal sane person would measure buildings in Minecraft:
 * "if it uses 2 blocks to build, its size is 2". This convention should only be
 * used to measure distances, not position! It's useful at the building stage,
 * when describing the size of details like crenellations on a castle wall.
 *
 * * The room of "centric" size 2 (described above) will have a "natural" size 3.
 *
 * @param position position of this node relative to its parent's [origin].
 *        Children of this node are placed relative to its [origin].
 */
open class Node(
    position: Vec3
) : INested<Node> {
    /** This node's position relative to its parent's [origin]. */
    var position: Vec3 = position.clone()
        set(value) { field.set(value) }  // keep the same instance

    /** Internal offset of the low-XYZ corner from [origin].
     * By default, it's set so that origin is at the low-XYZ corner. */
    open var start: Vec3 = Vec3(0, 0, 0)
        set(value) { field.set(value) }  // keep the same instance

    /** Children's positions are relative to this point.
     * This point itself is relative to [position]. */
    // This is a separate property to make it easier to refactor later.
    val origin: Vec3 get() = start // children are placed relative to corner

    final override var parent: Node? = null

    /** Rotation around Y axis CCW in degrees */
    open var rotationY: Double = 0.0

    override val children: MutableList<Node> = mutableListOf()

    /**
     * Vector (width, height, depth) in "centric" coordinates,
     * not accounting for rotation.
     */
    open var size: Vec3 = Vec3(0, 0, 0)
        set(value) { field.set(value) } // keep the same instance
    var width: Double
        get() = size.x
        set(value) { size.x = value }
    var height: Double
        get() = size.y
        set(value) { size.y = value }
    var depth: Double
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
        child.position = position
    }

    /**
     * Returns boundaries defined by node size and its walls, if it has any.
     * Relative to local origin, not accounting for rotation.
     */
    open fun getGroundBoundaries(): List<GroundBoundary> {
        val corner = start
        return listOf(
            corner,
            corner.addZ(size.z),
            corner.add(size.x, 0.0, size.z),
            corner.addX(size.x),
            corner,
        ).zipWithNext()
    }

    constructor() : this(Vec3.ZERO)

    override fun toString(): String =
        "${this::class.java.simpleName} [${tags.joinToString(", ")}]"
}

typealias GroundBoundary = Pair<Vec3, Vec3>