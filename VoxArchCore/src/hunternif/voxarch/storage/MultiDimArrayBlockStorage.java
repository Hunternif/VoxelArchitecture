package hunternif.voxarch.storage;

public class MultiDimArrayBlockStorage implements IFixedBlockStorage {
	/** [0 .. x .. width] [0 .. y .. height] [0 .. z .. length] [id, metadata] */
	private final int[][][][] array;
	
	/** BlockData that is returned in every call, to keep the memory overhead
	 * at the very minimum. */
	private final BlockData reusableData;
	
	public MultiDimArrayBlockStorage(int width, int height, int length) {
		array = new int[width][height][length][2];
		reusableData = new BlockData(0);
	}
	
	@Override
	public BlockData getBlock(int x, int y, int z) {
		int[] data = array[x][y][z];
		if (data[0] == 0) return null;
		reusableData.id = data[0];
		reusableData.metadata = data[1];
		return reusableData;
	}

	@Override
	public void setBlock(int x, int y, int z, BlockData block) {
		int[] data = array[x][y][z];
		data[0] = block.id;
		data[1] = block.metadata;
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

}
