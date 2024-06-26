package hunternif.voxarch.sandbox.castle.turret

import hunternif.voxarch.sandbox.castle.turret.BodyShape.*
import hunternif.voxarch.sandbox.castle.turret.BottomShape.*
import hunternif.voxarch.sandbox.castle.turret.RoofShape.*
import hunternif.voxarch.util.*
import hunternif.voxarch.vector.Vec3
import kotlin.math.*
import kotlin.random.Random

private const val minWidth = 2.0
private const val maxWidth = 12.0
/** Walkways should we at least this wide. */
private const val minWalkingSpace = 2.0
/** Interior floors can be at least this tall. */
private const val minInteriorHeight = 4.0
/** If a turret is at least this wide, must continue building on top. */
private const val minPlazaWidth = 12.0
/** How far a tapered turret can project from parent turret. */
private const val maxOverhang = 4.0

fun createCastleFromTurrets(
    origin: Vec3,
    seed: Long = 0
): Turret {
    val size = Random(seed).run {
        val width = nextEvenInt(12, 37)
        val height = nextInt(8, 37)
        Vec3(width, height, width)
    }
    return createTurret(
        origin = origin,
        size = size,
        // start with flat so we can build on top
        roofShape = FLAT_BORDERED,
        bodyShape = Random(seed+2).randomBody(),
        bottomShape = FOUNDATION,
        style = TowerStyle(),
        level = 0
    ).apply {
        addTurretsRecursive(seed)
    }
}

private fun Turret.addTurretsRecursive(seed: Long) {
    if (hasSpace(5.0) && bottomShape == FOUNDATION) {
        expandWithTurrets(seed+4444444)
    }
    if (hasSpace(minWidth, minWalkingSpace) && roofShape == FLAT_BORDERED) {
        buildOnTop(seed+5555555)
    }
    decorateWithTurrets(seed+6666666)
    // TODO prevent the next level from doing the same thing as the previous level?
}

private fun Turret.expandWithTurrets(seed: Long) {
    if (Random(seed+200).nextDouble() < 0.2) {
        plaza(seed+20000)
    }
    if (Random(seed+201).nextDouble() < 0.2) {
        bridge(seed+20001)
    }
    val wallCount = Random(seed+202).nextInt(3)
    for (x in 1..wallCount) {
        thickWall(seed+202+x)
    }
}

private fun Turret.buildOnTop(seed: Long) {
    val topOptions = mutableListOf<BuildOption>().apply {
        option(1.0) { centerTower(seed+10101) }
        option(level) { /* do nothing */ }

    }.toTypedArray()
    Random(seed+10102).nextWeighted(*topOptions).build(this)
}

private fun Turret.decorateWithTurrets(seed: Long) {
    // These wall decorations are mutually exclusive
    val wallOptions = mutableListOf<BuildOption>().apply {
        if (hasSpace(minWalkingSpace * 4)) {
            option(1.0) { turretsInCorners(seed) }
        }
        if (bodyShape == SQUARE) {
            if (!hasSpace(minPlazaWidth) && height >= minInteriorHeight) {
                option(0.5) { singleTurret(ROUND, seed) }
                option(0.5) { singleTurret(SQUARE, seed) }
                option(0.0) { flyingButtress(seed) }
                option(0.5) { balcony(SQUARE, seed) }
                option(0.0) { perimeterBalcony(seed) }
            }
        } else if (bodyShape == ROUND) {
            if (!hasSpace(minPlazaWidth) && height >= minInteriorHeight) {
                option(1.0) { singleTurret(ROUND, seed) }
                option(0.5) { balcony(ROUND, seed) }
                option(0.0) { perimeterBalcony(seed) }
            }
        }
        if (width < minPlazaWidth) {
            option(1.0) { /* do nothing if it's small enough */ }
        }
    }.toTypedArray()
    Random(seed+100).nextWeighted(*wallOptions).build(this)
}

private class BuildOption(
    override val probability: Double,
    val build: Turret.() -> Unit
) : IRandomOption

private fun MutableList<BuildOption>.option(
    probability: Double,
    build: Turret.() -> Unit
) {
    add(BuildOption(probability, build))
}
private fun MutableList<BuildOption>.option(
    probability: Int,
    build: Turret.() -> Unit
) = option(probability.toDouble(), build)


// Horizontal expansions

private fun Turret.bridge(seed: Long) {
    // TODO: bridge
}

private fun Turret.plaza(seed: Long) {
    // TODO: plaza
}

private fun Turret.thickWall(seed: Long) {
    // TODO: thick wall
    // check if parent is a turret, then build within its bounds
}

//TODO: use thin walls only when space is limited, or on higher levels
private fun Turret.thinWall(seed: Long) {
    // TODO: thin wall
}


// Decorations
//TODO: massive turrets should sit lower and favour Foundation
//TODO: if the turret is too wide, don't build a spire


/** Attaches 1 tapered turret to the side. */
private fun Turret.singleTurret(shape: BodyShape, seed: Long) {
    val angle = Random(seed+1000)
        .nextInt(-200, 201) + this.turretAngle

    val height = round(this.height * 0.6)
    val width = (this.width * 0.5)
        .clamp(minWidth, maxWidth)
        .roundToEven() // even sizes are better for symmetry

    val yOffset = round(Random(seed+1001)
        .nextDoubleOrMax(width, this.height))

    // TODO: proper attachment to square walls
    val origin = Vec3.UNIT_X.rotateY(angle).also {
        it.y = yOffset
        it.x = round(it.x * this.size.x * 0.6)
        it.z = round(it.z * this.size.z * 0.6)
    }

    val roofShape = randomRoof(seed+1002)

    turret(
        origin = origin,
        size = Vec3(width, height, width),
        roofShape = roofShape,
        bodyShape = shape,
        bottomShape = TAPERED,
        positionType = TurretPosition.WALL,
        style = this.style,
        angle = angle,
        level = this.level + 1
    ) {
        addTurretsRecursive(seed + level)
    }
}

/** Puts a turret on top, in the center, extending parent turret. */
private fun Turret.centerTower(seed: Long) {
    val roofWidth = width + style.roofOffset * 2
    val width = (roofWidth * 0.75)
        .clamp(minWidth, roofWidth - minWalkingSpace * 2)
    //TODO: it can't be too tall & thin

    val heightRatio = Random(seed+1010)
        .nextDouble(0.2, 0.8)
    val height = round(this.height * heightRatio)

    val roofShape = randomRoof(seed+1002)

    turret(
        origin = Vec3(0.0, this.height + 1, 0.0),
        size = Vec3(width, height, width),
        roofShape = roofShape,
        bodyShape = this.bodyShape,
        bottomShape = FLAT,
        positionType = TurretPosition.TOP,
        style = this.style,
        angle = this.turretAngle,
        level = this.level + 1
    ) {
        addTurretsRecursive(seed + level)
    }
}

/** Puts a turret on top randomly, extending parent turret. */
private fun Turret.randomTowerOnTop(seed: Long) {
    val roofWidth = width + style.roofOffset * 2
    val width = (Random(seed+10000001)
        .nextDouble(0.2 * 0.75) * roofWidth)
        .clamp(4.0, roofWidth - minWalkingSpace * 2)
    //TODO: it can't be too tall & thin

    val heightRatio = Random(seed+10000002)
        .nextDouble(0.2, 0.8)
    val height = round(this.height * heightRatio)

    val x = round(Random(seed+10000003)
        .nextDouble(-0.5, 0.5) * (this.width - width))
    val z = round(Random(seed+10000004)
        .nextDouble(-0.5, 0.5) * (this.width - width))

    val roofShape = randomRoof(seed+10000005)
    val bodyShape = Random(seed+10000006).randomBody()

    turret(
        origin = Vec3(x, this.height + 1, z),
        size = Vec3(width, height, width),
        roofShape = roofShape,
        bodyShape = bodyShape,
        bottomShape = FLAT,
        positionType = TurretPosition.TOP,
        style = this.style,
        angle = this.turretAngle,
        level = this.level + 1
    ) {
        addTurretsRecursive(seed + level)
    }
}

/** Attaches a single balcony on one side. */
private fun Turret.balcony(shape: BodyShape, seed: Long) {
    val angle = Random(seed+1020)
        .nextInt(-200, 201) + this.turretAngle

    val width = (this.width * 0.5)
        .clamp(minWidth, maxWidth)
        .roundToEven() // even sizes are better for symmetry

    val yOffset = round(Random(seed+1021)
        .nextDoubleOrMax(width, this.height - minInteriorHeight))

    // TODO: proper attachment to square walls
    val origin = Vec3.UNIT_X.rotateY(angle).also {
        it.y = yOffset
        it.x = round(it.x * this.size.x * 0.6)
        it.z = round(it.z * this.size.z * 0.6)
    }
    // TODO: add entrance
    turret(
        origin = origin,
        size = Vec3(width, 0.0, width),
        roofShape = FLAT_BORDERED,
        bodyShape = shape,
        bottomShape = TAPERED,
        positionType = TurretPosition.WALL,
        style = this.style,
        angle = angle,
        level = this.level + 1
    )
}

/** Attaches a single balcony that goes around the turret and
 * loops back on itself. */
private fun Turret.perimeterBalcony(seed: Long) {
    // TODO: perimeter balcony
}

//TODO: favor not going above parent roof height
//TODO: if the turret is too wide and low, prevent building child turrets in the
// direction of the parent turret.
private fun Turret.turretsInCorners(seed: Long) {
    val height = round(this.height * 0.6)
    val widthRatio = Random(seed+1040)
        .nextDouble(0.25, 0.5)
    val width = (widthRatio * this.width)
        .clamp(1.0, maxWidth) // can be extra narrow
        .roundToEven() // even sizes are better for symmetry

    val yOffset = round(Random(seed+1041)
        .nextDoubleOrMax(width, this.height))

    val radius = when(bodyShape) {
        SQUARE -> sqrt(size.x*size.x + size.z*size.z)/2
        ROUND -> this.width * 0.55
    }

    // How far the child turret sit from parent origin
    val maxRadius = (this.width/2 + maxOverhang) - width/2

    val roofShape = randomRoof(seed+1042)
    val bodyShape = when (this.bodyShape) {
        SQUARE -> Random(seed+1043).randomBody()
        ROUND -> ROUND
    }
    val bottomShape = when(this.bottomShape) {
        FLAT, TAPERED -> TAPERED
        FOUNDATION -> {
            Random(seed+1044).nextWeighted(
                RandomOption(1.0, FOUNDATION),
                RandomOption(0.5 + level / 2.0, TAPERED)
            ).value
        }
    }

    for (angle in 45..(360-45) step 90) {
        val origin = Vec3.UNIT_X.rotateY(angle).also {
            it.y = yOffset
            it.x = round((it.x * radius).clamp(-maxRadius, maxRadius))
            it.z = round((it.z * radius).clamp(-maxRadius, maxRadius))
        }
        turret(
            origin = origin,
            size = Vec3(width, height, width),
            roofShape = roofShape,
            bodyShape = bodyShape,
            bottomShape = bottomShape,
            positionType = TurretPosition.WALL,
            style = this.style,
            angle = angle.toDouble(),
            level = this.level + 1
        ) {
            // TODO break the symmetry of turrets at the same level doing the same thing
            addTurretsRecursive(seed + 10000000 + angle*level)
        }
    }
}

private fun Turret.flyingButtress(seed: Long) {
    // TODO: flying buttress
}



fun Random.randomRoof(): RoofShape = nextWeighted(
    RandomOption(1.0, FLAT_BORDERED),
    RandomOption(0.5, SPIRE),
    RandomOption(0.5, SPIRE_BORDERED)
).value

/** Takes into account turret size, makes sure spires are not too tall. */
fun Turret.randomRoof(seed: Long): RoofShape {
    val avgRadius = (size.x + size.z) / 4
    val spireHeight = (avgRadius + style.roofOffset) * style.spireRatio * 2
    val flatProb = if (spireHeight < 20) 1.0 else spireHeight / 6
    return Random(seed).nextWeighted(
        RandomOption(flatProb, FLAT_BORDERED),
        RandomOption(0.5, SPIRE),
        RandomOption(0.5, SPIRE_BORDERED)
    ).value
}

fun Random.randomBody(): BodyShape = nextWeighted(
    RandomOption(1.0, SQUARE),
    RandomOption(1.0, ROUND)
).value

/** Checks if the roof fits [offset] + [space] + [offset] on both X & Z axes. */
private fun Turret.hasSpace(space: Double, offset: Double = 0.0): Boolean =
    width + style.roofOffset * 2 >= offset * 2 + space &&
    depth + style.roofOffset * 2 >= offset * 2 + space

/**
 * Calculate how much sq.b. area there is on top, minus the [offset] from sides.
 */
private fun Turret.roofArea(offset: Int = 0): Double =
    when (roofShape) {
        SPIRE, SPIRE_BORDERED -> 0.0
        FLAT_BORDERED -> {
            when (bodyShape) {
                SQUARE ->
                    (width + (style.roofOffset - offset) * 2) *
                    (height + (style.roofOffset - offset) * 2)
                ROUND ->
                    (width / 2 + (style.roofOffset - offset)) *
                    (height / 2 + (style.roofOffset - offset)) *
                    PI
            }
        }
    }