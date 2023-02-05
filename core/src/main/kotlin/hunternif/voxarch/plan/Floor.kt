package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

/**
 * A horizontal surface that fills an entire room.
 *
 * Should only be added as a child to a [Room].
 *
 * The floor's origin is at parent's (0, [y], 0).
 */
open class Floor(
    y: Double = 0.0
) : Node(Vec3(0.0, y, 0.0)) {
    override val localCenter: Vec3
        get() = (parent?.let {
            // centered at parent
            it.localCenter.clone().apply {
                y = origin.y
            } - origin
        } ?: Vec3.ZERO) + start

    // Return parent's size
    override var size: Vec3
        get() {
            parent?.let {
                super.size.set(it.size)
                super.size.y = 0.0
            }
            return super.size
        }
        set(value) { super.size = value }
}