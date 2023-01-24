package hunternif.voxarch.plan;

import hunternif.voxarch.util.RoomUtil;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

import java.util.*;

/**
 * A corridor consisting of multiple straight segments that follow a path
 * defined by a sequence of point coordinates. The points are then connected
 * via straight line segments represented by separate {@link Room}s, with gates
 * at the start and the end.
 * @author Hunternif
 */
public class Corridor extends Structure {

	protected final LinkedList<Vec3> path = new LinkedList<>();
	
	/** Left (or right) corners of the walls enveloping the path. */
	protected Vec3[] envelopeLeft, envelopeRight;
	
	private Room startRoom, endRoom;
	private boolean gateAtStart = false, gateAtEnd = false;
	
	private final Vec2 sectionSize;
	
	private boolean childrenHaveCeiling = true, childrenHaveFloor = true;

	/**
	 * @param origin is the starting point of the corridor. It is assumed to be
	 * 				lying directly on a wall of the starting room (if any).
	 * @param sectionSize is (width, height) of the corridor's cross-section.
	 */
	public Corridor(Node parent, Vec3 origin, Vec2 sectionSize) {
		this.setParent(parent);
		this.setOrigin(origin);
		this.sectionSize = sectionSize;
		// Should we calculate total room size?
		appendPoint(Vec3.ZERO); // The origin as first point
	}

	public List<Room> getRooms() {
		List<Room> rooms = new ArrayList<>();
		for (Node node : getChildren()) {
			if (Room.class.isAssignableFrom(node.getClass())) {
				rooms.add((Room) node);
			}
		}
		return rooms;
	}
	
	/**
	 * Attaches the corridor to this room's closest wall, optionally creates
	 * a gate in place of the attached wall (to save on calculations later).
	 * Internally it inserts an additional point in order to make sure the
	 * corridor starts perpendicularly to the wall.
	 * The first point of the path is assumed to already be lying a wall of
	 * this room.
	 */
	public void attachStartTo(Room startRoom, boolean createGate) {
		this.startRoom = startRoom;
		this.gateAtStart = createGate;
	}
	
	/**
	 * See {@link #attachStartTo(Room, boolean)}.
	 * The <em>last</em> point of the path is assumed to already be lying a wall
	 * of this room.
	 */
	public void attachEndTo(Room endRoom, boolean createGate) {
		this.endRoom = endRoom;
		this.gateAtEnd = createGate;
	}
	
	/** Vector will be copied. */
	public void appendPoint(Vec3 point) {
		path.addLast(new Vec3(point));
	}

	/**
	 * Connect the points via straight line segments represented by separate
	 * {@link Room}s, create walls for them, and create gates at the start and
	 * the end.
	 * If 2 points are at different Y levels, the segment between them will be
	 * a flight of stairs.
	 */
	public void build() {
		if (path.size() < 2) return; // Too few points.
		// Insert points to make sure the corridor is perpendicular to the
		// attached rooms:
		if (startRoom != null) {
			Vec3 first = path.getFirst();
			Vec3 second = path.get(1);
			Vec3 pointOnStart = findPointOnNormalToWall(startRoom, first, second, gateAtStart);
			if (pointOnStart != null && !(pointOnStart.equals(first) || pointOnStart.equals(second))) {
				path.add(1, pointOnStart);
			}
		}
		if (endRoom != null) {
			Vec3 last = path.getLast();
			Vec3 nextToLast = path.get(path.size()-2);
			Vec3 pointOnEnd = findPointOnNormalToWall(endRoom, last, nextToLast, gateAtEnd);
			if (pointOnEnd != null && !(pointOnEnd.equals(last) || pointOnEnd.equals(nextToLast))) {
				path.add(path.size() - 1, pointOnEnd);
			}
		}
		
		buildEnvelopes();

		RoomUtil roomUtil = new RoomUtil();
		Iterator<Vec3> pathIter = path.iterator();
		Vec3 e = null;
		Vec3 f = pathIter.next();
		for (int i = 0; i < path.size() - 1; i++) {
			/* Iteration segment:
			 * end
			 * a f d
			 *   x - origin
			 * b e c
			 * start
			 */
			Vec3 a = envelopeLeft[i+1];
			Vec3 b = envelopeLeft[i];
			Vec3 c = envelopeRight[i];
			Vec3 d = envelopeRight[i+1];
			e = f;
			f = pathIter.next();
			/* Room orientation:
			 * -----------> X
			 * start   end
			 */
			Vec3 origin = new Vec3((e.x + f.x)/2, (e.y + f.y)/2, (e.z + f.z)/2);
			Vec3 size = new Vec3(0, sectionSize.y, sectionSize.x); // Need to update length later!
			double angle = Math.atan2(-(f.z - e.z), f.x - e.x) * 180 / Math.PI;
			Room room = new Room(this, origin, size, angle);
			//TODO: insert stairs where the floor has a slope
			// Insert the walls:
			Vec2 a2 = Vec2.fromXZ(roomUtil.translateToLocal(room, a));
			Vec2 b2 = Vec2.fromXZ(roomUtil.translateToLocal(room, b));
			Vec2 c2 = Vec2.fromXZ(roomUtil.translateToLocal(room, c));
			Vec2 d2 = Vec2.fromXZ(roomUtil.translateToLocal(room, d));
			room.addChild(new Wall(room, d2, a2, true));
			room.addChild(new Wall(room, a2, b2, false));
			room.addChild(new Wall(room, b2, c2, true));
			room.addChild(new Wall(room, c2, d2, false));
			// Update length (it's called "width" because it's along the X axis):
			room.setWidth(Math.max(Math.max(Math.abs(a2.x), Math.abs(b2.x)),
					Math.max(Math.abs(c2.x), Math.abs(d2.x))) * 2);
			room.getStart().addLocal(-room.getWidth()/2, 0, 0);
			// This way to calculate origin-vs-size is straight-forward, but
			// it causes the room volume to extend past its walls in case of
			// sharp turns.
			room.setHasCeiling(childrenHaveCeiling);
			room.setHasFloor(childrenHaveFloor);
			this.addChild(room);
		}
	}
	
	/**
	 * Helper method used to make the start and end of the corridor orthogonal
	 * to the closest wall of a given room.
	 * WARNING: not really mathematically accurate yet!
	 * 
	 * @param room		the room to which to connect.
	 * @param first		end point on the path closest to the room.
	 * @param second	point on the path after the first.
	 * @param addGate	whether to create a gate at the wall.
	 * @return point to be inserted between the two given points, in local
	 * 			coordinates
	 */
	protected Vec3 findPointOnNormalToWall(Room room, Vec3 first, Vec3 second, boolean addGate) {
		RoomUtil roomUtil = new RoomUtil();
		// Translate the point into room coordinates to find the closest wall:
		Vec3 firstInRoom = roomUtil.translateToRoom(this, first, room.getParent());
		Wall wall = roomUtil.findClosestWall(room, Vec2.fromXZ(firstInRoom));
		if (wall == null) return null;
		// Translate coordinates back inside this corridor:
		Vec3 p1 = roomUtil.translateToRoom(room, Vec3.fromXZ(wall.getP1()), this);
		Vec3 p2 = roomUtil.translateToRoom(room, Vec3.fromXZ(wall.getP2()), this);
		// Vector parallel the wall, spanning corridor (gate) width:
		Vec3 p = p2.subtract(p1).normalizeLocal().multiplyLocal(sectionSize.x);
		// Normal to the wall:
		Vec3 n = p2.subtract(p1).crossProduct(Vec3.UNIT_Y).normalizeLocal();
		// Vector from point 0 to point 1 on the path:
		Vec3 d = second.subtract(first);
		d.y = first.y; // Make it flat
		// The following solution was obtained by solving a vector equation:
		double d_n = d.dotProduct(n);
		double D = d_n*d_n + 2*p.dotProduct(d); // Discriminant
		double t = 0;
		// CHEAT: if D < 0, assume that a 90 degree angle is the maximum:
		if (D < 0) {
			t = sectionSize.x/2;
		} else {
			t = 0.5*(d_n + Math.sqrt(D));
		}
		Vec3 newPoint = first.add(n.multiplyLocal(t));
		
		if (addGate) {
			Vec3 gateOrigin = roomUtil.translateToParent(this, first);
			p1 = roomUtil.translateToParent(this, p1);
			p2 = roomUtil.translateToParent(this, p2);
			double angle = Math.atan2(-(p2.z - p1.z), p2.x - p1.x) * 180 / Math.PI;
			// Not sure about which way the gate is oriented :/
			Gate gate = new Gate(this.getParent(), room, this, gateOrigin, sectionSize, angle);
			this.getParent().addChild(gate);
		}
		return newPoint;
	}
	
	/** Build enveloping paths for left and right walls. The results populate
	 * the arrays {@link #envelopeLeft} and {@link #envelopeRight}. */
	protected void buildEnvelopes() {
		envelopeLeft = new Vec3[path.size()];
		envelopeRight = new Vec3[path.size()];
		// Process first and last points:
		// First:
		Vec3 wallVec = path.get(1).subtract(path.getFirst()).crossProduct(Vec3.UNIT_Y)
				.normalizeLocal().multiplyLocal(-sectionSize.x/2);
		envelopeLeft[0] = path.getFirst().add(wallVec);
		envelopeRight[0] = path.getFirst().subtract(wallVec);
		// Last:
		wallVec = path.get(path.size()-2).subtract(path.getLast()).crossProduct(Vec3.UNIT_Y)
				.normalizeLocal().multiplyLocal(-sectionSize.x/2);
		envelopeLeft[path.size()-1] = path.getLast().subtract(wallVec);
		envelopeRight[path.size()-1] = path.getLast().add(wallVec);
		// Process in-between points:
		if (path.size() < 3) return;
		ListIterator<Vec3> pathIter = path.listIterator(0);
		// Consecutive points on the path: start-...-a-b-c-...-end
		Vec3 a = pathIter.next();
		Vec3 b = pathIter.next();
		Vec3 c = null;
		for (int i = 1; i < path.size() - 1; i++) {
			c = pathIter.next();
			
			Vec3 cb_norm = b.subtract(c);
			boolean turning_right = a.subtract(b).crossProduct(cb_norm).y < 0;
			cb_norm.y = 0;
			cb_norm.normalizeLocal();
			double ab2 = (a.x - b.x)*(a.x - b.x) + (a.z - b.z)*(a.z - b.z);
			double ab = Math.sqrt(ab2);
			Vec3 d = b.add( cb_norm.multiply(-ab) );
			double ad2 = (a.x - d.x)*(a.x - d.x) + (a.z - d.z)*(a.z - d.z);
			double part = sectionSize.x/2/Math.sqrt(ad2) * Math.sqrt(4*ab2-ad2);
			if (!turning_right) part *= -1;
			double bd_left = ab + part;
			double bd_right = ab - part;
			Vec3 wallVecC = b.subtract(c).crossProduct(Vec3.UNIT_Y).normalizeLocal().multiplyLocal(-sectionSize.x/2);
			envelopeLeft[i] = d.subtract(wallVecC).add( cb_norm.multiply(bd_left) );
			envelopeRight[i] = d.add(wallVecC).add( cb_norm.multiply(bd_right) );
			
			a = b;
			b = c;
		}
	}

	/** Will modify the inner rooms' ceiling flag. */
	public void setHasCeiling(boolean hasCeiling) {
		childrenHaveCeiling = hasCeiling;
		for (Room child : getRooms()) {
			child.setHasCeiling(hasCeiling);
		}
	}
	public boolean getHasCeiling() {
		return childrenHaveCeiling;
	}

	/** Will modify the inner rooms' floor flag. */
	public void setHasFloor(boolean hasFloor) {
		childrenHaveFloor = hasFloor;
		for (Room child : getRooms()) {
			child.setHasFloor(hasFloor);
		}
	}
	public boolean getHasFloor() {
		return childrenHaveFloor;
	}
}
