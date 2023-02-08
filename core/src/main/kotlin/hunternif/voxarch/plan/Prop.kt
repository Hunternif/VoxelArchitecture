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
 * @param position will be copied
 */
open class Prop(position: Vec3, type: String) : Node(position) {
    init {
        this.tags += type
    }

    constructor(type: String): this(Vec3.ZERO, type)
}