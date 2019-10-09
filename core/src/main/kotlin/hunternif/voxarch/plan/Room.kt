package hunternif.voxarch.plan

import hunternif.voxarch.util.Box
import hunternif.voxarch.util.MathUtil
import hunternif.voxarch.vector.Vec2
import hunternif.voxarch.vector.Vec3

/**
 * ```
 * Y
 *  +-------> X (East)
 *  | xz1--+
 *  | |    |
 *  | +--xz2
 *  V
 *  Z
 * ```
 * @param origin somewhere at floor level, will be copied
 * @param xz1 corner of the room relative to _its origin_, with lower XZ values, will be copied
 * @param xz2 corner of the room relative to _its origin_, with higher XZ values, will be copied
 * @param height
 */
open class Room(
    origin: Vec3,
    xz1: Vec2,
    xz2: Vec2,
    var height: Double
) : Node(origin) {
    var xz1: Vec2 = xz1.clone()
    var xz2: Vec2 = xz2.clone()
    open var hasCeiling = true
    open var hasFloor = true

    init {
        require(xz2.x >= xz1.x) { "Room points must be in order of increasing X" }
        require(xz2.y >= xz1.y) { "Room points must be in order of increasing Z" }
    }

    var width: Double
        get() = xz2.x - xz1.x
        set(value) { // set vs center. might want to change this
            floorCenter.apply {
                xz1.x = x - value/2
                xz2.x = x + value/2
            }
        }
    var length: Double
        get() = xz2.y - xz1.y
        set(value) { // set vs center. might want to change this
            floorCenter.apply {
                xz1.y = y - value/2
                xz2.y = y + value/2
            }
        }
    /** Vector (width, height, length), doesn't take rotation into account.
     * Components of this vector are equal to the distance between the corners
     * of the room. It would take that number + 1 blocks to build each boundary
     * of the room in a world. */
    //TODO: make room size count the actual number of blocks
    val size get() = Vec3(width, height, length)
    val floorCenter get() = origin.add(xz1.x, 0.0, xz1.y).addLocal(width/2, 0.0, length/2)
    /** relative to the parent's origin*/
    val boundingBox get() = Box(floorCenter, size)

    val rooms get() = children.filterIsInstance<Room>()
    val walls get() = children.filterIsInstance<Wall>()
    val props get() = children.filterIsInstance<Prop>()
    val gates get() = children.filterIsInstance<Gate>()
    val hatches get() = children.filterIsInstance<Hatch>()

    /**
     * Add to children 4 walls matching room edges
     */
    fun createFourWalls() {
        val a = size.x/2
        val b = size.z/2
        /*
		 * (Wall indices)
		 * +---------> X
		 * | xz1 a
		 * |  +- 1 -+
		 * |  |     |
		 * |  2     0 b
		 * |  |     |
		 * |  +- 3 -+
		 * V        xz2
		 * Z
		 */
        // Going counterclockwise:
        addChild(Wall(Vec3(a, 0.0, b), Vec3(a, height, -b)))
        addChild(Wall(Vec3(a, 0.0, -b), Vec3(-a, height, -b)))
        addChild(Wall(Vec3(-a, 0.0, -b), Vec3(-a, height, b)))
        addChild(Wall(Vec3(-a, 0.0, b), Vec3(a, height, b)))
    }

    /** Adds to children [count] walls arranged in an oval inscribed within room edges.
     * @param count the number of vertices on the oval, has to be >= 3.
     */
    fun createRoundWalls(count: Int) {
        //TODO: check the round walls, they have protruding ends sticking out.
        if (count < 3) return
        val a = size.x / 2
        val b = size.z / 2
        val angleStep = 360.0 / count.toDouble()
        // Going counterclockwise:
        var angle = -angleStep / 2
        while (angle < 360 - angleStep / 2) {
            addChild(Wall(
                Vec3(
                    a * MathUtil.cosDeg(angle),
                    0.0,
                    -b * MathUtil.sinDeg(angle)
                ),
                Vec3(
                    a * MathUtil.cosDeg(angle + angleStep),
                    height,
                    -b * MathUtil.sinDeg(angle + angleStep)
                )
            ))
            angle += angleStep
        }
    }


    // legacy constructor
    constructor(
        parent: Node?,
        origin: Vec3,
        size: Vec3,
        rotationY: Double
    ) : this(
        origin,
        Vec2(-size.x/2, -size.z/2),
        Vec2(size.x/2, size.z/2),
        size.y
    ) {
        this.parent = parent
        this.rotationY = rotationY
    }

    // legacy constructor
    constructor(origin: Vec3, size: Vec3): this(null, origin, size, 0.0)
}
