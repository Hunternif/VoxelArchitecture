package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.util.rotateY
import hunternif.voxarch.vector.Vec3
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
class Placer4Turrets(seed: Long = 0) : TurretPlacer {
    private val minWidth = 2

    private val rand = Random(seed)

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
                bottomShape = BottomShape.TAPERED,
                positionType = TurretPosition.WALL
            ))
        }
        return result
    }
}
