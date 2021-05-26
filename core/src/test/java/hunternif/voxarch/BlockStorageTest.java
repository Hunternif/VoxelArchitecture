package hunternif.voxarch;

import static org.junit.Assert.*;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.MultiDimArrayBlockStorage;
import hunternif.voxarch.storage.IFixedBlockStorage;

import org.junit.Test;

/**
 * @author Hunternif
 */
public class BlockStorageTest {
	@Test
	public void testArrayBlockStorage() {
		IFixedBlockStorage storage = new MultiDimArrayBlockStorage(2, 3, 4);
		BlockData block = new BlockData("B");
		storage.setBlock(1, 2, 0, block);
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 3; y++) {
				for (int z = 0; z < 4; z++) {
					if (x == 1 && y == 2 && z == 0) {
						assertEquals(block, storage.getBlock(x, y, z));
					} else {
						assertNull(storage.getBlock(x, y, z));
					}
				}
			}
		}
	}
}
