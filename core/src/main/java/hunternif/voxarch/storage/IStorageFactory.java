package hunternif.voxarch.storage;

/**
 * Factory for allocating memory for fixed-size block storage.
 * @author Hunternif
 */
public interface IStorageFactory {
	/** Factory method, returns a new fixed-size block storage with the
	 * specified dimensions. */
	IFixedBlockStorage createFixed(int width, int height, int length);
}
