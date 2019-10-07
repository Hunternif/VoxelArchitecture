package hunternif.voxarch.plan;

import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

/**
 * A gate between 2 {@link Room}s, can be vertical or horizontal. A gate
 * generator should clear a passage and may want to create some sort arch
 * over it.
 * The y coordinate should be at room's origin.
 * @author Hunternif
 */
public class Gate extends Node {
	public static enum Orientation {
		VERTICAL, HORIZONTAL;
	}

	private final Room room1, room2;
	
	/** For vertical gates it is (width, height), for horizontal (width, length). */
	private final Vec2 size;
	
	private final Orientation orientation;
	
	/**
	 * For a horizontal gate, the origin point is in the middle of the gate at
	 * floor level. For a vertical gate, it is in the actual center of it.
	 * The vector arguments will be cloned.
	 */
	public Gate(Room parent, Room room1, Room room2, Vec3 origin, Vec2 size, Orientation orientation, double rotationY) {
		super(parent, origin, rotationY);
		this.room1 = room1;
		this.room2 = room2;
		this.size = new Vec2(size);
		this.orientation = orientation;
	}

	public Vec2 getSize() {
		return size;
	}
	
	public Room getRoom1() {
		return room1;
	}

	public Room getRoom2() {
		return room2;
	}

	protected Orientation getOrientation() {
		return orientation;
	}
	public boolean isVertical() {
		return orientation == Orientation.VERTICAL;
	}
	public boolean isHorizontal() {
		return orientation == Orientation.HORIZONTAL;
	}
}
