package hunternif.voxarch.storage;

import hunternif.voxarch.util.BlockData;
import hunternif.voxarch.util.IntVec2;
import hunternif.voxarch.util.MathUtil;
import hunternif.voxarch.util.Matrix2;

/**
 * A base class for a set of blocks, meant to be overridden.
 * @author Hunternif
 */
public class Structure {
	/** The underlying voxel storage that contains this structure's block data. */
	private final IFixedBlockStorage storage;
	
	/**
	 * Creates a new structure on the specified fixed-size storage.
	 */
	public Structure(IFixedBlockStorage storage) {
		this.storage = storage;
	}
	
	public int getWidth() {
		return storage.getWidth();
	}
	public int getHeight() {
		return storage.getHeight();
	}
	public int getLength() {
		return storage.getLength();
	}
	
	/**
	 * Creates a new structure with the content of this structure rotated
	 * at an arbitrary angle around the Y axis. <b>Warning:</b> Rotating at
	 * a non-right angle will screw the structure significantly.
	 * @param factory	is needed to allocate memory for the new structure.
	 * @param angle		is in degrees, counterclockwise.
	 */
	public Structure rotate(IStorageFactory factory, float angle) {
		/*
		 * Aerial view:
		 * Y
		 *  +----> X (East)
		 *  |
		 *  V
		 *  Z
		 */
		Matrix2 mat = Matrix2.rotationMatrix(angle);
		
		IntVec2 vec = new IntVec2(getWidth(), getLength());
		mat.multiplyCeiling(vec);
		IFixedBlockStorage newStorage = factory.create(vec.x, getHeight(), vec.y);
		Structure newStruct = new Structure(newStorage);
		
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				for (int z = 0; z < getLength(); z++) {
					BlockData block = storage.getBlock(x, y, z);
					if (block == null) continue;
					block.rotate(angle);
					mat.multiplyCeiling(vec.set(x, z));
					newStorage.setBlock(vec.x, y, vec.y, block);
				}
			}
		}
		return newStruct;
	}
	
	/**
	 * Paste this structure into the given storage at the specified point.
	 */
	public void pasteInto(IBlockStorage world, int x, int y, int z) {
		for (int nx = 0; nx < getWidth(); nx++) {
			for (int ny = 0; ny < getHeight(); ny++) {
				for (int nz = 0; nz < getLength(); nz++) {
					world.setBlock(x + nx, y + ny, z + nz, storage.getBlock(nx, ny, nz));
				}
			}
		}
	}
}
