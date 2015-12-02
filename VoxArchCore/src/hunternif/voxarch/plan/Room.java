package hunternif.voxarch.plan;

import hunternif.voxarch.util.Box;
import hunternif.voxarch.util.MathUtil;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class of the architectural plan, can have walls and contain more rooms.
 * Contains gates between the child rooms. The size of the room should include
 * the floor at y = 0 and the ceiling at y = height;
 * the walls run through the middle of blocks, and each block has its origin in
 * its center, therefore there is no 0.5 offset.
 * 
 * Think of blocks as a replacement for coordinates.
 * The size of the room equals the number of blocks that make the respective
 * walls minus one.
 * In the example below 0 is air, 1 is floor, 2 is wall, 3 is ceiling:
 * 
 *  2 2 2
 *  2 1 2
 *  2 2 2
 *  
 *  2 2 2
 *  2 0 2
 *  2 2 2
 *  
 *  2 2 2
 *  2 3 2
 *  2 2 2
 *  
 *  The room traced above floor by floor has size 2x2x2 and origin at (1, 0, 1)
 *  
 * @author Hunternif
 */
public class Room {
	private Room parent;
	private final List<Room> children = new ArrayList<Room>();
	
	private final List<Gate> gates = new ArrayList<Gate>();
	
	protected final List<Wall> walls = new ArrayList<Wall>();
	
	/** Vector (width, height, length), doesn't take rotation into account.
	 * Components of this vector are equal to the distance between the corners
	 * of the room. It would take that number + 1 blocks to build each boundary
	 * of the room in a world. */
	private final Vec3 size;

	/** The coordinates in blocks of the origin point relative to the origin
	 * of the parent room. The origin is located in the center of the room
	 * at the the floor level. */
	private final Vec3 origin;
	
	private final double rotationY;
	
	/** These flags determine whether the floor or the ceiling will be generated. */
	private boolean hasCeiling = true, hasFloor = true;
	
	/** The type specifies a purpose for the room. It can be used by a generator
	 * to assign a particular style to it. */
	private String type = null;
	
	/**
	 * @param origin	center of the room, relative to the origin of the parent.
	 * @param size		length of boundaries, each would occupy size + 1 actual blocks.
	 * @param rotationY	in degrees, relative to the parent.
	 */
	public Room(Vec3 origin, Vec3 size, double rotationY) {
		this.origin = new Vec3(origin);
		this.size = new Vec3(size);
		this.rotationY = rotationY;
	}
	
	protected void setParent(Room parent) {
		this.parent = parent;
	}
	public Vec3 getOrigin() {
		return origin;
	}
	
	public Room getParent() {
		return parent;
	}
	
	public void addChild(Room child) {
		children.add(child);
		child.setParent(this);
	}
	public Room addChild(Vec3 origin, Vec3 size, double rotationY) {
		Room room = new Room(origin, size, rotationY);
		addChild(room);
		return room;
	}

	public List<Room> getChildren() {
		return children;
	}
	
	public List<Wall> getWalls() {
		return walls;
	}

	/** Vector (width, height, length), doesn't take rotation into account.
	 * Components of this vector are equal to the distance between the corners
	 * of the room. It would take that number + 1 blocks to build each boundary
	 * of the room in a world. */
	public Vec3 getSize() {
		return size;
	}
	
	public Box getBoundingBox() {
		// Not a field member because the origin and size vectors are mutable.
		return new Box(origin, size);
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
		//TODO: check the round walls, they have protruding ends sticking out.
		if (vertices < 3) return;
		double a = size.x/2;
		double b = size.z/2;
		double angleStep = 360d / (double)vertices;
		// Going counterclockwise:
		for (double angle = -angleStep/2; angle < 360 - angleStep/2; angle += angleStep) {
			walls.add(new Wall(this,
					new Vec2(a * MathUtil.cosDeg(angle), - b * MathUtil.sinDeg(angle)),
					new Vec2(a * MathUtil.cosDeg(angle + angleStep), - b * MathUtil.sinDeg(angle + angleStep))));
		}
	}
	
	/** Calculates the position of the origin of the room relative to the origin
	 * of the {@link ArchPlan} by adding together all its parent rooms' origins. */
	public Vec3 findGlobalPosition() {
		Vec3 pos = new Vec3(getOrigin());
		Room room = this;
		while (room.getParent() != null) {
			room = room.getParent();
			pos.addLocal(room.getOrigin());
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

	public boolean hasCeiling() {
		return hasCeiling;
	}

	public Room setHasCeiling(boolean hasCeiling) {
		this.hasCeiling = hasCeiling;
		return this;
	}

	public boolean hasFloor() {
		return hasFloor;
	}

	public Room setHasFloor(boolean hasFloor) {
		this.hasFloor = hasFloor;
		return this;
	}

	public String getType() {
		return type;
	}

	public Room setType(String type) {
		this.type = type;
		return this;
	}
}
