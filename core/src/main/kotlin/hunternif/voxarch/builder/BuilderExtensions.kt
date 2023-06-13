package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.findIntAABB
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.storage.SymmetricStorageX
import hunternif.voxarch.storage.TransformedBlockStorage
import hunternif.voxarch.util.forEachXZ
import hunternif.voxarch.util.round
import hunternif.voxarch.vector.*

/**
 * Returns transformed storage, which accepts "local" coordinates
 * and transforms it to "global" coordinates.
 */
fun IBlockStorage.toLocal(trans: ILinearTransformation) =
    TransformedBlockStorage(this, trans)

/**
 * Returns symmetric storage that sets blocks in 2 directions on the X axis.
 */
fun IBlockStorage.asSymmetricX(midPoint: Number) =
    SymmetricStorageX(this, midPoint)

/**
 * Runs [buildAt] at every (x, y, z) point inside the node's boundary,
 * at floor Y level.
 * Is samples in global coordinate space, applying [trans] to local coordinates.
 * Works correctly only for CONVEX rooms.
 * @param trans must rotate and translate (0, 0, 0) to room's origin.
 * @param buildAt the arguments (x, y, z) are global coordinates,
 *      i.e. relative to the storage, NOT to the room. Y is floor level.
 */
fun Node.fillXZ(
    trans: ILinearTransformation,
    buildAt: (x: Int, y: Int, z: Int) -> Unit
) {
    val aabb = findIntAABB(trans)
    val boundaries = getGroundBoundaries().map { it.transform(trans) }
    aabb.forEachXZ { x, z ->
        val q = Vec3(x, aabb.minY, z)
        // Test if the point q is contained on the inside of each wall
        var inside = true
        for (b in boundaries) {
            if (b.length < 0.00001) continue // 0-size boundaries are useless
            if (!b.isInside(q)) {
                inside = false
                break
            }
        }
        if (inside) buildAt(x, aabb.minY, z)
    }
}

/**
 * Same as [fillXZ], but the arguments (x, y, z) are in node's local space.
 *
 * See [fillXZ] about parameters.
 */
fun Node.fillXZLocal(
    trans: ILinearTransformation,
    buildAt: (x: Double, y: Double, z: Double) -> Unit
) {
    val inverse = trans.inverse()
    val vec = Vec3(0, 0, 0)
    fillXZ(trans) { x, y, z ->
        inverse.transformLocal(vec.set(x, y, z))
        buildAt(vec.x, vec.y, vec.z)
    }
}

/**
 * Runs [buildAt] at every (x, y, z) point inside the node's boundary.
 *
 * See [fillXZ] about parameters.
 */
fun Node.fillXYZ(
    trans: ILinearTransformation,
    buildAt: (x: Int, y: Int, z: Int) -> Unit
) {
    fillXZ(trans) { x, y, z ->
        for (dy in 0..height.toInt()) {
            buildAt(x, y + dy, z)
        }
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
 * Runs [buildAt] at every point on the line from [p1] to [p2].
 * Adapting Bresenham's line algorithm to 3D.
 */
fun line2(p1: Vec3, p2: Vec3, buildAt: (pos: Vec3) -> Unit) {
    val p1round = p1.round()
    val p2round = p2.round()
    // Find the smallest slope octant.
    // Use a linear transformation to orient the octant so that
    // slopes (dy/dx, dz/dx) are all within [0, 1].
    val trans = LinearTransformation().translate(p1round)
    val end = p2round - p1round
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
        buildAt(pos)
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
