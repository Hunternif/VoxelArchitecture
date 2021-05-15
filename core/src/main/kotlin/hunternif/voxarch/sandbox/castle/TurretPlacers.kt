package hunternif.voxarch.sandbox.castle

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
    private val rand = Random(seed)

    override fun placeTurrets(parent: TurretData): List<TurretData> {
        if (parent.size.x < 2) return emptyList()
        val result = mutableListOf<TurretData>()
        for (angle in 0..270 step 90) {
            val size = parent.size.multiply(0.333)
            result.add(TurretData(
                angle = angle.toDouble(),
                //TODO: allow asymmetric parent shape
                distance = parent.size.x / 2 + 1,
                size = size,
                baseline = -size.y / 2,
                roofShape = parent.roofShape,
                bodyShape = parent.bodyShape,
                bottomShape = BottomShape.TAPERED
            ))
        }
        return result
    }
}
