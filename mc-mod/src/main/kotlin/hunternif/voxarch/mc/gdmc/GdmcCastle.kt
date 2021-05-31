package hunternif.voxarch.mc.gdmc

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.plan.wall
import hunternif.voxarch.sandbox.castle.BLD_CURTAIN_WALL
import hunternif.voxarch.sandbox.castle.turret.addGrandCastleTurretsRecursive
import hunternif.voxarch.sandbox.castle.turret.randomBody
import hunternif.voxarch.sandbox.castle.turret.randomRoof
import hunternif.voxarch.sandbox.castle.turret.turret
import hunternif.voxarch.util.nextEvenInt
import hunternif.voxarch.vector.Box2D
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.vector.Vec3
import hunternif.voxarch.world.HeightMap
import hunternif.voxarch.world.Mountain
import hunternif.voxarch.world.spreadBoxesAlongPerimeter
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

const val towerWidth = 6
const val towerHeight = 8
const val wallSectionLength = 16

/** Wall is this much lower than the tower */
const val wallTowerOffset = 4

// Not used because I'm ok with walls running into ground
//const val wallMinHeight = 2

/** Puts 4 towers a bitoff the center of the map. */
internal fun defaultCastle(terrain: HeightMap, seed:Long): Node {
    val side = wallSectionLength + towerWidth
    val w = towerWidth
    // top left corner is such that the castle is close to center
    val topLeft = IntVec2(
        terrain.width/2 + w,
        terrain.length/2 + w
    )
    val boxes = topLeft.run { listOf(
        Box2D(add(0, 0), add(w, w)),
        Box2D(add(side, 0), add(side+w, w)),
        Box2D(add(side, side), add(side+w, side+w)),
        Box2D(add(0, side), add(w, side+w))
    )}
    return castleFromBoxes(boxes, terrain, seed)
}

internal fun mountainCastle(terrain: HeightMap, mnt: Mountain, seed:Long): Node {
    val boxes = mnt.top.spreadBoxesAlongPerimeter(
        IntVec2(towerWidth, towerWidth),
        (wallSectionLength + towerWidth).toDouble()
    )
    return castleFromBoxes(boxes, terrain, seed)
}

private fun castleFromBoxes(boxes: List<Box2D>, terrain: HeightMap, seed:Long): Node {
    // set origin at start of heightmap but at Y=0, because
    // all further points will have XZ relative to heightmap, but absolute Y.
    val castle = Structure(Vec3(terrain.start.x, 0, terrain.start.y))
    val towers = boxes.mapIndexed { i, box ->
        castle.towerFromBox(box, terrain, seed + 10000000*i)
    }
    //TODO: make sure towers are not placed lower than "height + minWallHeight"
    castle.buildWallsBetween(towers)
    return castle
}

private fun Node.towerFromBox(box: Box2D, terrain: HeightMap, seed: Long): Room {
    val ground = box.fold(0) { acc, p -> max(acc, terrain[p]) }
    val width = towerWidth + Random(seed+120).nextEvenInt(0, 7)
    val height = towerHeight + Random(seed+121).nextInt(0, 18)
    return turret(
        origin = Vec3(box.center.x, ground.toDouble(), box.center.y),
        size = Vec3(width, height, width),
        roofShape = Random(seed+122).randomRoof(),
        bodyShape = Random(seed+123).randomBody()
    ) {
        addGrandCastleTurretsRecursive(seed)
    }
}

private fun Node.buildWallsBetween(towers: List<Room>) {
    if (towers.isEmpty()) return
    for (i in 0 until towers.count()-1) {
        buildWallBetween(towers[i], towers[i+1])
    }
    buildWallBetween(towers.last(), towers[0])
}

private fun Node.buildWallBetween(t1: Room, t2: Room) {
    if (t1 == t2) return
    // make sure he wall isn't too tall for the lower tower,
    // or too short for the higher tower.
    val minBottom = min(t1.origin.y, t2.origin.y)
    val highestGround = max(t1.origin.y, t2.origin.y)
    val highestRoof = max(t1.origin.y + t1.height, t2.origin.y + t2.height)
    val lowestRoof = min(t1.origin.y + t1.height, t2.origin.y + t2.height)

    val wallTop = min(lowestRoof, highestRoof - wallTowerOffset)
    val p1 = t1.origin.clone().apply { y = minBottom }
    val p2 = t2.origin.clone().apply { y = wallTop }

    wall(p1, p2) {
        type = BLD_CURTAIN_WALL
    }
}