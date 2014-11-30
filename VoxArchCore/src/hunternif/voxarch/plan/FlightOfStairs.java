package hunternif.voxarch.plan;

import hunternif.voxarch.vector.Vec3;

public class FlightOfStairs extends Room {

	private final double stairHeight;
	
	public FlightOfStairs(Vec3 origin, Vec3 size, double rotationY, double stairHeight) {
		super(origin, size, rotationY);
		this.stairHeight = stairHeight;
	}
	
	public double getStairHeight() {
		return stairHeight;
	}

}
