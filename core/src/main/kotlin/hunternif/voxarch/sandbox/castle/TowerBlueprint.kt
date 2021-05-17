package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.plan.*
import hunternif.voxarch.util.rotateY
import hunternif.voxarch.vector.Vec3
import kotlin.math.ceil

/**
 * Data about a single turret, in the context of the placement algorithm.
 */
data class TurretData(
    /** Position relative to origin of parent turret. */
    val origin: Vec3,
    /** Size of the current turret. */
    val size: Vec3,
    /** Rotation of the current turret. Usually facing away
     * from the center of parent turret. */
    val angle: Double,
    val roofShape: RoofShape,
    val bodyShape: BodyShape,
    val bottomShape: BottomShape,
    val positionType: TurretPosition
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
enum class TurretPosition {
    /** Sits on the edge of the wall of parent turret. */
    WALL,
    /** Sits away from parent turret with a bridge to parent's wall. */
    WALL_BRIDGE,
    /** Sits on the corner of 2 walls of _rectangular_ parent turret. */
    CORNER,
    /** Sits away from parent turret with a bridge to parent's corner. */
    CORNER_BRIDGE,
    /** Sits directly on the roof of parent turret. */
    TOP,
    /** No relation to parent turret. */
    NONE
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
    /** Function to place child turrets onto the given turret. */
    turretPlacer: TurretPlacer = PlacerNoTurrets
): Room {
    val turretData = TurretData(
        origin = origin,
        size = size,
        angle = 0.0,
        roofShape = roofShape,
        bodyShape = bodyShape,
        bottomShape = BottomShape.FOUNDATION,
        positionType = TurretPosition.NONE
    )
    return recursiveTowerWithTurrets(
        turretData, commonStyle, 0, maxRecursions, turretPlacer)
}

private fun recursiveTowerWithTurrets(
    turretData: TurretData,
    style: TowerStyle,
    depth: Int,
    maxRecursions: Int,
    turretPlacer: TurretPlacer
): Room {
    val tower = turretData.run {
        tower(origin, size, roofShape, bodyShape, bottomShape, style)
    }
    if (depth < maxRecursions) {
        turretPlacer.placeTurrets(turretData).forEach {
            val childTurret = recursiveTowerWithTurrets(
                it, style, depth + 1, maxRecursions, turretPlacer)
            tower.addChild(childTurret)
            // TODO: if distance > parent size, place bridge
        }
    }
    return tower
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
