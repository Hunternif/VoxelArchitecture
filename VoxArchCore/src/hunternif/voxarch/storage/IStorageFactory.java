package hunternif.voxarch.storage;

public interface IStorageFactory {
	IFixedBlockStorage create(int width, int height, int length);
}
