package hunternif.voxarch.plan;

import hunternif.voxarch.util.RoomUtil;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

/**
 * A gate between 2 {@link Room}s, can be vertical or horizontal. A gate
 * generator should clear a passage and create some sort arch over it.
 * @author Hunternif
 */
public class Gate {
	public static enum Orientation {
		VERTICAL, HORIZONTAL;
	}

	private final Room parent, room1, room2;
	
	/** Coordinates of the origin of the parent room. */
	private final Vec3 origin;
	
	private double rotationY;
	
	/** For vertical gates it is (width, height), for horizontal (width, length). */
	private final Vec2 size;
	
	private final Orientation orientation;
	
	public Gate(Room parent, Room room1, Room room2, Vec3 origin, Vec2 size, Orientation orientation) {
		this.parent = parent;
		this.room1 = room1;
		this.room2 = room2;
		this.origin = origin;
		this.size = size;
		this.orientation = orientation;
	}

	public Vec3 getOrigin() {
		return origin;
	}

	public Vec2 getSize() {
		return size;
	}

	public void setRotationY(double value) {
		this.rotationY = value;
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

	/** Sets rotation of this gate aligned with the closest wall of the
	 * specified room. Used when connecting rooms together during initial
	 * stage of generation of {@link ArchPlan}. The room and this gate are
	 * assumed to have the same parent, i.e. position and rotation relative
	 * to it. */
	public void alignToClosestWall(Room room) {
		Vec2 origin2D = new Vec2(origin.x, origin.z);
		Wall closest = RoomUtil.findClosestWall(room, origin2D);
		if (closest != null) {
			setRotationY(closest.getAngleDeg() + room.getRotationY());
		}
	}
}
