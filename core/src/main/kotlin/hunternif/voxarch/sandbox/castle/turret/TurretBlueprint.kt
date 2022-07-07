package hunternif.voxarch.sandbox.castle.turret

import hunternif.voxarch.plan.*
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.util.circle
import hunternif.voxarch.util.square
import hunternif.voxarch.vector.Vec3
import kotlin.math.ceil

// DSL
fun Node.turret(
    origin: Vec3,
    size: Vec3,
    roofShape: RoofShape,
    bodyShape: BodyShape,
    bottomShape: BottomShape = BottomShape.FOUNDATION,
    positionType: TurretPosition = TurretPosition.NONE,
    style: TowerStyle = TowerStyle(),
    angle: Double = 0.0,
    level: Int = 1,
    action: Turret.() -> Unit = {}
): Turret = createTurret(
    origin = origin,
    size = size,
    roofShape = roofShape,
    bodyShape = bodyShape,
    bottomShape = bottomShape,
    positionType = positionType,
    style = style,
    angle = angle,
    level = level
).also {
    this.addChild(it)
    action.invoke(it)
}

fun createTurret(
    origin: Vec3,
    size: Vec3 = Vec3(6.0, 12.0, 6.0),
    roofShape: RoofShape,
    bodyShape: BodyShape,
    bottomShape: BottomShape = BottomShape.FOUNDATION,
    positionType: TurretPosition = TurretPosition.NONE,
    style: TowerStyle = TowerStyle(),
    angle: Double = 0.0,
    level: Int = 0
): Turret {
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

    return Turret(
        origin = origin,
        size = size,
        roofShape = roofShape,
        bodyShape = bodyShape,
        bottomShape = bottomShape,
        positionType = positionType,
        style = style,
        turretAngle = angle,
        level = level
    ).apply {
        if (hasFoundation) {
            floor { type = BLD_FOUNDATION }
        }
        if (hasTaperedBottom) {
            centeredPolygonRoom(
                innerFloorCenter.addY(-taperedBottomHeight),
                taperedBottomSize
            ) {
                createPolygon(bodyShape)
                type = BLD_TURRET_BOTTOM
            }
        }
        type = BLD_TOWER_BODY
        floor()
        createPolygon(bodyShape)
        createWalls()

        // corbels
        walls.forEach {
            it.path(size.y) {
                type = BLD_TOWER_CORBEL
            }
        }
        // TODO: place corbels as separate nodes

        // spire:
        if (hasSpire) {
            centeredPolygonRoom(spireOrigin, spireSize) {
                createPolygon(bodyShape)
                type = BLD_TOWER_SPIRE
            }
        }

        // overhanging roof:
        if (withCrenellation) {
            centeredPolygonRoom(roofOrigin, roofSize) {
                type = BLD_TOWER_ROOF
                ceiling()
                createPolygon(bodyShape)
                createWalls()
            }
        }
    }
}

private fun PolygonRoom.createPolygon(bodyShape: BodyShape) {
    when (bodyShape) {
        BodyShape.SQUARE -> polygon.square(width)
        BodyShape.ROUND -> polygon.circle(width)
    }
}