package hunternif.voxarch.util;

/**
 * An abstraction for the way orientation is written to block metadata.
 * Override {@link #setOrientaion(BlockOrientation)} to set metadata
 * appropriately.
 */
public abstract class OrientableBlockData extends BlockData {

	private BlockOrientation orient = BlockOrientation.NONE;
	
	public OrientableBlockData(int id, int metadata) {
		super(id, metadata);
	}
	
	@Override
	public void rotate(float angle) {
		BlockOrientation orient = getOrientaion();
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

}
