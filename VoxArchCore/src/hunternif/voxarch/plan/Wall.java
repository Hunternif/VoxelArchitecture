package hunternif.voxarch.plan;

import hunternif.voxarch.vector.Vec2;

/**
 * Wall that defines a boundary of a room. Should be positioned as follows
 * (in local coordinates):
 * <pre>
 *  +-------------> X
 *  |      inside
 *  | p1 == wall == p2
 *  |      outside
 *  V
 * Y
 * </pre>
 * @author Hunternif
 */
public class Wall {
	/** Coordinates of end points of the base of the wall relative to the center
	 * of the containing room. */
	private final Vec2 p1, p2;
	
	private final Room room;
	
	/** If true, the wall will not generated, but only used as boundary for
	 * calculations. */
	private boolean transparent = false;
	
	/** Vectors will be copied. */
	public Wall(Room room, Vec2 p1, Vec2 p2, boolean transparent) {
		this.room = room;
		this.p1 = new Vec2(p1);
		this.p2 = new Vec2(p2);
		this.transparent = transparent;
	}
	public Wall(Room room, Vec2 p1, Vec2 p2) {
		this(room, p1, p2, false);
	}

	public Vec2 getP1() {
		return p1;
	}
	public Vec2 getP2() {
		return p2;
	}

	public Room getRoom() {
		return room;
	}
	
	/** Returns the angle between the vector of this wall and the X axis. */
	public double getAngleDeg() {
		// The first argument is inverted because the Y axis represents inverted
		// Z axis
		return Math.atan2(-p2.y + p1.y, p2.x - p1.x) * 180 / Math.PI;
	}
	
	public double getLength() {
		return p2.distanceTo(p1);
	}
	
	public double getHeight() {
		return room.getSize().y;
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}
}
