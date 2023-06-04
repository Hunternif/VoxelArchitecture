package hunternif.voxarch.plan

import hunternif.voxarch.vector.Plane
import hunternif.voxarch.vector.Vec3

/**
 * A sloped surface that runs from low XY to high XY.
 * Use cases:
 *  - space under gable roofs
 *  - staircases
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
open class Slope(origin: Vec3) : Node(origin) {
    constructor() : this(Vec3.ZERO)

    override fun getBoundaries(): List<Plane> {
        val walls = getGroundBoundaries()
        return ArrayList<Plane>(walls.size + 2).apply {
            addAll(walls)
            add(Plane(Vec3(0.0, start.y, 0.0), -Vec3.UNIT_Y)) // floor
            // sloped ceiling:
            add(Plane.from3Points(
                start,
                start.add(0.0, 0.0, depth),
                start + this@Slope.size
            ))
        }
    }
}