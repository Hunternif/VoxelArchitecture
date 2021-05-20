package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.plan.*
import hunternif.voxarch.sandbox.castle.turret.*
import hunternif.voxarch.vector.Vec3
import kotlin.math.ceil

// DSL
fun Node.tower(
    origin: Vec3,
    size: Vec3 = Vec3(6.0, 12.0, 6.0),
    roofShape: RoofShape,
    bodyShape: BodyShape,
    bottomShape: BottomShape = BottomShape.FOUNDATION,
    style: TowerStyle = TowerStyle(),
    action: Turret.() -> Unit = {}
): Turret = createTower(
    origin, size, roofShape, bodyShape, bottomShape, style
).also {
    this.addChild(it)
    action.invoke(it)
}

fun createTower(
    origin: Vec3,
    size: Vec3 = Vec3(6.0, 12.0, 6.0),
    roofShape: RoofShape,
    bodyShape: BodyShape,
    bottomShape: BottomShape = BottomShape.FOUNDATION,
    style: TowerStyle = TowerStyle()
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

    return Turret(origin, size).apply {
        this.roofShape = roofShape
        this.bodyShape = bodyShape
        this.bottomShape = bottomShape
        this.style = style

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
