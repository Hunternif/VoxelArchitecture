package hunternif.voxarch.plan;

import hunternif.voxarch.vector.Vec2;

public class Wall {
	/** Coordinates of end points of the base of the wall relative to the center
	 * of the containing room. */
	private final Vec2 p1, p2;
	
	private final Room room;
	
	public Wall(Room room, Vec2 p1, Vec2 p2) {
		this.room = room;
		this.p1 = new Vec2(p1);
		this.p2 = new Vec2(p2);
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
	
	public double getAngleDeg() {
		return Math.atan2(p2.y - p1.y, p2.x - p1.x) * 360 / Math.PI;
	}
	
	public double getLength() {
		return p2.distanceTo(p1);
	}
	
	public double getHeight() {
		return room.getSize().y;
	}
}
