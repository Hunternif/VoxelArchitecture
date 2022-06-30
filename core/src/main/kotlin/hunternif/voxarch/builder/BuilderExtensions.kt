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
 * Is samples in global coordinate space, applying [trans] to local coordinates.
 * Works correctly only for CONVEX rooms.
 * @param trans must rotate and translate (0, 0, 0) to room's origin.
 * @param buildAt the arguments (x, y, z) are global coordinates,
 *      i.e. relative to the storage, NOT to the room. Y is floor level.
 */
fun Room.fillXZ(
    trans: ITransformation,
    buildAt: (x: Int, y: Int, z: Int) -> Unit
) {
    val aabb = findIntAABB(trans)
    val boundaries = getGroundBoundaries()
        .map { trans.transform(it.first) to trans.transform(it.second) }
    aabb.forEachXZ { x, z ->
        val q = Vec3(x, aabb.minY, z)
        // Test if the point q is contained on the inside of each wall
        var inside = true
        for (b in boundaries) {
            val p1p2 = b.second - b.first
            val p1q = q - b.first
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

/**
 * Runs [buildAt] at every point on the line from [p1] to [p2].
 * Adapting Bresenham's line algorithm to 3D.
 */
fun line2(
    p1: Vec3,
    p2: Vec3,
    buildAt: (pos: IntVec3) -> Unit
) {
    // Find the smallest slope octant.
    // Use a linear transformation to orient the octant so that
    // slopes (dy/dx, dz/dx) are all within [0, 1].
    val trans = LinearTransformation().translate(p1)
    val end = p2 - p1
    if (end.x < 0) {
        trans.mirrorX()
        end.x *= -1
    }
    if (end.y < 0) {
        trans.mirrorY()
        end.y *= -1
    }
    if (end.z < 0) {
        trans.mirrorZ()
        end.z *= -1
    }
    if (end.y > end.x) {
        trans.mirrorY().rotateZ(-90.0)
        val temp = end.y
        end.y = end.x
        end.x = temp
    }
    if (end.z > end.x) {
        trans.mirrorX().rotateY(-90.0)
        val temp = end.z
        end.z = end.x
        end.x = temp
    }
    // Now slopes (dy/dx, dz/dx) are all within [0, 1]
    var x = 0.0
    var y = 0.0
    var z = 0.0
    var dy = 2 * end.y - end.x
    var dz = 2 * end.z - end.x
    while (x <= end.x) {
        val pos = trans.transform(x, y, z)
        buildAt(pos.toIntVec3())
        if (dy > 0) {
            y++
            dy -= 2 * end.x
        }
        if (dz > 0) {
            z++
            dz -= 2 * end.x
        }
        dy += 2 * end.y
        dz += 2 * end.z
        x++
    }
}
