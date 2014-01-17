package hunternif.voxarch.storage;

/** This storage has fixed dimensions and non-negative block coordinates. */
public interface IFixedBlockStorage extends IBlockStorage {
	int getWidth();
	int getHeight();
	int getLength();
}
