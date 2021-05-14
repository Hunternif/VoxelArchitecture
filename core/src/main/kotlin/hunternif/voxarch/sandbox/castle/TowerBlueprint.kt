package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.plan.*
import hunternif.voxarch.util.rotateY
import hunternif.voxarch.vector.Vec3
import kotlin.math.ceil

/**
 * Data about a single turret, in the context of the placement algorithm.
 */
data class TurretData(
    /** Angle in polar coordinates from the center or parent turret. */
    val angle: Double,
    /** Radius in polar coordinates from the center or parent turret. */
    val distance: Double,
    /** Rotation of the current turret. Usually it's the same as [angle] */
    val rotation: Double = angle,
    /** Y offset of the current turret from the roof of parent turret. */
    val baseline: Double = 0.0,
    /** Size of the current turret. */
    val size: Vec3,
    val roofShape: RoofShape,
    val bodyShape: BodyShape,
    val bottomShape: BottomShape
)

enum class RoofShape {
    /** Flat top surrounded by a border. */
    FLAT_BORDERED,
    /** Pyramid/cone roof with no border. */
    SPIRE,
    /** Pyramid/cone roof surrounded by a border. */
    SPIRE_BORDERED,
    /** Spherical dome. */
    //DOME
}
enum class BodyShape {
    /** Square or rectangle. */
    SQUARE,
    /** Round or oval. */
    ROUND
}
enum class BottomShape {
    /** Turrets sits directly on top of parent turret. */
    FLAT,
    /** Stretches down to the ground. */
    FOUNDATION,
    /** Tapers to a point. Could be partially hidden in parent turret's wall. */
    TAPERED
}

/**
 * Parameters that affect geometry, that should apply to all child turrets.
 */
data class TowerStyle(
    /** Offset for borders and spires in all child turrets. */
    val roofOffset: Int = 1,
    /** Y/X ratio of spires for all child turrets. */
    val spireRatio: Double = 1.25,
    /** Y/X ratio of tapered bottoms of turrets. */
    val turretTaperRatio: Double = 0.75
)

/**
 * Lay out a tower structure with multiple recursive turrets.
 */
fun towerWithTurrets(
    origin: Vec3,
    size: Vec3,
    roofShape: RoofShape,
    bodyShape: BodyShape,
    commonStyle: TowerStyle = TowerStyle(),
    maxRecursions: Int = 4,
    /** Random seed */
    seed: Long = 0,
    /** Function to place child turrets onto the given turret. */
    placeTurrets: TurretPlacer = ::placeNoTurrets
): Room {
    val turretData = TurretData(
        angle = 0.0,
        distance = 0.0,
        size = size,
        roofShape = roofShape,
        bodyShape = bodyShape,
        bottomShape = BottomShape.FOUNDATION
    )
    return recursiveTowerWithTurrets(
        origin, turretData, commonStyle, 0, maxRecursions, seed, placeTurrets)
}

private fun recursiveTowerWithTurrets(
    origin: Vec3,
    turretData: TurretData,
    style: TowerStyle,
    depth: Int,
    maxRecursions: Int,
    seed: Long,
    placeTurrets: TurretPlacer
): Room {
    val tower = turretData.run {
        tower(origin, size, roofShape, bodyShape, bottomShape, style)
    }
    if (depth < maxRecursions) {
        placeTurrets(turretData, seed).forEach {
            val pos = Vec3.UNIT_X
                .rotateY(it.angle)
                .multiplyLocal(it.distance)
                .addY(turretData.size.y + it.baseline)
            val childTurret = recursiveTowerWithTurrets(
                pos, it, style, depth + 1, maxRecursions, seed, placeTurrets)
            tower.addChild(childTurret)
            // TODO: if distance > parent size, place bridge
        }
    }
    return tower
}

/**
 * Returns a list of child turret placements.
 */
typealias TurretPlacer = (parent: TurretData, seed: Long) -> List<TurretData>

fun placeNoTurrets(parent: TurretData, seed: Long) = emptyList<TurretData>()

/** Place turrets symmetrically in 4 directions. */
fun place4Turrets(parent: TurretData, seed: Long): List<TurretData> {
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

fun tower(
    origin: Vec3,
    size: Vec3 = Vec3(6.0, 12.0, 6.0),
    roofShape: RoofShape,
    bodyShape: BodyShape,
    bottomShape: BottomShape = BottomShape.FOUNDATION,
    style: TowerStyle = TowerStyle()
): Room {
    val roofOrigin = Vec3(0.0, size.y + 1, 0.0)
    val roofSize = Vec3(
        size.x + style.roofOffset*2,
        0.0,
        size.z + style.roofOffset*2
    )

    val hasSpire = when (roofShape) {
        RoofShape.SPIRE -> true
        RoofShape.SPIRE_BORDERED -> true
        else -> false
    }
    val avgRadius = (size.x + size.z) / 4
    val spireHeight = (avgRadius + style.roofOffset) * style.spireRatio * 2
    val spireOrigin = roofOrigin.clone()
    val spireSize = roofSize.addY(spireHeight)

    val withCrenellation = when (roofShape) {
        RoofShape.FLAT_BORDERED -> true
        RoofShape.SPIRE_BORDERED -> true
        else -> false
    }

    val hasFoundation = bottomShape == BottomShape.FOUNDATION
    val hasTaperedBottom = bottomShape == BottomShape.TAPERED
    val taperedBottomHeight = avgRadius * style.turretTaperRatio * 2
    val taperedBottomSize = Vec3(size.x, taperedBottomHeight, size.z)

    return Room(origin, size).apply {
        if (hasFoundation) {
            floor { type = BLD_FOUNDATION }
        }
        if (hasTaperedBottom) {
            centeredRoom(
                innerFloorCenter.addY(-taperedBottomHeight),
                taperedBottomSize
            ) {
                createTowerWalls(bodyShape)
                walls.forEach { it.transparent = true }
                type = BLD_TURRET_BOTTOM
            }
        }
        floor()
        createTowerWalls(bodyShape)
        // TODO: place corbels as separate nodes
        type = BLD_TOWER_BODY

        // spire:
        if (hasSpire) {
            centeredRoom(spireOrigin, spireSize) {
                createTowerWalls(bodyShape)
                walls.forEach { it.transparent = true }
                type = BLD_TOWER_SPIRE
            }
        }

        // overhanging roof:
        if (withCrenellation) {
            centeredRoom(roofOrigin, roofSize) {
                ceiling()
                createTowerWalls(bodyShape)
                type = BLD_TOWER_ROOF
            }
        }
    }
}

private fun Room.createTowerWalls(bodyShape: BodyShape) {
    when (bodyShape) {
        BodyShape.SQUARE -> createFourWalls()
        BodyShape.ROUND -> {
            val sideCount = ceil((size.x + size.z) * 0.167).toInt() * 4
            createRoundWalls(sideCount)
        }
    }
}
