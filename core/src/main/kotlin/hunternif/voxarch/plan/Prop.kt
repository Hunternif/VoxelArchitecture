package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

/**
 * ```
 * Y
 *  +----> X (East)
 *  |
 *  V
 *  Z
 * ```
 * A fixed-size prop, i.e. a statue or a simple torch.
 * During planning it is effectively treated as a point.
 * @param origin will be copied
 */
class Prop(origin: Vec3) : Node(origin)