package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

/**
 * A horizontal surface that fills an entire room.
 *
 * Should only be added as a child to a [Room].
 *
 * The floor's origin is at parent's (0, [height], 0).
 */
open class Floor(val height: Double = 0.0): Node(Vec3(0.0, height, 0.0))