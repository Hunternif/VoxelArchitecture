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
	
	/**
	 * Helper method, only actually does something in OrientableBlockData.
	 * What it does there is rotate the BlockOrientation counterclockwise by
	 * the specified angle.
	 */
	public void rotate(float angle) {}
}
