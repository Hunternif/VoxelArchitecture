package hunternif.voxarch.plan

import hunternif.voxarch.util.Box
import hunternif.voxarch.util.MathUtil
import hunternif.voxarch.util.SnapOrigin
import hunternif.voxarch.util.snapStart
import hunternif.voxarch.vector.Vec3

/**
 * ```
 * Y
 *  +------------> X (East)
 *  | start ---+
 *  | | origin |
 *  | +--------+
 *  V
 *  Z
 * ```
 *
 * Components of [size] vector are equal to distances between corner blocks
 * of the room. It would take that number + 1 blocks to build each boundary
 * of the room in a world.
 *
 * @param position position of this node relative to its parent's [origin].
 * @param size size in centric coordinates, see [Node].
 */
open class Room(
    position: Vec3,
    size: Vec3
) : Node(position) {

    init {
        this.size = size
        // for legacy tests to pass:
        snapStart(SnapOrigin.FLOOR_CENTER)
    }

    /** Relative to the parent's origin. Doesn't take into account rotation! */
    val boundingBox: Box get() = Box.fromCorners(position + start, position + start + size)

    val rooms get() = children.filterIsInstance<Room>()
    val floors get() = children.filterIsInstance<Floor>()
    val walls get() = children.filterIsInstance<Wall>()
    val props get() = children.filterIsInstance<Prop>()
    val gates get() = children.filterIsInstance<Gate>()
    val hatches get() = children.filterIsInstance<Hatch>()

    /**
     * Add to children 4 walls matching room edges
     */
    @Deprecated("Use PolyRoom")
    fun createFourWalls() {
        val a = size.x/2
        val b = size.z/2
        val c = innerFloorCenter
        /*
		 * (Wall indices)
		 * +---------> X
		 * | start
		 * |   +- 1 -+
		 * |   |     |
		 * |   2  c  0 b
		 * |   |     |
		 * |   +- 3 -+
		 * V      a
		 * Z
		 */
        // Going counterclockwise:
        wall(c.add(a, 0.0, b), c.add(a, height, -b))
        wall(c.add(a, 0.0, -b), c.add(-a, height, -b))
        wall(c.add(-a, 0.0, -b), c.add(-a, height, b))
        wall(c.add(-a, 0.0, b), c.add(a, height, b))
    }

    /** Adds to children [count] walls arranged in an oval inscribed within room edges.
     * @param count the number of vertices on the oval, has to be >= 3.
     */
    @Deprecated("Use PolyRoom")
    fun createRoundWalls(count: Int) {
        //TODO: check the round walls, they have protruding ends sticking out.
        if (count < 3) return
        val a = size.x / 2
        val b = size.z / 2
        val c = innerFloorCenter
        val angleStep = 360.0 / count.toDouble()
        // Going counterclockwise:
        var angle = -angleStep / 2
        while (angle < 360 - angleStep / 2) {
            wall(
                c.add(
                    a * MathUtil.cosDeg(angle),
                    0.0,
                    -b * MathUtil.sinDeg(angle)
                ),
                c.add(
                    a * MathUtil.cosDeg(angle + angleStep),
                    height,
                    -b * MathUtil.sinDeg(angle + angleStep)
                )
            )
            angle += angleStep
        }
    }

    override fun getGroundBoundaries(): List<GroundBoundary> {
        // add walls
        return super.getGroundBoundaries() +
            walls.map { it.bottomStart to it.bottomEnd }
    }


    /** legacy constructor */
    constructor(
        parent: Node?,
        position: Vec3,
        size: Vec3,
        rotationY: Double
    ) : this(position, size) {
        this.parent = parent
        this.rotationY = rotationY
    }

    constructor() : this(Vec3.ZERO, Vec3.ZERO)

    // LEGACY
    @Deprecated("use child node Floor")
    open var hasCeiling = false
        set(value) {
            field = value
            if (value) ceiling()
        }
    @Deprecated("use child node Floor")
    open var hasFloor = false
        set(value) {
            field = value
            if (value) floor()
        }
}
