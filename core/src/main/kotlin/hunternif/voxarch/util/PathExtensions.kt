package hunternif.voxarch.util

import hunternif.voxarch.plan.Path
import hunternif.voxarch.vector.Vec3
import kotlin.math.round

/** Adds points on a rectangle, centered at origin */
fun Path.rectangle(length: Double, width: Double) {
    val a = length / 2
    val b = width / 2
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

/** Adds [count] points on an ellipse, centered at origin.
 * Works best with even count. */
fun Path.ellipse(length: Double, width: Double, count: Int) {
    if (count < 3) return
    val a = length / 2
    val b = width / 2
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
/** Adds [count] points on a circle, centered at origin.
 * Works best with even count. */
fun Path.circle(width: Double, count: Int) = ellipse(width, width, count)