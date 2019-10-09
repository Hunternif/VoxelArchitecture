package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec2
import hunternif.voxarch.vector.Vec3

/**
 * ```
 *  Y
 *  /\
 *  |           outside
 *  |  origin == gate ==
 *  |           inside
 *  +---------------------> X
 * Z
 * ```
 * A vertical hatch between two [Room]s.
 * The gate's generator should clear the passage and may add decorations.
 * @param origin the lower-left point, will be copied
 * @param size X=width, Y=length (Z)
 */
class Hatch(
    origin: Vec3,
    var size: Vec2
) : Node(origin) {

    // legacy constructor
    constructor(
        parent: Node?,
        room1: Room,
        room2: Room,
        origin: Vec3,
        size: Vec2,
        rotationY: Double
    ): this(
        origin,
        size
    ) {
        this.parent = parent
        this.rotationY = rotationY
    }
}
