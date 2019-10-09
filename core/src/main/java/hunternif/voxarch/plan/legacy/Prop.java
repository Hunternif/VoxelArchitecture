package hunternif.voxarch.plan.legacy;

import hunternif.voxarch.vector.Vec3;

/**
 * A fixed-size prop, i.e. a statue or a simple torch.
 * During planning it is effectively treated as a point.
 * @author Hunternif
 */
public class Prop extends Node {
	/** Defines what this prop actually is. */
	private final String name;

	/**
	 * The coordinates in blocks of the origin point relative to the origin
	 * of the parent room. Origin's location is different depending on mount type.
	 */
	public Prop(Room parent, String name, Vec3 origin, double rotationY) {
		super(parent, origin, rotationY);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
