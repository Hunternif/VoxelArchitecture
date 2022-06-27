package hunternif.voxarch.storage

import hunternif.voxarch.vector.IntVec3

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

    // Default implementations
    fun getBlock(vec: IntVec3): BlockData? = vec.run { getBlock(x, y, z) }
    fun setBlock(vec: IntVec3, block: BlockData?) = vec.run { setBlock(x, y, z, block) }
    fun clearBlock(vec: IntVec3) = vec.run { clearBlock(x, y, z) }
}