package hunternif.voxarch.plan

import hunternif.voxarch.util.MathUtil
import hunternif.voxarch.util.rotateY
import hunternif.voxarch.util.snapTo
import hunternif.voxarch.vector.Vec2
import hunternif.voxarch.vector.Vec3

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
 *
 * Internally the state is represented via rotation + width + height.
 * The rest is computed!
 *
 * @param start relative to _parent's origin_, Y = floor level, will be copied.
 * @param end relative to _parent's origin_, Y = wall height, will be copied.
 */
open class Wall(
    start: Vec3,
    end: Vec3,
    /** If true, the wall will not be generated, only used as boundary for calculations. */
    var transparent: Boolean = false
) : Node(start) {

    final override var rotationY: Double
        get() = super.rotationY
        set(value) {
            super.rotationY = value
                .snapTo(0, 90, -90, 180, -180, delta = 0.1)
                .snapTo(45, 135, -45, -135, delta = 0.01)
            direction.set(Vec3.UNIT_X.rotateY(rotationY))
        }

    /** Helper vector that points from origin in the direction of rotation. */
    protected val direction: Vec3 = Vec3(0, 0, 0)

    init {
        //TODO move this to an extra constructor
        width = origin.toXZ().distanceTo(end.toXZ())
        height = end.y - origin.y
        rotationY = MathUtil.atan2Deg(-end.z + origin.z, end.x - origin.x)
    }

    /** Start point on the ground, vs parent origin. Read-only. */
    val bottomStart: Vec3 get() = origin

    /** End point on the ground, vs parent origin. Read-only. */
    val bottomEnd: Vec3 get() = origin + direction * width

    /** Top far point relative to local origin. Read-only. */
    val innerEnd: Vec3 get() = direction * width + Vec3.UNIT_Y * height

    /** Top far point relative to parent origin. Read-only. */
    val end: Vec3 get() = origin + innerEnd

    /** XZ of start point, vs parent origin. Read-only. */
    val p1: Vec2 get() = Vec2.fromXZ(origin)

    /** XZ of end point, vs parent origin. Read-only. */
    val p2: Vec2 get() = Vec2.fromXZ(end)

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
    constructor(room: Room, p1: Vec2, p2: Vec2) : this(room, p1, p2, false)

    constructor() : this(Vec3.ZERO, Vec3.ZERO)
}
