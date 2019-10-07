package hunternif.voxarch.storage;

import hunternif.voxarch.vector.IntVec3;

/**
 * A base class for a set of blocks, meant to be overridden.
 * @author Hunternif
 */
public class Structure {
	/** The underlying voxel storage that contains this structure's block data. */
	private final IFixedBlockStorage storage;
	
	/** The origin point. For a corridor it could be in the middle of its
	 * entrance. Block transformations should modify the origin while preserving
	 * its relative position to other blocks. */
	private IntVec3 origin = new IntVec3(0, 0, 0);
	
	/**
	 * Creates a new structure on the specified fixed-size storage.
	 */
	public Structure(IFixedBlockStorage storage) {
		this.storage = storage;
	}
	
	/** The underlying voxel storage that contains this structure's block data. */
	public IFixedBlockStorage getStorage() {
		return storage;
	}
	
	/** A vector (width, height, length) */
	public IntVec3 getSize() {
		return new IntVec3(storage.getWidth(), storage.getHeight(), storage.getLength());
	}
	
	/** Set a vector inside this structure as the origin point. The vector's
	 * relative position to other blocks should be preserved during block
	 * transformations i.e. rotation.*/
	public void setOrigin(int x, int y, int z) {
		this.origin = new IntVec3(x, y, z);
	}
	/** Returns the original origin. Be careful not to modify it accidentally! */
	public IntVec3 getOrigin() {
		return origin;
	}
}
