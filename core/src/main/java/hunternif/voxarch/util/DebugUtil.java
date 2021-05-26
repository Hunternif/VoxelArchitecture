package hunternif.voxarch.util;

import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.storage.IFixedBlockStorage;
import hunternif.voxarch.vector.IntVec3;

/**
 * 
 * @author Hunternif
 *
 */
public class DebugUtil {
	/**
	 * Returns a string representation of a 3d region of a block storage. It
	 * consists of separate blocks representing one y-layer from the lowest to
	 * the highest, with X axis running from left to right and Z axis running
	 * from top to bottom.
	 * @param storage
	 * @param from		minimum coordinates on all axes
	 * @param to		maximum coordinates on all axes
	 */
	public static String printStorageRegion(IBlockStorage storage, IntVec3 from, IntVec3 to) {
		StringBuilder sb = new StringBuilder();
		for (int y = from.y; y < to.y; y++) {
			if (sb.length() > 0) sb.append("\n\n");
			for (int z = from.z, dz = 0; z < to.z; z++, dz++) {
				if (dz > 0) sb.append("\n");
				for (int x = from.x, dx = 0; x < to.x; x++, dx++) {
					if (dx > 0) sb.append(" ");
					BlockData block = storage.getBlock(x, y, z);
					if (block == null) sb.append(0);
					else sb.append(block.getKey());
				}
			}
		}
		return sb.toString();
	}
	
	/** See {@link #printStorageRegion}, but for all contents of the storage. */
	public static String printFixedStorage(IFixedBlockStorage storage) {
		return printStorageRegion(storage, new IntVec3(0, 0, 0),
				new IntVec3(storage.getWidth(), storage.getHeight(), storage.getLength()));
	}
}
