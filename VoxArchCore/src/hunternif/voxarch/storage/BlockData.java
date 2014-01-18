package hunternif.voxarch.storage;

import hunternif.voxarch.util.BlockOrientation;

public class BlockData {
	protected int id;
	protected int metadata;
	
	private BlockOrientation orient = BlockOrientation.NONE;
	
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
	
	/** Rotate the BlockOrientation (if not NONE) counterclockwise by the
	 * specified angle. */
	public void rotate(float angle) {
		if (orient == BlockOrientation.NONE) return;
		setOrientaion(orient.rotate(angle));
	}

	public BlockOrientation getOrientaion() {
		return orient;
	}
	
	/** Override this method to set metadata appropriately. */
	public void setOrientaion(BlockOrientation orient) {
		this.orient = orient;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BlockData)) return false;
		if (obj == this) return true;
		return ((BlockData)obj).id == id && ((BlockData)obj).metadata == metadata;
	}
	
	@Override
	public BlockData clone() {
		return new BlockData(id, metadata);
	}
}
