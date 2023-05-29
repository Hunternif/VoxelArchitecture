package hunternif.voxarch.plan

import hunternif.voxarch.builder.Builder
import hunternif.voxarch.util.INested
import hunternif.voxarch.vector.Vec3

/**
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
        set(value) { field.set(value) }  // keep the same instance

    /** Internal offset of the low-XZ corner.
     * Children are placed relative to parent's [origin], but parent's [start]
     * suggests where children should be placed.
     * By default, it's set so that origin is at the low-XYZ corner. */
    open var start: Vec3 = Vec3(0, 0, 0)
        set(value) { field.set(value) }  // keep the same instance

    final override var parent: Node? = null

    /** Rotation around Y axis CCW in degrees */
    open var rotationY: Double = 0.0

    /** If set, this builder class will have higher priority than BuilderConfig */
    var builder: Builder<*>? = null

    /** If true, the node will not be generated, only used as boundary for calculations. */
    var transparent: Boolean = false

    /** Children will only place voxels inside this mask. */
    var clipMask: ClipMask = ClipMask.OFF

    override val children: MutableList<Node> = mutableListOf()

    /**
     * Vector (width, height, depth) in "centric" coordinates,
     * not accounting for rotation.
     */
    open var size: Vec3 = Vec3(0, 0, 0)
        set(value) { field.set(value) } // keep the same instance
    /** "centric" width (X axis) */
    var width: Double
        get() = size.x
        set(value) { size.x = value }
    /** "centric" height (Y axis) */
    var height: Double
        get() = size.y
        set(value) { size.y = value }
    /** "centric" depth (Z axis) */
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

    fun addChild(child: Node, position: Vec3) {
        addChild(child)
        child.origin = position
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