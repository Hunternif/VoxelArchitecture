package hunternif.voxarch.vector

import hunternif.voxarch.util.MathUtil.ceiling
import hunternif.voxarch.util.MathUtil.floor
import kotlin.math.max
import kotlin.math.min

/** Axis-aligned bounding box in 3D, in integer coordinates. */
data class IntAABB(
    var minX: Int = Int.MAX_VALUE,
    var minY: Int = Int.MAX_VALUE,
    var minZ: Int = Int.MAX_VALUE,
    var maxX: Int = Int.MIN_VALUE,
    var maxY: Int = Int.MIN_VALUE,
    var maxZ: Int = Int.MIN_VALUE,
) {
    val size: IntVec3 = IntVec3(0, 0, 0)
        get() = field.set(maxX - minX, maxY - minY, maxZ - minZ)

    var minVec: IntVec3 = IntVec3(0, 0, 0)
        get() = field.set(minX, minY, minZ)
        set(value) { setMin(value) }

    var maxVec: IntVec3 = IntVec3(0, 0, 0)
        get() = field.set(maxX, maxY, maxZ)
        set(value) { setMax(value) }

    fun setMin(x: Int, y: Int, z: Int) {
        minX = x
        minY = y
        minZ = z
    }
    fun setMin(min: IntVec3) {
        minX = min.x
        minY = min.y
        minZ = min.z
    }
    fun setMax(x: Int, y: Int, z: Int) {
        maxX = x
        maxY = y
        maxZ = z
    }
    fun setMax(max: IntVec3) {
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

    fun union(x: Int, y: Int, z: Int) {
        minX = min(minX, x)
        minY = min(minY, y)
        minZ = min(minZ, z)
        maxX = max(x, maxX)
        maxY = max(y, maxY)
        maxZ = max(z, maxZ)
    }
    fun union(x: Double, y: Double, z: Double) {
        minX = min(minX, floor(x))
        minY = min(minY, floor(y))
        minZ = min(minZ, floor(z))
        maxX = max(ceiling(x), maxX)
        maxY = max(ceiling(y), maxY)
        maxZ = max(ceiling(z), maxZ)
    }
    fun union(p: IntVec3) {
        union(p.x, p.y, p.z)
    }
    fun union(p: Vec3) {
        union(p.x, p.y, p.z)
    }

    fun testPoint(p: Vec3) = testPoint(p.x, p.y, p.z)
    fun testPoint(x: Double, y: Double, z: Double) =
        x >= minX && y >= minY && x <= maxX && y <= maxY && z >= minZ && z <= maxZ
    fun testPoint(x: Int, y: Int, z: Int) =
        x >= minX && y >= minY && x <= maxX && y <= maxY && z >= minZ && z <= maxZ
}