package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

/**
 * Container node intended for complete structures.
 */
open class Structure(origin: Vec3 = Vec3.ZERO) : Node(origin) {
    constructor(): this(Vec3.ZERO)
}
