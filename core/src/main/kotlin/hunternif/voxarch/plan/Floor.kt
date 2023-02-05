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

    override var start: Vec3
        get() = parent?.let {
            val corner = it.start - origin
            corner.y = 0.0
            corner
        } ?: Vec3(0, 0, 0)
        set(value) {}

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

    override fun getGroundBoundaries(): List<GroundBoundary> {
        val originXZ = origin.clone().apply { y = 0.0 }
        return parent?.getGroundBoundaries()?.map {
            it.first - originXZ to it.second - originXZ
        } ?: super.getGroundBoundaries()
    }
}