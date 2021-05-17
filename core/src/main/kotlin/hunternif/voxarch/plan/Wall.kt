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
    val bottomStart: Vec3 get() = origin
    val bottomEnd: Vec3 get() = Vec3(end.x, 0.0, end.z)
    var end: Vec3 = end.clone()

    override var rotationY: Double
        get() = atan2(-end.z + origin.z, end.x - origin.x) * 180 / Math.PI
        set(value) {}

    val p1: Vec2 get() = Vec2.fromXZ(origin)
    val p2: Vec2 get() = Vec2.fromXZ(end)

    val length: Double get() = p2.distanceTo(p1)
    val height: Double get() = end.y - origin.y


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
}
