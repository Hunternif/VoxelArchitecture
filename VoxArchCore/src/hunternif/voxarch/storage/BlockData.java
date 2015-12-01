package hunternif.voxarch.storage;

import hunternif.voxarch.util.BlockOrientation;

/**
 * Contains block id, metadata and {@link BlockOrientation}. The orientation
 * should modify the metadata in overridden method {@link #setOrientaion}.
 * @author Hunternif
 */
public class BlockData {
	private int id;
	private int metadata;
	
	private BlockOrientation orient = null;
	
	public BlockData(int id) {
		this(id, 0);
	}
	
	public BlockData(int id, int metadata) {
		this.id = id;
		this.metadata = metadata;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	
	public void setMetadata(int metadata) {
		this.metadata = metadata;
	}
	public int getMetadata() {
		return metadata;
	}
	
	public boolean hasOrientation() {
		return orient != null;
	}
	
	/** Rotate the BlockOrientation (if not NONE) counterclockwise by the
	 * specified angle. */
	public void rotate(double angle) {
		if (!hasOrientation()) return;
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
	
	@Override
	public String toString() {
		return "id: " + id + " metadata: " + metadata;
	}
}
