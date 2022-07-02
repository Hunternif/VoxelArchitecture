package hunternif.voxarch.storage

import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.vector.Vec3

/**
 * A unified interface for a voxel world into which structures are pasted.
 *
 * To avoid incurring a large memory overhead, an implementation might reuse the
 * returned BlockData instances, so don't use them for long-term storage.
 */
interface IBlockStorage {
    /** Returns block data at the given coordinates, null if it is empty.  */
    fun getBlock(x: Int, y: Int, z: Int): BlockData?
    fun setBlock(x: Int, y: Int, z: Int, block: BlockData?)
    fun clearBlock(x: Int, y: Int, z: Int)


    // Convenience methods
    fun getBlock(x: Double, y: Double, z: Double): BlockData? = getBlock(x.toInt(), y.toInt(), z.toInt())
    fun getBlock(vec: IntVec3): BlockData? = vec.run { getBlock(x, y, z) }
    fun getBlock(vec: Vec3): BlockData? = vec.run { getBlock(x, y, z) }

    fun setBlock(x: Double, y: Double, z: Double, block: BlockData?) = setBlock(x.toInt(), y.toInt(), z.toInt(), block)
    fun setBlock(vec: IntVec3, block: BlockData?) = vec.run { setBlock(x, y, z, block) }
    fun setBlock(vec: Vec3, block: BlockData?) = vec.run { setBlock(x, y, z, block) }

    fun clearBlock(x: Double, y: Double, z: Double) { clearBlock(x, y, z) }
    fun clearBlock(vec: IntVec3) = vec.run { clearBlock(x, y, z) }
    fun clearBlock(vec: Vec3) = vec.run { clearBlock(x, y, z) }
}