package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3
import kotlin.math.max

/**
 * A room shaped as a cylinder with an polygon at its base.
 * It's good for building round turrets or irregularly shaped enclosures.
 * The polygon could be convex or concave.
 *
 * Rationale:
 * a Room is a node that benefits from having width, length and height.
 * Some rooms are just rectangular boxes.
 * But some have this cylindrical quality, e.g. castle yard or turret.
 */
open class PolyRoom(origin: Vec3, size: Vec3) : Room(origin, size) {
    /**
     * Describes the perimeter on the XZ plane.
     * It's made of 3d points for convenience, but their Y should be 0.
     */
    val polygon: Path = Path(Vec3.ZERO)

    /** For styling: approximate expected length of edges on the the polygon. */
    var edgeLength: Double = 1.0
        set(value) { field = max(1.0, value) }
    /** For styling. Mimics Turret's body shape. */
    var shape: PolyShape = PolyShape.SQUARE

    /** Creates a wall from each edge of the polygon. */
    fun createWalls() {
        polygon.origin = innerFloorCenter
        polygon.segments.forEach {
            wall(it.p1, it.p2.addY(height))
        }
    }

    constructor() : this(Vec3.ZERO, Vec3.ZERO)

}

enum class PolyShape {
    /** axis-aligned walls, 4 towers in corners */
    SQUARE,
    /** rectangular sections project in each axis direction */
//    CROSS,
    /** N towers spread evenly in a circle */
    ROUND,
    /** N towers spread evenly in a circle,
     * and then moved by random distance from center */
//    JITTERY_POLYGON
}