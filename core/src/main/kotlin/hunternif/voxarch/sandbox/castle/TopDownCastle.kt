package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.wall
import hunternif.voxarch.sandbox.castle.turret.*
import hunternif.voxarch.sandbox.castle.turret.BodyShape.*
import hunternif.voxarch.sandbox.castle.turret.BottomShape.*
import hunternif.voxarch.util.*
import hunternif.voxarch.vector.Vec3
import kotlin.math.PI
import kotlin.math.round
import kotlin.random.Random

private const val minWidth = 2.0
private const val maxWidth = 12.0
/** Walkways should we at least this wide. */
private const val minWalkSpace = 2.0
/** Interior floors can be at least this tall. */
private const val minInteriorHeight = 4.0
/** Wall is this much lower than the tower */
private const val wallTowerOffset = 4.0

/**
 * Start with a small turret on top, and recursively add more structures below.
 */
fun createCastleTopDown(
    origin: Vec3,
    seed: Long = 0
): Node {
    val size = Random(seed).run {
        val width = nextEvenInt(4, 8)
        val height = nextInt(8, 20)
        Vec3(width, height, width)
    }
    val topTurret = createTurret(
        origin = origin,
        size = size,
        roofShape = Random(seed + 2).randomRoof(),
        bodyShape = Random(seed + 2).randomBody(),
        bottomShape = FOUNDATION,
        style = TowerStyle(),
        level = 4
    )
    return createParentRecursive(topTurret, seed)
}

private fun createParentRecursive(turret: Turret, seed: Long): Node {
    val parent = createParent(turret, seed)
    if (parent.level == 0) return parent
    return createParentRecursive(parent, seed + 1000000000)
}

private fun createParent(turret: Turret, seed: Long): Turret {
    val options = mutableListOf<BuildOption>().apply {
        option(1.0) { innerWard(it, seed) }
        option(1.0) { outerWard(it, seed) }
    }.toTypedArray()

    return Random(seed + 100100100)
        .nextWeighted(*options)
        .build(turret)
}

/** Returns a fake turret that contains an "inner ward" surrounded by walls,
 * and [keep] acts as a keep sitting on one of the walls. */
private fun innerWard(keep: Turret, seed: Long): Turret {
    val ward = outerWard(keep, seed)
    // The keep sits in the middle of a random wall
    val keepWall = ward.walls.random(Random(seed + 1010))
    keep.origin = keepWall.run {
        Vec3(round((p1.x + p2.x)/2), keep.origin.y, round((p1.y + p2.y)/2))
    }
    return ward
}

/** Returns a fake turret that contains an "outer ward" surrounded by walls,
 * and [keep] acts as a keep sitting in the center. */
private fun outerWard(keep: Turret, seed: Long): Turret {
    val wardWidthInc = Random(seed + 1002)
        .nextDouble(minWalkSpace * 2, 25.0)
    val wardWidth = round(keep.width + wardWidthInc).roundToEven()

    var wardShape = if (wardWidth >= minWidth * 3) {
        Random(seed + 1001).randomBody()
    } else SQUARE

    val turretWidthRatio = Random(seed + 1003).nextDouble(0.1, 0.8)
    val turretWidth = (keep.width * turretWidthRatio)
        .clamp(minWidth, maxWidth)
        .roundToEven()

    val keepElevation = Random(seed + 1009).nextDouble(4.0, 20.0)

    val turretHeightRatio = Random(seed + 1004).nextDouble(0.5, 0.9)
    val turretHeight = round((keep.height + keepElevation) * turretHeightRatio)
        .clampMin(wallTowerOffset)

    val wallHeight = round(turretHeight - wallTowerOffset).clampMin(3.0)

    val turretCount = when (wardShape) {
        SQUARE -> 4
        // assuming wall section length should equal to turret width * 1.5
        ROUND -> (wardWidth * PI / (turretWidth + keep.style.roofOffset * 2) / 2.5)
            .clampMin(4.0).roundToEven().toInt()
    }
    if (turretCount == 4) wardShape = SQUARE

    val turretRoofShape = Random(seed + 1005).randomRoof()
    val turretBodyShape = Random(seed + 1006).randomBody()
    val turretBottomShape = if (keep.level > 1) {
        Random(seed + 1007).next(TAPERED, FOUNDATION)
    } else FOUNDATION

    val angleStep = 360.0 / turretCount
    val radius = wardWidth / 2 / MathUtil.cosDeg(angleStep / 2)

    return Turret(
        origin = keep.origin,
        size = Vec3(wardWidth, wallHeight, wardWidth),
        bodyShape = wardShape,
        style = keep.style,
        level = keep.level - 1
    ).apply {
        val turrets = mutableListOf<Turret>()
        var angle = angleStep / 2
        while (angle < 360.0) {
            val origin = Vec3.UNIT_X.rotateY(angle).also {
                it.x = round(it.x * radius)
                it.z = round(it.z * radius)
            }
            turrets.add(turret(
                origin = origin,
                size = Vec3(turretWidth, turretHeight, turretWidth),
                roofShape = turretRoofShape,
                bodyShape = turretBodyShape,
                bottomShape = turretBottomShape,
                style = keep.style,
                level = keep.level
            ))
            angle += angleStep
        }

        // Create a loop
        turrets.add(turrets.first())
        turrets.zipWithNext { a, b ->
            wall(a.origin, b.origin.addY(wallHeight)) {
                type = BLD_CURTAIN_WALL
            }
        }

        // Place keep in the center
        addChild(keep, Vec3.ZERO.addY(keepElevation))
        keep.positionType = TurretPosition.WALL
    }
}

private enum class WallCurtainShape {
    /** axis-aligned walls, 4 towers in corners */
    SQUARE,
    /** rectangular sections project in each axis direction */
//    CROSS,
    /** N towers spread evenly in a circle */
    POLYGON,
    /** N towers spread evenly in a circle,
     * and then moved by random distance from center */
//    JITTERY_POLYGON
}

private class BuildOption(
    override val probability: Double,
    val build: (Turret) -> Turret
) : IRandomOption

private fun MutableList<BuildOption>.option(
    probability: Double,
    build: (Turret) -> Turret
) {
    add(BuildOption(probability, build))
}