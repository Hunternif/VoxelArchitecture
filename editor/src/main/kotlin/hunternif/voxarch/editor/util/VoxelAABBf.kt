package hunternif.voxarch.editor.util

import org.joml.AABBf
import org.joml.Vector3f
import kotlin.math.max
import kotlin.math.min

/** AABB that snaps to integer coordinates, but its edges are at 0.5 offset. */
open class VoxelAABBf {
    var minX: Int = Int.MAX_VALUE
    var minY: Int = Int.MAX_VALUE
    var minZ: Int = Int.MAX_VALUE
    var maxX: Int = Int.MIN_VALUE
    var maxY: Int = Int.MIN_VALUE
    var maxZ: Int = Int.MIN_VALUE
    
    private val edgeAABB = AABBf()

    /** Resets min and max to infinity */
    fun reset() {
        edgeAABB.reset()
        minX =  Int.MAX_VALUE
        minY =  Int.MAX_VALUE
        minZ =  Int.MAX_VALUE
        maxX =  Int.MIN_VALUE
        maxY =  Int.MIN_VALUE
        maxZ =  Int.MIN_VALUE
    }

    fun set(other: VoxelAABBf) {
        minX = other.minX
        minY = other.minY
        minZ = other.minZ
        maxX = other.maxX
        maxY = other.maxY
        maxZ = other.maxZ
        updateEdgeAABB()
    }

    fun setMin(x: Int, y: Int, z: Int) {
        minX = x
        minY = y
        minZ = z
        updateEdgeAABB()
    }
    fun setMax(x: Int, y: Int, z: Int) {
        maxX = x
        maxY = y
        maxZ = z
        updateEdgeAABB()
    }

    fun correctBounds() {
        minX = min(minX, maxX)
        minY = min(minY, maxY)
        minZ = min(minZ, maxZ)
        maxX = max(minX, maxX)
        maxY = max(minY, maxY)
        maxZ = max(minZ, maxZ)
        updateEdgeAABB()
    }

    private fun updateEdgeAABB() {
        edgeAABB.setMin(-0.5f + minX, -0.5f + minY, -0.5f + minZ)
        edgeAABB.setMax(0.5f + maxX, 0.5f + maxY, 0.5f + maxZ)
    }
    
    fun testPoint(x: Int, y: Int, z: Int) =
        edgeAABB.testPoint(x.toFloat(), y.toFloat(), z.toFloat())

    fun testPoint(x: Float, y: Float, z: Float) = edgeAABB.testPoint(x, y, z)

    fun testPoint(p: Vector3f) = edgeAABB.testPoint(p)

    fun testPointXZ(x: Float, z: Float) = edgeAABB.run {
        x >= minX && z >= minZ && x <= maxX && z <= maxZ
    }

    fun testPointXZ(p: Vector3f) = edgeAABB.run {
        p.x >= minX && p.z >= minZ && p.x <= maxX && p.z <= maxZ
    }

    fun union(x: Int, y: Int, z: Int) {
        minX = min(minX, x)
        minY = min(minY, y)
        minZ = min(minZ, z)
        maxX = max(x, maxX)
        maxY = max(y, maxY)
        maxZ = max(z, maxZ)
        updateEdgeAABB()
    }

    fun union(x: Double, y: Double, z: Double) {
        union(x.toInt(), y.toInt(), z.toInt())
    }
}