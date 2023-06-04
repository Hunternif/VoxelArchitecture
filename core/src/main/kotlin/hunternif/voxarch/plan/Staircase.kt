package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

/**
 * A straight flight of stairs. Runs from low XY to high XY.
 * Ideally the size should be so that `height <= width`.
 *
 * ```
 * Y
 *  ^
 *  |       # (start + size)
 *  |     #
 *  |   #
 *  | (start)
 *  +--------------> X (East)
 */
class Staircase(origin: Vec3) : Slope(origin) {
    constructor() : this(Vec3.ZERO)
}