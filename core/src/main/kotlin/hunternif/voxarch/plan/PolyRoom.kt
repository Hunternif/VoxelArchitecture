package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

/**
 * A room shaped as a cylinder with a polygon at its base.
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
     * Its origin must be in the center of the [PolyRoom].
     */
    val polygon: Path = Path(Vec3.ZERO)

    /**
     * For styling: approximate expected length of edges for ROUND shape.
     * If < 1, edge length is calculated dynamically.
     * [sideCount] takes precedence over [edgeLength].
     */
    var edgeLength: Double = 0.0

    /**
     * Side count for ROUND shape.
     * If < 3, side count is calculated dynamically.
     * [sideCount] takes precedence over [edgeLength].
     */
    var sideCount: Int = 0

    /** For styling. Mimics Turret's body shape. */
    var shape: PolyShape = PolyShape.SQUARE

    /** Creates a wall from each edge of the polygon. */
    fun createWalls() {
        polygon.origin = innerFloorCenter
        polygon.segments.forEach {
            wall(
                polygon.origin + it.p1,
                polygon.origin + it.p2.addY(height)
            )
        }
    }

    override fun getGroundBoundaries(): List<GroundBoundary> {
        return if (polygon.segments.isNotEmpty()) {
            polygon.segments.map { polygon.origin + it.p1 to polygon.origin + it.p2 }
        } else {
            super.getGroundBoundaries()
        }
    }

    constructor() : this(Vec3.ZERO, Vec3.ZERO)

}

enum class PolyShape {
    /** axis-aligned walls, 4 towers in corners */
    SQUARE,

    /** rectangular sections project in each axis direction */
//    CROSS,

    /** Walls spread evenly in a circle, edge length is dynamically calculated. */
    ROUND,

    /** 8 sides */
    OCTAGON,

    /** N towers spread evenly in a circle,
     * and then moved by random distance from center */
//    JITTERY_POLYGON
}