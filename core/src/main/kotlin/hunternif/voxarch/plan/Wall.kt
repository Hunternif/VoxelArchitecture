package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec2
import hunternif.voxarch.vector.Vec3
import kotlin.math.atan2

/**
 * ```
 * Y
 *  +----------------------> X (East)
 *  |         inside
 *  | start == wall == end
 *  |  (p1)   outside   (p2)
 *  |(origin)
 *  V
 *  Z
 * ```
 * [rotationY] is read-only and is determined by `origin` and `end`.
 * @param start relative to _parent's origin_, Y = floor level, will be copied.
 * @param end relative to _parent's origin_, Y = wall height, will be copied.
 */
open class Wall(
    start: Vec3,
    end: Vec3,
    /** If true, the wall will not be generated, only used as boundary for calculations. */
    var transparent: Boolean = false
) : Node(start) {
    /** Start point on the ground, vs parent origin. */
    val bottomStart: Vec3 get() = origin
    /** End point on the ground, vs parent origin. */
    val bottomEnd: Vec3 get() = Vec3(end.x, origin.y, end.z)

    /** Top far point relative to local origin */
    val innerEnd: Vec3 = end.subtract(start)
    /** Top far point relative to parent origin.
     * It's read-only, so that the entire wall can be moved by moving origin.
     * To move the end, change [innerEnd]. */
    val end: Vec3 get() = origin.add(innerEnd)

    /** Read-only, determined from start and end. */
    override var rotationY: Double
        get() = atan2(-innerEnd.z, innerEnd.x) * 180 / Math.PI
        set(_) {}

    /** XZ of start point, vs parent origin. */
    val p1: Vec2 get() = Vec2.fromXZ(origin)
    /** XZ of end point, vs parent origin. */
    val p2: Vec2 get() = Vec2.fromXZ(end)

    override var size: Vec3 = Vec3(0, 0, 0)
        get() = field.set(length, height, 0.0)
        set(value) {
            length = value.x
            height = value.y
        }
    override var length: Double
        get() = bottomEnd.distanceTo(bottomStart)
        set(value) {
            if (length == 0.0) {
                innerEnd.x = value
            } else {
                val ratio = value / length
                innerEnd.x *= ratio
                innerEnd.z *= ratio
            }
        }
    override var height: Double
        get() = innerEnd.y
        set(value) { innerEnd.y = value }


    /** Legacy constructor */
    constructor(room: Room, p1: Vec2, p2: Vec2, transparent: Boolean):
    this(
        Vec3(p1.x, 0.0, p1.y),
        Vec3(p2.x, room.height, p2.y),
        transparent
    ) {
        this.parent = room
    }
    /** Legacy constructor */
    constructor(room: Room, p1: Vec2, p2: Vec2): this(room, p1, p2, false)

    constructor(): this(Vec3.ZERO, Vec3.ZERO)
}
