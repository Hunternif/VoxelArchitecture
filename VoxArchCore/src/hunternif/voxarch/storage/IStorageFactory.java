package hunternif.voxarch.storage;

public interface IStorageFactory {
	IFixedBlockStorage createFixed(int width, int height, int length);
}
