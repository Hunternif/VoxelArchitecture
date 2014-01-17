package hunternif.voxarch.util;

public class BlockData {
	protected int id;
	protected int metadata;
	
	public BlockData(int id) {
		this(id, 0);
	}
	
	public BlockData(int id, int metadata) {
		this.id = id;
		this.metadata = metadata;
	}
	
	public int getId() {
		return id;
	}
	
	public int getMetadata() {
		return metadata;
	}
}
