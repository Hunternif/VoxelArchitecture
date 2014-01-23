package hunternif.voxarch.plan;

import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

public class Gate {
	public static enum Orientation {
		VERTICAL, HORIZONTAL;
	}

	private final Room room1, room2;
	
	/** Coordinates of the middle of the base of the gate relative to the global
	 * architectural plan. */ 
	private final Vec3 origin;
	
	private final double rotationY;
	
	/** For vertical gates is is (width, height), for horizontal (width, length). */
	private final Vec2 size;
	
	private final Orientation orientation;
	
	public Gate(Room room1, Room room2, Vec3 origin, Vec2 size, double rotationY, Orientation orientation) {
		this.room1 = room1;
		this.room2 = room2;
		this.origin = origin;
		this.size = size;
		this.rotationY = rotationY;
		this.orientation = orientation;
	}

	public Vec3 getOrigin() {
		return origin;
	}

	public Vec2 getSize() {
		return size;
	}

	public double getRotationY() {
		return rotationY;
	}

	public Room getRoom1() {
		return room1;
	}

	public Room getRoom2() {
		return room2;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	/** Sets rotation of this gate aligned with the closest wall of the
	 * specified room. Used when connecting rooms together during initial
	 * stage of generation of {@link ArchPlan}. */
	public void alignToClosestWall(Room room) {
		//TODO align to closest wall
	}
}
