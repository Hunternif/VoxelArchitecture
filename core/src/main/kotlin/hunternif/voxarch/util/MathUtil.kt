package hunternif.voxarch.util

import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.vector.Matrix4
import hunternif.voxarch.vector.Vec3
import hunternif.voxarch.vector.Vec4
import kotlin.math.*

/**
 * Create a left-right-symmetric spacing of integers between 0 and [length].
 *
 * [minSpace] and [maxSpace] define how close the integers can be to each other.
 *
 * This is useful for building decorations, e.g. crenellations on a castle wall.
 *
 * ```
 *
 * Example:
 *   symmetricSpacing(9, 1, 2, true)
 *
 * Both results are valid, but the 2nd is preferred as the most even:
 * 0  3 5  8
 * *--*-*--*
 *
 * 0 2 4 5 8
 * *-*-*-*-*
 * ```
 *
 */
fun symmetricSpacing(
    length: Int,
    minSpace: Int,
    maxSpace: Int
): List<Int> {
    //TODO: implement symmetric spacing correctly
    assert(minSpace <= maxSpace)
    var i = -minSpace
    var j = length - 1 + minSpace
    var space = j - i
    val result = mutableListOf<Int>()

    // Naive implementation will use minSpace

    while (space >= minSpace * 3) {
        val step = minSpace
        i += step
        j -= step
        space = j - i
        result.add(i)
        result.add(j)
    }
    if (space >= minSpace * 2 && space % 2 == 0) {
        result.add(length / 2)
    }
    return result.sorted()
}

/**
 * Returns a new [Vec3] rotated by [angle] degrees.
 */
fun Vec3.rotateY(angle: Int): Vec3 = rotateY(angle.toDouble())

/**
 * Returns a new [Vec3] rotated by [angle] degrees.
 */
fun Vec3.rotateY(angle: Double): Vec3 {
    val vec4 = Vec4(x, y, z, 1.0)
    Matrix4.rotationY(angle).multiplyLocal(vec4)
    return Vec3.from(vec4)
}

fun Vec3.round() = Vec3(x.roundToInt(), y.roundToInt(), z.roundToInt())
fun Vec3.roundLocal(): Vec3 = set(x.roundToInt(), y.roundToInt(), z.roundToInt())
fun Vec3.roundToInt() = IntVec3(x.roundToInt(), y.roundToInt(), z.roundToInt())
fun max(a: Vec3, b: Vec3) = Vec3(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z))
fun min(a: Vec3, b: Vec3) = Vec3(min(a.x, b.x), min(a.y, b.y), min(a.z, b.z))

/** Angle in degrees of a line segment [start]-[end] vs X axis. */
fun segmentAngleY(start: Vec3, end: Vec3) =
    atan2(-end.z + start.z, end.x - start.x) * 180 / Math.PI

/**
 * Ensures the result is between [min] (incl.) and [max] (incl.).
 * In case when max < min, returns [max].
 */
fun Double.clamp(min: Double, max: Double): Double {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}
fun Double.clamp(min: Int, max: Int): Double =
    clamp(min.toDouble(), max.toDouble())
fun Double.clampMin(min: Double): Double = max(min, this)
fun Float.clamp(min: Float, max: Float): Float {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}
fun Int.clamp(min: Int, max: Int): Int {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}

fun Double.round(): Double = this.roundToInt().toDouble()
fun Double.roundToEven(): Double = round(this / 2) * 2
fun Int.roundToEven(): Int = (this / 2) * 2