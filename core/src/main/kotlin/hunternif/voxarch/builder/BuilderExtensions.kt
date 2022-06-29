package hunternif.voxarch.builder

import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.storage.TransformedBlockStorage
import hunternif.voxarch.util.forEachXZ
import hunternif.voxarch.vector.*

/**
 * Returns transformed storage, which accepts "local" coordinates
 * and transforms it to "global" coordinates.
 */
fun IBlockStorage.toLocal(trans: ILinearTransformation) =
    TransformedBlockStorage(this, trans)

/**
 * Runs the function [buildAt] at every (x, y, z) point inside the room's walls,
 * at floor Y level.
 * @param trans must rotate and translate (0, 0, 0) to room's origin.
 * @param buildAt the arguments (x, y, z) are global coordinates,
 *      i.e. relative to the storage, NOT to the room. Y is floor level.
 */
fun Room.fillXZ(
    trans: TransformationStack,
    buildAt: (x: Int, y: Int, z: Int) -> Unit
) {
    val aabb = findIntAABB(trans)
    val boundaries = getGroundBoundaries()
    aabb.forEachXZ { x, z ->
        val q = Vec3(x, aabb.minY, z)
        // Test if the point q is contained on the inside of each wall
        var inside = true
        for (b in boundaries) {
            val p1 = trans.transform(b.first)
            val p2 = trans.transform(b.second)
            val p1p2 = p2 - p1
            val p1q = q - p1
            val cross = p1p2.crossProduct(p1q)
            if (cross.y < 0) {
                inside = false
                break
            }
        }
        if (inside) buildAt(x, aabb.minY, z)
    }
}

fun line(
    p1: Vec3,
    p2: Vec3,
    step: Double = 1.0,
    startOffset: Double = 0.0,
    endOffset: Double = 0.0,
    build: (pos: Vec3) -> Unit
) {
    val lineVec = p2.subtract(p1).normalizeLocal()
    val stepVec = lineVec.multiply(step)
    val length = p2.distanceTo(p1)
    val pos = p1.add(lineVec.multiply(startOffset))
    var x = startOffset
    while (x <= length - endOffset) {
        build(pos)
        pos.addLocal(stepVec)
        x += step
    }
}

/**
 * Finds the GLOBAL axis-aligned bounding box that contains this room's walls.
 * The returned result is in global coordinates.
 * [trans] must rotate and translate (0, 0, 0) to room's origin.
 */
fun Room.findAABB(trans: ITransformation): AABB {
    val aabb = AABB()
    val boundaries = getGroundBoundaries()
    for (b in boundaries) {
        aabb.union(trans.transform(b.first))
        aabb.union(trans.transform(b.second))
    }
    aabb.maxY += height // we assume Y is always up
    aabb.correctBounds()
    return aabb
}

/** See [findAABB] */
fun Room.findIntAABB(trans: ITransformation): IntAABB = findAABB(trans).toIntAABB()


typealias RoomGroundBoundary = Pair<Vec3, Vec3>

/**
 * If the room has walls, returns wall vectors at floor level.
 * Otherwise, returns boundaries defined by room size.
 */
fun Room.getGroundBoundaries(): List<RoomGroundBoundary> {
    return if (walls.isNotEmpty()) {
        walls.map { it.bottomStart to it.bottomEnd }
    } else {
        // No walls, use the room size:
        listOf(
            start,
            start.addZ(size.z),
            start.add(size.x, 0.0, size.z),
            start.addX(size.x),
            start
        ).zipWithNext()
    }
}