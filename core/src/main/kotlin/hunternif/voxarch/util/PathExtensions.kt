package hunternif.voxarch.util

import hunternif.voxarch.plan.Path
import hunternif.voxarch.vector.Vec3
import kotlin.math.round

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

/** Adds [count] points on an ellipse, centered at origin.
 * Works best with even count. */
fun Path.ellipse(width: Double, length: Double, count: Int) {
    if (count < 3) return
    val a = width / 2
    val b = length / 2
    val angleStep = 360.0 / count
    // Add half a step to maximize polygon area
    val radiusX = a / MathUtil.cosDeg(angleStep / 2)
    val radiusZ = b / a * radiusX
    // Going counterclockwise:
    var angle = -angleStep / 2
    while (angle < 360 - angleStep / 2) {
        addPoint(
            Vec3(
                round(radiusX * MathUtil.cosDeg(angle)),
                0.0,
                round(-radiusZ * MathUtil.sinDeg(angle))
            )
        )
        angle += angleStep
    }
    loopToStart()
}
/** Adds [count] points on a circle, centered at origin.
 * Works best with even count. */
fun Path.circle(width: Double, count: Int) = ellipse(width, width, count)