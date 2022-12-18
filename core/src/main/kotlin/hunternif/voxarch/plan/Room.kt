package hunternif.voxarch.plan

import hunternif.voxarch.util.Box
import hunternif.voxarch.util.MathUtil
import hunternif.voxarch.vector.Vec3
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

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
 * @param origin coordinates of nodes inside this Node are counted
 *            from this origin.
 * @param size size in centric coordinates,
 * see [wiki](https://github.com/Hunternif/VoxelArchitecture/wiki/Definitions).
 */
open class Room(
    origin: Vec3,
    size: Vec3
) : Node(origin) {

    private val startDelegate = CenteredStartDelegate()
    /**
     * Internal offset of the low-XZ corner of the room.
     * By default it's set so that origin is at the center of the floor.
     */
    var start: Vec3 by startDelegate
    fun isCentered() = startDelegate.innerValue == null
    fun setCentered(value: Boolean) {
        startDelegate.innerValue = if (value) null else start
    }

    override var width: Double
        get() = size.x
        set(value) { size.x = value }
    override var height: Double
        get() = size.y
        set(value) { size.y = value }
    override var length: Double
        get() = size.z
        set(value) { size.z = value }
    /** Vector (width, height, length), doesn't take rotation into account.
     * Components of this vector are equal to distances between corner blocks
     * of the room. It would take that number + 1 blocks to build each boundary
     * of the room in a world. */
    override var size: Vec3 = size.clone()
        set(value) { field.set(value) } // keep the same instance

    /** Vs local origin */
    val innerFloorCenter: Vec3 get() = start.add(size.x/2, 0.0, size.z/2)
    /** Vs parent's origin */
    val floorCenter: Vec3 get() = origin.add(innerFloorCenter)

    /** Relative to the parent's origin. Doesn't take into account rotation! */
    val boundingBox: Box get() = Box.fromCorners(origin.add(start), origin.add(start).add(size))

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


    /** legacy constructor */
    constructor(
        parent: Node?,
        origin: Vec3,
        size: Vec3,
        rotationY: Double
    ) : this(origin, size) {
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

    companion object {
        private class CenteredStartDelegate: ReadWriteProperty<Room, Vec3> {
            var innerValue: Vec3? = null
            override fun getValue(thisRef: Room, property: KProperty<*>): Vec3 =
                innerValue ?: Vec3(-thisRef.width / 2, 0.0, -thisRef.length / 2)

            override fun setValue(thisRef: Room, property: KProperty<*>, value: Vec3) {
                this.innerValue = value
            }

        }
    }
}
