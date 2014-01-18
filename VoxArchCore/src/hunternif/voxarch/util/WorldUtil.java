package hunternif.voxarch.util;

import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.storage.Structure;

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
	
	/**
	 * Paste the specified structure into the specified storage at the specified
	 * point.
	 */
	public static void pasteStructure(IBlockStorage world, Structure structure, int x, int y, int z) {
		IntVec3 size = structure.getSize();
		for (int nx = 0; nx < size.x; nx++) {
			for (int ny = 0; ny < size.y; ny++) {
				for (int nz = 0; nz < size.z; nz++) {
					world.setBlock(x + nx, y + ny, z + nz, structure.getStorage().getBlock(nx, ny, nz));
				}
			}
		}
	}
}
