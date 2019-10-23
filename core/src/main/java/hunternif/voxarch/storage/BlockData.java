package hunternif.voxarch.storage;

import hunternif.voxarch.util.Direction;

/**
 * Contains block id, metadata and {@link Direction}. The orientation
 * should modify the metadata in overridden method {@link #setOrientation}.
 * @author Hunternif
 */
public class BlockData {
	private int id;
	private int metadata;
	
	private Direction orient = null;
	
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
	
	/** Rotate the Direction (if not NONE) counterclockwise by the
	 * specified angle. */
	public void rotate(double angle) {
		if (!hasOrientation()) return;
		setOrientation(orient.rotate(angle));
	}

	public Direction getOrientation() {
		return orient;
	}
	
	/** Override this method to set metadata appropriately. */
	public void setOrientation(Direction orient) {
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
		return "id: " + id + " " + orient.name() + " meta: " + metadata;
	}
}
