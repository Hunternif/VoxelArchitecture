package hunternif.voxarch.storage;

/**
 * This storage has fixed dimensions and non-negative block coordinates.
 * @author Hunternif
 */
public interface IFixedBlockStorage extends IBlockStorage {
	int getWidth();
	int getHeight();
	int getLength();
}
