package hunternif.voxarch.vector

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Represents a 3D plane.
 * [a], [b], [c], [d] are factors in the plane equation `a*x + b*y + c*z + d = 0`.
 */
open class Plane(
    var a: Double,
    var b: Double,
    var c: Double,
    var d: Double,
) {
    val normal: Vec3 = Vec3(1, 0, 0)
        get() = field.set(a, b, c).normalizeLocal()

    constructor(point: Vec3, normal: Vec3) :
        this(normal.x, normal.y, normal.z, -point.dotProduct(normal))

    constructor() : this(1.0, 1.0, 1.0, 0.0)

    /** Sets values in this plane to be equal to [other] */
    fun set(other: Plane) {
        this.a = other.a
        this.b = other.b
        this.c = other.c
        this.d = other.d
    }

    /**
     * Returns true if [p] is in the "inside" half-space produced by this plane.
     * [normal] points to the "outside" direction.
     */
    fun isInside(p: Vec3): Boolean = isInside(p.x, p.y, p.z)

    /** See [isInside] */
    fun isInside(x: Number, y: Number, z: Number): Boolean = distance(x, y, z) <= 0.0

    /** Returns true if [p] sits on the plane, within error [margin]. */
    fun isOnPlane(p: Vec3, margin: Double = 0.00001): Boolean =
        abs(distance(p)) < margin

    /**
     * Signed distance from [p] to this plane.
     * Distance is positive, if the point is on the "outside", i.e. on the side
     * where [normal] is pointing.
     */
    fun distance(p: Vec3): Double = distance(p.x, p.y, p.z)

    /** Signed distance from the given point to this plane. */
    fun distance(x: Number, y: Number, z: Number): Double {
        val n = sqrt(a * a + b * b + c * c)
        if (n == 0.0) {
            // degenerate case, will use distance to a point instead:
            return findPoint().distanceTo(x.toDouble(), y.toDouble(), z.toDouble())
        }
        return (a * x.toDouble() + b * y.toDouble() + c * z.toDouble() + d) / n
    }

    /** Finds a point on the plane */
    fun findPoint(): Vec3 {
        val p = Vec3(
            if (a == 0.0) 0.0 else 1 / a,
            if (b == 0.0) 0.0 else 1 / b,
            if (c == 0.0) 0.0 else 1 / c,
        )
        val dot = a * p.x + b * p.y + c * p.z
        if (dot != 0.0) p.multiplyLocal(-d / dot)
        return p
    }

    /**
     * Moves the plane by vector [delta]. I.e. if it passed through point `A`,
     * now it will pass through point `A + delta`.
     * Returns a new plane.
     */
    open fun move(delta: Vec3): Plane = transform(LinearTransformation().translate(delta))

    /** Applies the given transformation to this plane and returns a new plane. */
    open fun transform(trans: ILinearTransformation): Plane {
        // transform 2 points: point P1 on the plane, and point N1 = P1 + normal
        val p1 = findPoint()
        val n1 = p1 + normal
        val p2 = trans.transform(p1)
        val n2 = trans.transform(n1)
        return Plane(p2, n2 - p2)
    }

    companion object {
        /** Normal points according to the right-hand rule */
        fun from3Points(p1: Vec3, p2: Vec3, p3: Vec3): Plane {
            // calculate normal, assuming CCW winding
            val normal = (p2 - p1).crossProduct(p3 - p1).normalizeLocal()
            return Plane(p1, normal)
        }

        /** Normal points to the right, when going from [p1] to [p2] */
        fun vertical(p1: Vec3, p2: Vec3): Plane =
            from3Points(p1, p2, p2 + Vec3.UNIT_Y)

        /** Normal points up */
        fun horizontal(p: Vec3): Plane = Plane(p, Vec3.UNIT_Y)

        /** Normal points up */
        fun horizontal(y: Double): Plane = Plane(Vec3(0.0, y, 0.0), Vec3.UNIT_Y)
    }
}