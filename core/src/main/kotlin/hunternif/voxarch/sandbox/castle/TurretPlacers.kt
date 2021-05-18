package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.util.*
import hunternif.voxarch.vector.Vec3
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.round
import kotlin.random.Random

/**
 * Returns a list of child turret placements.
 */
interface TurretPlacer {
    fun placeTurrets(parent: TurretData): List<TurretData>
}

/**
 * No-op, places no child turrets.
 */
object PlacerNoTurrets : TurretPlacer {
    override fun placeTurrets(parent: TurretData): List<TurretData> =
        emptyList()
}

/**
 * Place turrets symmetrically in 4 directions.
 */
class Placer4Turrets() : TurretPlacer {
    private val minWidth = 2

    override fun placeTurrets(parent: TurretData): List<TurretData> {
        if (parent.size.x < minWidth) return emptyList()
        val result = mutableListOf<TurretData>()
        for (angle in 0..270 step 90) {
            val size = parent.size.multiply(0.333)
            val origin = Vec3.UNIT_X.rotateY(angle).apply {
                y = parent.size.y - size.y / 2
                x *= (parent.size.x / 2 + 1)
                z *= (parent.size.z / 2 + 1)
            }
            result.add(TurretData(
                origin = origin,
                angle = angle.toDouble(),
                size = size,
                roofShape = parent.roofShape,
                bodyShape = parent.bodyShape,
                bottomShape = BottomShape.FOUNDATION,
                positionType = TurretPosition.WALL,
                depth = parent.depth + 1
            ))
        }
        return result
    }
}

/**
 * Copied from zivbot's
 * [Grand Castle Generator](https://www.thingiverse.com/thing:1682427).
 */
class GrandCastleTurretPlacer(
    /** How many sub-elements, values 0.5 - 10*/
    private val dense: Double = 5.0,
    /** How tall and narrow the turrets, values 0.05 - 1*/
    private val tall: Double = 0.5,
    private val seed: Long = 0
) : TurretPlacer {
    private val maxDepth = ceil(dense/3).toInt()
    private val minWidth = 2.0
    private val maxWidth = 12.0
    private val heightRatio = 0.4 + (1 - tall)*0.4
    private val widthRatio = 0.2 + (dense/10)*0.6

    override fun placeTurrets(parent: TurretData): List<TurretData> {
        val depth = parent.depth
        if (depth > maxDepth) return emptyList()
        val result = mutableListOf<TurretData>()

        val parentWidth = min(parent.size.x, parent.size.z)
        val turretCount = ceil(dense/3).toInt() - depth + 1

        for (t in 1 .. turretCount) {
            val bodyShape = Random(seed + depth)
                .next(BodyShape.values())
            val roofShape = Random(seed + depth)
                .next(RoofShape.values())

            // Don't rotate the turret itself (square looks bad at an angle),
            // but take into account orientation of the parent.
            val angle = Random(seed + t*10 + depth)
                .nextInt(-200, 201) + parent.angle

            val height = round(parent.size.y * heightRatio)
            val width = (parentWidth * widthRatio)
                .clamp(minWidth, maxWidth)
                .roundToEven() // even sizes are better for symmetry

            val yOffset = ceil(
                Random(seed + t + depth)
                .nextDouble(width, parent.size.y)
            )

            val origin = Vec3.UNIT_X.rotateY(angle).apply {
                y = yOffset
                x *= (parent.size.x * 0.55)
                z *= (parent.size.z * 0.55)
            }

            result.add(TurretData(
                origin = origin,
                angle = angle,
                size = Vec3(width, height, width),
                roofShape = roofShape,
                bodyShape = bodyShape,
                bottomShape = BottomShape.TAPERED,
                positionType = TurretPosition.WALL,
                depth = depth + 1
            ))
        }
        return result
    }
}
