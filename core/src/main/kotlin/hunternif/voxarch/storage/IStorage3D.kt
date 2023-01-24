package hunternif.voxarch.storage

import hunternif.voxarch.vector.IntVec3

interface IStorage3D<T> : Iterable<IntVec3> {
    val minX: Int
    val minY: Int
    val minZ: Int
    val maxX: Int
    val maxY: Int
    val maxZ: Int
    val width: Int get() = if (size == 0) 0 else maxX - minX + 1
    val height: Int get() = if (size == 0) 0 else maxY - minY + 1
    val depth: Int get() = if (size == 0) 0 else maxZ - minZ + 1
    val sizeVec: IntVec3 get() = IntVec3(width, height, depth)
    operator fun get(x: Int, y: Int, z: Int): T
    operator fun set(x: Int, y: Int, z: Int, v: T)
    /** The number of non-null elements */
    val size: Int
    // Additional default implementations
    operator fun get(p: IntVec3): T = get(p.x, p.y, p.z)
    operator fun set(p: IntVec3, v: T) { set(p.x, p.y, p.z, v ) }

    operator fun contains(p: IntVec3): Boolean = contains(p.x, p.y, p.z)
    @Suppress("ConvertTwoComparisonsToRangeCheck")
    fun contains(x: Int, y: Int, z: Int): Boolean =
        x >= minX && x <= maxX &&
        y >= minY && y <= maxY &&
        z >= minZ && z <= maxZ

    override operator fun iterator(): Iterator<IntVec3> = iterator {
        for (x in minX .. maxX)
            for (y in minY .. maxY)
                for (z in minZ .. maxZ)
                    yield(IntVec3(x, y, z))
    }

    fun isEmpty(): Boolean = size == 0
    fun isNotEmpty(): Boolean = size != 0
}