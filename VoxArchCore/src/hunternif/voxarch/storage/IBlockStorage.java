package hunternif.voxarch.storage;

import hunternif.voxarch.util.BlockData;

/**
 * A unified interface for a voxel world into which structures are pasted.
 * <p>
 * To avoid incurring a large memory overhead, an implementation might reuse the
 * returned BlockData instances, so don't use them for long-term storage.
 * </p>
 */
public interface IBlockStorage {
	// Using plain ints instead of IntVec3 to improve performance,
	// because all these methods are likely to be used in iteration.
	BlockData getBlock(int x, int y, int z);
	void setBlock(int x, int y, int z, BlockData block);
	void clearBlock(int x, int y, int z);
}
