package hunternif.voxarch.util

import hunternif.voxarch.plan.Path
import hunternif.voxarch.util.MathUtil.roundUp
import hunternif.voxarch.vector.Vec3
import kotlin.math.PI

/** Adds points on a rectangle, centered at origin */
fun Path.rectangle(width: Double, length: Double) {
    val a = width / 2
    val b = length / 2
    /*
     * (Wall indices)
     * +---------> X
     * | start
     * |   +- 1 -+
     * |   |     |
     * |   2  c  0 b
     * |   |     |
     * |   +- 3 -+
     * V      a
     * Z
     */
    // Going counterclockwise:
    addPoint(Vec3(a, 0.0, b))
    addPoint(Vec3(a, 0.0, -b))
    addPoint(Vec3(-a, 0.0, -b))
    addPoint(Vec3(-a, 0.0, b))
    loopToStart()
}

/** Adds points on a square, centered at origin */
fun Path.square(width: Double) = rectangle(width, width)

/** Adds [count] points on a regular polygon, centered at origin.
 * Works best with even count. */
fun Path.polygon(width: Double, length: Double, count: Int) {
    if (count < 3) return
    val a = width / 2
    val b = length / 2
    val angleStep = 360.0 / count
    // Going counterclockwise:
    var angle = angleStep / 2
    while (angle < 360) {
        addPoint(
            Vec3(
                a * MathUtil.cosDeg(angle),
                0.0,
                -b * MathUtil.sinDeg(angle)
            )
        )
        angle += angleStep
    }
    loopToStart()
}

/** Adds points on an ellipse, centered at origin.
 * Adds many points to ensure the circle is symmetric */
fun Path.ellipse(width: Double, length: Double, segmentLength: Double = 4.0) {
    // For symmetry, calculate points on a circle,
    // then squish them into an ellipse at the end.
    val r = width / 2
    val zFrac = length / width
    // Decide one quadrant, and then copy it 4 times.
    val segmentLengthUpdated = kotlin.math.max(1.0, segmentLength)
    val countInQuadrant =  kotlin.math.max(2,
        roundUp(PI / 2 * r / segmentLengthUpdated)
    )

    // If the number of points in quadrant is odd, there is one point on the diagonal
    val hasDiagonal = countInQuadrant % 2 == 1
    val diagonalPoint = Vec3(
        r * MathUtil.cosDeg(45.0),
        0.0,
        -r * MathUtil.cosDeg(45.0)
    )

    // Go CCW, add points to octant (1/8 of a circle):
    /*
     * |
     * |  3
     * |   2
     * +---1-> X
     * |
     * V
     * Z
     */
    val angleStep = 90.0 / countInQuadrant
    var angle = angleStep / 2
    val pointsInOctant = mutableListOf<Vec3>()
    while (angle < 45.0) {
        pointsInOctant.add(
            Vec3(
                r * MathUtil.cosDeg(angle),
                0.0,
                -r * MathUtil.sinDeg(angle)
            )
        )
        angle += angleStep
    }

    // Complete a quadrant from 2 octants, including the diagonal:
    val pointsInQuadrant = mutableListOf<Vec3>().apply {
        addAll(pointsInOctant)
        if (hasDiagonal) add(diagonalPoint)
        pointsInOctant.reversed().forEach { add(Vec3(-it.z, it.y, -it.x)) }
    }

    // Complete a circle from 4 quadrants:
    pointsInQuadrant.forEach { addPoint(it.x, it.y, it.z * zFrac) }
    pointsInQuadrant.forEach { addPoint(it.z, it.y, -it.x * zFrac) }
    pointsInQuadrant.forEach { addPoint(-it.x, it.y, -it.z * zFrac) }
    pointsInQuadrant.forEach { addPoint(-it.z, it.y, it.x * zFrac) }
    loopToStart()
}

/** Adds points on a circle, centered at origin. */
fun Path.circle(width: Double, segmentLength: Double = 4.0) = ellipse(width, width, segmentLength)