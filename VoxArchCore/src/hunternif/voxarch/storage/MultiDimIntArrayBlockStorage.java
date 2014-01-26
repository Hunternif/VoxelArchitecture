package hunternif.voxarch.storage;

import hunternif.voxarch.util.DebugUtil;

/**
 * IFixedBlockStorage implementation using multidimensional array of integers.
 * @author Hunternif
 */
public class MultiDimIntArrayBlockStorage implements IFixedBlockStorage {
	/** [0 .. x .. width] [0 .. y .. height] [0 .. z .. length] [id, metadata] */
	private final int[][][][] array;
	
	/** BlockData that is returned in every call, to keep the memory overhead
	 * at the very minimum. */
	private final BlockData reusableData;
	
	public static final IStorageFactory factory = new IStorageFactory() {
		@Override
		public IFixedBlockStorage createFixed(int width, int height, int length) {
			return new MultiDimIntArrayBlockStorage(width, height, length);
		}
	};
	
	public MultiDimIntArrayBlockStorage(int width, int height, int length) {
		array = new int[width][height][length][2];
		reusableData = new BlockData(0);
	}
	
	@Override
	public BlockData getBlock(int x, int y, int z) {
		int[] data = array[x][y][z];
		if (data[0] == 0) return null;
		reusableData.setId(data[0]);
		reusableData.setMetadata(data[1]);
		return reusableData;
	}

	@Override
	public void setBlock(int x, int y, int z, BlockData block) {
		int[] data = array[x][y][z];
		data[0] = block.getId();
		data[1] = block.getMetadata();
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		int[] data = array[x][y][z];
		data[0] = 0;
		data[1] = 0;
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
