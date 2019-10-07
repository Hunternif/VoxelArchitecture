package hunternif.voxarch.plan;

import hunternif.voxarch.vector.Vec3;

/**
 * Stairs are a special kind of room that has an slope along the local X axis.
 * @author Hunternif
 */
public class FlightOfStairs extends Room {

	/** The change in height at 1 block distance. */
	private double slope = 0;
	
	public FlightOfStairs(Room parent, Vec3 origin, Vec3 size, double rotationY) {
		super(parent, origin, size, rotationY);
	}

	public void setSlope(double slope) {
		this.slope = slope;
	}
	
	public double getSlope() {
		return slope;
	}

}
