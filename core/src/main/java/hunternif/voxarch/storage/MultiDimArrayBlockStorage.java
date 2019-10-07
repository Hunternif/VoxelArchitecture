package hunternif.voxarch.storage;

import hunternif.voxarch.util.DebugUtil;

/**
 * IFixedBlockStorage implementation using multidimensional array of BlockData.
 * Doesn't reuse BlockData!
 * @author Hunternif
 */
public class MultiDimArrayBlockStorage implements IFixedBlockStorage {
	/** [0 .. x .. width] [0 .. y .. height] [0 .. z .. length] */
	private final BlockData[][][] array;
	
	public static final IStorageFactory factory = new IStorageFactory() {
		@Override
		public IFixedBlockStorage createFixed(int width, int height, int length) {
			return new MultiDimArrayBlockStorage(width, height, length);
		}
	};
	
	public MultiDimArrayBlockStorage(int width, int height, int length) {
		array = new BlockData[width][height][length];
	}
	
	@Override
	public BlockData getBlock(int x, int y, int z) {
		return array[x][y][z];
	}

	@Override
	public void setBlock(int x, int y, int z, BlockData block) {
		array[x][y][z] = block;
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		array[x][y][z] = null;
	}

	@Override
	public int getWidth() {
		return array.length;
	}

	@Override
	public int getHeight() {
		return array[0].length;
	}

	@Override
	public int getLength() {
		return array[0][0].length;
	}
	
	@Override
	public String toString() {
		return DebugUtil.printFixedStorage(this);
	}

}
