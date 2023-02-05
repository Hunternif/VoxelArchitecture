package hunternif.voxarch.plan

import hunternif.voxarch.util.MathUtil.clampAngle
import hunternif.voxarch.util.rotateYLocal
import hunternif.voxarch.vector.*

/** Finds offset of this node's origin in global coordinates, i.e.
 * in the coordinate space where its highest parent node exists.
 */
//TODO: cache global position when nodes aren't moved
fun Node.findGlobalPosition(): Vec3 {
    var depth = 0
    val result = origin.clone()
    var parent = this.parent
    while (parent != null) {
        depth++
        if (depth > 10000) {
            println("Possibly infinite recursion!")
            return result
        }
        result.rotateYLocal(parent.rotationY)
        result.addLocal(parent.origin)
        parent = parent.parent
    }
    return result
}

/** Finds this node's rotation vs global coordinates. */
fun Node.findGlobalRotation(): Double {
    var depth = 0
    var result = rotationY
    var parent = this.parent
    while (parent != null) {
        depth++
        if (depth > 10000) {
            println("Possibly infinite recursion!")
            return result
        }
        result += parent.rotationY
        parent = parent.parent
    }
    return clampAngle(result)
}

/**
 * Finds the GLOBAL axis-aligned bounding box that contains this room's walls.
 * The returned result is in global coordinates.
 * [trans] must rotate and translate (0, 0, 0) to room's origin.
 */
fun Node.findAABB(trans: ITransformation): AABB {
    val aabb = AABB()
    val boundaries = getGroundBoundaries()
    for (b in boundaries) {
        aabb.union(trans.transform(b.first))
        aabb.union(trans.transform(b.second))
    }
    aabb.maxY = aabb.minY + height // we assume Y is always up
    aabb.correctBounds()
    return aabb
}

/**
 * See [findAABB], determines transformation by traversing parent tree.
 */
fun Node.findGlobalAABB(): AABB {
    val trans = LinearTransformation()
    trans.translate(findGlobalPosition())
    trans.rotateY(findGlobalRotation())
    return findAABB(trans)
}

/**
 * Finds AABB of this node relative to its parent, accounting for rotation.
 */
fun Node.findLocalAABB(): AABB {
    val trans = LinearTransformation()
    trans.translate(origin)
    trans.rotateY(rotationY)
    return findAABB(trans)
}

/** See [findAABB] */
fun Node.findIntAABB(trans: ITransformation): IntAABB = findAABB(trans).toIntAABB()


/** Finds children in the subtree matching the given class and tags. */
inline fun <reified N : Node> Node.query(vararg tags: String): Sequence<N> = sequence {
    val tagSet = tags.toSet()
    this@query.iterateSubtree().forEach {
        if (it is N && (tagSet.isEmpty() || it.tags.containsAll(tagSet))) {
            yield(it)
        }
    }
}

/** Convenience property that gets and sets low-XYZ point vs parent origin.
 * Accounts for rotation. Setting it moves origin. */
var Node.minPoint: Vec3
    get() = findLocalAABB().minVec
    set(value) { origin += value - minPoint }

/** Convenience property that gets and sets high-XYZ point vs parent origin.
 * Accounts for rotation. Setting it moves origin. */
var Node.maxPoint: Vec3
    get() = findLocalAABB().maxVec
    set(value) { origin += value - maxPoint }

/** See [minPoint] */
var Node.minX: Double
    get() = minPoint.x
    set(value) { origin.x += value - minX }

/** See [minPoint] */
var Node.minY: Double
    get() = minPoint.y
    set(value) { origin.y += value - minY }

/** See [minPoint] */
var Node.minZ: Double
    get() = minPoint.z
    set(value) { origin.z += value - minZ }

/** See [maxPoint] */
var Node.maxX: Double
    get() = maxPoint.x
    set(value) { origin.x += value - maxX }

/** See [maxPoint] */
var Node.maxY: Double
    get() = maxPoint.y
    set(value) { origin.y += value - maxY }

/** See [maxPoint] */
var Node.maxZ: Double
    get() = maxPoint.z
    set(value) { origin.z += value - maxZ }
