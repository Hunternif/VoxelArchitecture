package hunternif.voxarch.plan;

import hunternif.voxarch.vector.Vec3;

/**
 * Stairs are a special kind of room that has an slope along the local X axis.
 * @author Hunternif
 */
public class FlightOfStairs extends Room {

	/** The change in height at 1 block distance. */
	private double slope = 0;
	
	public FlightOfStairs(Vec3 origin, Vec3 size) {
		super(origin, size);
	}

	public void setSlope(double slope) {
		this.slope = slope;
	}
	
	public double getSlope() {
		return slope;
	}

}
