package hunternif.voxarch.util

import hunternif.voxarch.vector.Matrix4
import hunternif.voxarch.vector.Vec3
import hunternif.voxarch.vector.Vec4
import kotlin.math.round
import kotlin.random.Random

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

/**
 * Gets the next random *even* Int from the given [range].
 */
fun Random.nextEvenInt(range: IntRange): Int {
    return nextEvenInt(range.first, range.last + 1)
}

/**
 * Gets the next random *even* Int between [from] (inclusive)
 * and [until] exclusive.
 */
fun Random.nextEvenInt(from: Int, until: Int): Int {
    val startPadding = if (from % 2 == 0) 0 else 1
    val start = from / 2 + startPadding
    val end = until / 2
    // +1 because [until] it's exclusive
    return this.nextInt(start, end + 1) * 2
}

fun <T> Random.next(items: Array<T>): T = items[nextInt(items.size)]

fun <T> Random.next(items: List<T>): T = items[nextInt(items.size)]

fun Double.clamp(min: Double, max: Double): Double = when {
    this < min -> min
    this > max -> max
    else -> this
}

fun Double.roundToEven() = round(this / 2)*2