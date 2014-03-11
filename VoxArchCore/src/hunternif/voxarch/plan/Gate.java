package hunternif.voxarch.plan;

import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

/**
 * A gate between 2 {@link Room}s, can be vertical or horizontal. A gate
 * generator should clear a passage and create some sort arch over it. The y
 * coordinate should be at room's origin + 1, to account for the floor.
 * @author Hunternif
 */
public class Gate {
	public static enum Orientation {
		VERTICAL, HORIZONTAL;
	}

	private final Room parent, room1, room2;
	
	/** For a horizontal gate, the origin point is in the middle of the gate at
	 * floor level. For a vertical gate, it is in the actual center of it.*/
	private final Vec3 origin;
	
	private final double rotationY;
	
	/** For vertical gates it is (width, height), for horizontal (width, length). */
	private final Vec2 size;
	
	private final Orientation orientation;
	
	/** The type specifies a purpose for the room. It can be used by a generator
	 * to assign a particular style to it. */
	private String type = null;
	
	public Gate(Room parent, Room room1, Room room2, Vec3 origin, Vec2 size, Orientation orientation, double rotationY) {
		this.parent = parent;
		this.room1 = room1;
		this.room2 = room2;
		this.origin = origin;
		this.size = size;
		this.orientation = orientation;
		this.rotationY = rotationY;
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

	public Room getParent() {
		return parent;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
