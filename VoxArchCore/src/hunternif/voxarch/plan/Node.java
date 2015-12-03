package hunternif.voxarch.plan;

import hunternif.voxarch.vector.Vec3;

/**
 * Contains some generic properties of a part of an {@link ArchPlan}.
 * @author Hunternif
 */
public class Node {
	private Room parent;
	
	/** Is interpreted differently for different kinds of nodes. */
	private final Vec3 origin;
	
	private final double rotationY;
	
	/** The type specifies a purpose for the node. It can be used by a generator
	 * to assign a particular architectural style or materials to it. */
	private String type = null;
	
	/** For use with incremental building. This means that this node will not be re-built. */
	private boolean isBuilt = false;
	
	/** The origin vector will be copied. */
	public Node(Room parent, Vec3 origin, double rotationY) {
		this.parent = parent;
		this.origin = new Vec3(origin);
		this.rotationY = rotationY;
	}
	/** The origin vector will be copied. */
	public Node(Vec3 origin, double rotationY) {
		this(null, origin, rotationY);
	}
	
	
	protected void setParent(Room parent) {
		this.parent = parent;
	}
	public Room getParent() {
		return parent;
	}
	
	public Vec3 getOrigin() {
		return origin;
	}
	
	public double getRotationY() {
		return rotationY;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}

	public void setBuilt(boolean isBuilt) {
		this.isBuilt = isBuilt;
	}
	public boolean isBuilt() {
		return isBuilt;
	}
}
