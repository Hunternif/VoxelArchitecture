package hunternif.voxarch.vector

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Represents a 3D plane.
 * [a], [b], [c], [d] are factors in the plane equation `a*x + b*y + c*z + d = 0`.
 */
class Plane(
    var a: Double,
    var b: Double,
    var c: Double,
    var d: Double,
) {
    val normal: Vec3 = Vec3(1, 0, 0)
        get() = field.set(a, b, c).normalizeLocal()

    constructor(point: Vec3, normal: Vec3) :
        this(normal.x, normal.y, normal.z, -point.dotProduct(normal))

    /**
     * Returns true if [p] is in the "inside" half-space produced by this plane.
     * [normal] points to the "outside" direction.
     */
    fun isInside(p: Vec3): Boolean = distance(p) > 0

    /** Returns true if [p] sits on the plane, within error [margin]. */
    fun isOnPlane(p: Vec3, margin: Double = 0.00001): Boolean =
        abs(distance(p)) < margin

    /** Signed distance from [p] to this plane. */
    fun distance(p: Vec3): Double {
        val n = sqrt(a * a + b * b + c * c)
        return (a * p.x + b * p.y + c * p.z + d) / n
    }

    companion object {
        fun from3Points(p1: Vec3, p2: Vec3, p3: Vec3): Plane {
            // calculate normal, assuming CCW winding
            val normal = (p2 - p1).crossProduct(p3 - p1).normalizeLocal()
            return Plane(p1, normal)
        }

        fun vertical(p1: Vec3, p2: Vec3): Plane =
            from3Points(p1, p2, p2 - Vec3.UNIT_Y)

        fun horizontal(p: Vec3): Plane = Plane(p, Vec3.UNIT_Y)
        fun horizontal(y: Double): Plane = Plane(Vec3(0.0, y, 0.0), Vec3.UNIT_Y)
    }
}