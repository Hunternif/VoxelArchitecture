package hunternif.voxarch.plan

import hunternif.voxarch.util.Box
import hunternif.voxarch.util.MathUtil
import hunternif.voxarch.vector.Vec3

/**
 * ```
 * Y
 *  +------------> X (East)
 *  | start ---+
 *  | | origin |
 *  | +----- end (roof level)
 *  V
 *  Z
 * ```
 * @param origin anywhere, will be copied
 * @param start corner of the room relative to _parent's origin_, with lower XZ values, will be copied
 * @param end corner of the room relative to _parent's origin_, with higher XZ values, will be copied
 */
open class Room(
    origin: Vec3,
    start: Vec3,
    end: Vec3
) : Node(origin) {
    var start: Vec3 = start.clone()
    var end: Vec3 = end.clone()
    open var hasCeiling = true
    open var hasFloor = true

    init {
        require(end.x >= start.x) { "Room corners must be in order of increasing X" }
        require(end.y >= start.y) { "Room corners must be in order of increasing Y" }
        require(end.z >= start.z) { "Room corners must be in order of increasing Z" }
    }

    /** when modified, origin becomes the center of the floor */
    var width: Double
        get() = end.x - start.x
        set(value) {
            start.x = origin.x - value/2
            end.x = origin.x + value/2
        }
    val height: Double get() = end.y - start.y
    val length: Double get() = end.z - start.z
    /** Vector (width, height, length), doesn't take rotation into account.
     * Components of this vector are equal to the distance between the corners
     * of the room. It would take that number + 1 blocks to build each boundary
     * of the room in a world. */
    //TODO: make room size count the actual number of blocks
    val size get() = Vec3(width, height, length)
    /** relative to the parent's origin */
    val boundingBox get() = Box.fromCorners(start, end)

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
		 * | start
		 * |   +- 1 -+
		 * |   |     |
		 * |   2     0 b
		 * |   |     |
		 * |   +- 3 -+
		 * V      a  end
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


    /** legacy constructor */
    constructor(
        parent: Node?,
        origin: Vec3,
        size: Vec3,
        rotationY: Double
    ) : this(
        origin,
        origin.add(-size.x/2, 0.0, -size.z/2),
        origin.add(size.x/2, size.y, size.z/2)
    ) {
        this.parent = parent
        this.rotationY = rotationY
    }

    /** Origin is set in the center of the floor */
    constructor(origin: Vec3, size: Vec3): this(null, origin, size, 0.0)
}
