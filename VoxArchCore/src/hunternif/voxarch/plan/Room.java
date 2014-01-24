package hunternif.voxarch.plan;

import hunternif.voxarch.util.MathUtil;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class of the architectural plan, can have walls and contain more rooms.
 * Contains gates between the child rooms.
 * @author Hunternif
 */
public class Room {
	private final Room parent;
	private final List<Room> children = new ArrayList<Room>();
	
	private final List<Gate> gates = new ArrayList<Gate>();
	
	protected final List<Wall> walls = new ArrayList<Wall>();
	
	/** Vector (width, height, length), doesn't take rotation into account. */
	private final Vec3 size;

	/** The coordinates in blocks of the origin point relative to the origin
	 * of the parent room. The origin is located at the center of the floor. */
	private final Vec3 origin;
	
	private final double rotationY;
	
	public Room(Room parent, Vec3 origin, Vec3 size, double rotationY) {
		this.parent = parent;
		this.origin = new Vec3(origin);
		this.size = new Vec3(size);
		this.rotationY = rotationY;
	}
	
	public Vec3 getOrigin() {
		return origin;
	}
	
	public Room getParent() {
		return parent;
	}
	
	public void addChild(Room child) {
		children.add(child);
	}

	public List<Room> getChildren() {
		return children;
	}
	
	public List<Wall> getWalls() {
		return walls;
	}

	public Vec3 getSize() {
		return size;
	}

	public double getRotationY() {
		return rotationY;
	}
	
	public void addGate(Gate gate) {
		gates.add(gate);
	}
	
	public List<Gate> getGates() {
		return gates;
	}
	
	
	/** Fills the {@link #walls} array with 4 walls defined by the {@link #size}
	 * vector. */
	public void createFourWalls() {
		double a = size.x/2;
		double b = size.z/2;
		/*
		 * Aerial view of the reference frame:
		 * Y
		 *  +----> X (East)
		 *  |
		 *  V
		 *  Z
		 */
		// Going counterclockwise:
		walls.add(new Wall(this, new Vec2(a, b), new Vec2(a, -b)));
		walls.add(new Wall(this, new Vec2(a, -b), new Vec2(-a, -b)));
		walls.add(new Wall(this, new Vec2(-a, -b), new Vec2(-a, b)));
		walls.add(new Wall(this, new Vec2(-a, b), new Vec2(a, b)));
	}
	
	/** Fills the {@link #walls} array with a cycle of walls approximating an
	 * oval inscribed in the bounding box defined by the {@link #size} vector.
	 * @param vertices the number of vertices on the oval, has to be >= 3. */
	public void createRoundWalls(int vertices) {
		if (vertices < 3) return;
		double a = size.x/2;
		double b = size.z/2;
		double angleStep = 360d / (double)vertices;
		// Going counterclockwise:
		for (double angle = 0; angle < 360; angle += angleStep) {
			walls.add(new Wall(this,
					new Vec2(a * MathUtil.cosDeg(angle), - b * MathUtil.sinDeg(angle)),
					new Vec2(a * MathUtil.cosDeg(angle + angleStep), - b * MathUtil.sinDeg(angle + angleStep))));
		}
	}
	
	public void connectTo(Room room) {
		//TODO create a gate between the rooms and align it to wall.
		// The gate has to be about the size of the intersection of the 2 rooms
		// Use vertical gate if the room is above or below this one, otherwise
		// use horizontal gate.
	}
	
	/** Calculates the position of the origin of the room relative to the origin
	 * of the {@link ArchPlan} by adding together all its parent rooms' origins. */
	public Vec3 findGlobalPosition() {
		Vec3 pos = new Vec3(getOrigin());
		Room room = this;
		while (room.getParent() != null) {
			room = room.getParent();
			pos.add(room.getOrigin());
		}
		return pos;
	}
	
	/** Calculates the total rotation of the room around the Y axis by adding
	 * all its parent rooms' rotations. */
	public double findGlobalRotationY() {
		double rot = getRotationY();
		Room room = this;
		while (room.getParent() != null) {
			room = room.getParent();
			rot += room.getRotationY();
		}
		return rot;
	}
}
