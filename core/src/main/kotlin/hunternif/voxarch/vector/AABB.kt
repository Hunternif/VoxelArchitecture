package hunternif.voxarch.vector

import kotlin.math.max
import kotlin.math.min

/** Axis-aligned bounding box in 3D. */
data class AABB(
    var minX: Double = Double.MAX_VALUE,
    var minY: Double = Double.MAX_VALUE,
    var minZ: Double = Double.MAX_VALUE,
    var maxX: Double = Double.MIN_VALUE,
    var maxY: Double = Double.MIN_VALUE,
    var maxZ: Double = Double.MIN_VALUE,
) {

    fun setMin(x: Double, y: Double, z: Double) {
        minX = x
        minY = y
        minZ = z
    }
    fun setMin(min: Vec3) {
        minX = min.x
        minY = min.y
        minZ = min.z
    }
    fun setMax(x: Double, y: Double, z: Double) {
        maxX = x
        maxY = y
        maxZ = z
    }
    fun setMax(max: Vec3) {
        maxX = max.x
        maxY = max.y
        maxZ = max.z
    }

    fun correctBounds() {
        minX = min(minX, maxX)
        minY = min(minY, maxY)
        minZ = min(minZ, maxZ)
        maxX = max(minX, maxX)
        maxY = max(minY, maxY)
        maxZ = max(minZ, maxZ)
    }

    fun union(x: Double, y: Double, z: Double) {
        minX = min(minX, x)
        minY = min(minY, y)
        minZ = min(minZ, z)
        maxX = max(x, maxX)
        maxY = max(y, maxY)
        maxZ = max(z, maxZ)
    }
    fun union(p: Vec3) {
        minX = min(minX, p.x)
        minY = min(minY, p.y)
        minZ = min(minZ, p.z)
        maxX = max(p.x, maxX)
        maxY = max(p.y, maxY)
        maxZ = max(p.z, maxZ)
    }

    fun testPoint(p: Vec3) = testPoint(p.x, p.y, p.z)
    fun testPoint(x: Double, y: Double, z: Double) =
        x >= minX && y >= minY && x <= maxX && y <= maxY && z >= minZ && z <= maxZ

    fun toIntAABB(): IntAABB {
        val iaabb = IntAABB()
        iaabb.union(minX, minY, minZ)
        iaabb.union(maxX, maxY, maxZ)
        iaabb.correctBounds()
        return iaabb
    }
}