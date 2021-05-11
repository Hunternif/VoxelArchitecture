package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

/**
 * ```
 * Y
 *  +------------> X (East)
 *  | start --+
 *  | |       |
 *  | +---- (start + size)
 *  V
 *  Z
 * ```
 * Horizontal surface. Potentially it can be uneven, so `end` can have higher Y.
 * @param start aka `origin`, will be copied
 * @param size
 */
open class Floor(
    start: Vec3,
    size: Vec3
): Room(start, size, Vec3.ZERO)