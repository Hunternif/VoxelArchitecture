package hunternif.voxarch.storage;

import hunternif.voxarch.util.IntVec3;

public interface IStructure extends IBlockStorage {
	/**
	 * Rotate the structure at an arbitrary angle around the vertical axis.
	 * Rotating at a non-right angle will screw the structure significantly.
	 */
	void rotate(float angle);
	
	/**
	 * Insert another structure into this structure at the specified point.
	 */
	// Using plain ints instead of one IntVec3 here because I probably won't
	// have a pre-instantiated vector when inserting structures, so requiring
	// to instantiate one will be excessive.
	void insert(IStructure toInsert, int x, int y, int z);
	
	/**
	 * @return vector (width, height, length).
	 */
	IntVec3 getSize();
}
