package hunternif.voxarch.util;

/** An abstraction for the way orientation is written to block metadata. */
public abstract class OrientableBlockData extends BlockData {

	public OrientableBlockData(int id, int metadata) {
		super(id, metadata);
	}
	
	@Override
	public void rotate(float angle) {
		BlockOrientation orient = getOrientaion();
		if (orient == BlockOrientation.NONE) return;
		setOrientaion(orient.rotate(angle));
	}

	public abstract BlockOrientation getOrientaion();
	
	public abstract void setOrientaion(BlockOrientation orient);

}
