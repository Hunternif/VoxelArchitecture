package hunternif.voxarch.util;

import hunternif.voxarch.storage.IBlockStorage;

public class WorldUtil {
	/**
	 * Remove all blocks from the specified volume that the specified filter
	 * doesn't accept.
	 */
	public static void clearVolume(IBlockStorage storage, Region3 volume, IBlockFilter filter) {
		for (int x = volume.minX; x <= volume.maxX; x++) {
			for (int z = volume.minZ; z <= volume.maxZ; z++) {
				for (int y = volume.minY; y <= volume.maxY; y++) {
					BlockData block = storage.getBlock(x, y, z);
					if (!filter.accept(block)) {
						storage.clearBlock(x, y, z);
					}
				}
			}
		}
	}
}
