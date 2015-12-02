package hunternif.voxarch.plan;

import hunternif.voxarch.vector.Vec3;

/**
 * A fixed-size prop, i.e. a statue or a simple torch.
 * During planning it is effectively treated as a point.
 * @author Hunternif
 */
public class Prop {
	private final Room parent;
	
	/** The coordinates in blocks of the origin point relative to the origin
	 * of the parent room. Origin's location is different depending on mount type. */
	private final Vec3 origin;

	private final double rotationY;
	
	/** Defines what this prop actually is. */
	private final String name;
	
	/** Can be used by a generator to assign a particular style or materials to the prop. */
	private String type = null;

	public Prop(Room parent, String name, Vec3 origin, double rotationY) {
		this.parent = parent;
		this.name = name;
		this.origin = new Vec3(origin);
		this.rotationY = rotationY;
	}

	public Room getParent() {
		return parent;
	}
	
	public String getName() {
		return name;
	}
	
	public Vec3 getOrigin() {
		return origin;
	}

	public double getRotationY() {
		return rotationY;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
