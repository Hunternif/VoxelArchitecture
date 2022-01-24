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

    /** Top far point relative to origin */
    private val innerEnd: Vec3 = end.subtract(start)
    /** Top far point relative to parent origin */
    val end: Vec3 get() = origin.add(innerEnd)

    override var rotationY: Double
        get() = atan2(-innerEnd.z, innerEnd.x) * 180 / Math.PI
        set(value) {}

    val p1: Vec2 get() = Vec2.fromXZ(origin)
    val p2: Vec2 get() = Vec2.fromXZ(end)

    var length: Double
        get() = bottomEnd.distanceTo(bottomStart)
        set(value) {
            if (length == 0.0) {
                innerEnd.x = length
            } else {
                val ratio = value / length
                innerEnd.x *= ratio
                innerEnd.z *= ratio
            }
        }
    var height: Double
        get() = innerEnd.y
        set(value) { innerEnd.y = value }
}
