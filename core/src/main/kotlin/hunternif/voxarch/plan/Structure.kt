package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

/**
 * Container node intended for complete structures.
 */
open class Structure(position: Vec3 = Vec3.ZERO) : Node(position) {
    constructor(): this(Vec3.ZERO)
}
