package hunternif.voxarch.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.vector.Matrix2;
import hunternif.voxarch.vector.Matrix4;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;
import hunternif.voxarch.vector.Vec4;

/**
 * 
 * @author Hunternif
 *
 */
public class RoomUtil {
	/**
	 * Returns the wall in the room that is closest to the specified point on
	 * the horizontal plane.
	 * In case of several walls being equally close (e.g. when the shortest
	 * distance to 2 walls is measured in their mutual corner), the 'closeness'
	 * is determined by comparing the average distance to their end points.
	 * @param room
	 * @param point coordinates relative to the parent of the room.
	 */
	public static Wall findClosestWall(Room room, Vec2 point) {
		// Check if the room has no walls:
		if (room.getWalls().isEmpty()) return null;
		Vec2 p = new Vec2(point.x - room.getOrigin().x,
						  point.y - room.getOrigin().z);
		Matrix2 rot = Matrix2.rotationMatrix(room.getRotationY());
		rot.multiplyLocal(p);
		double distance = Double.MAX_VALUE;
		// First measure the actual distance:
		List<Wall> candidates = new ArrayList<>();
		for (Wall wall : room.getWalls()) {
			Vec2 dest = VectorUtil.closestPointOnLineSegment(p, wall.getP1(), wall.getP2());
			double curDistance = p.squareDistanceTo(dest);
			if (curDistance == distance) {
				candidates.add(wall);
			} else if (curDistance < distance) {
				distance = curDistance;
				candidates = new ArrayList<>();
				candidates.add(wall);
			}
		}
		if (candidates.size() > 1) {
			// Compare distances to their end points:
			//TODO: test the new improved RoomUtil.findClosestWall()
			Wall closest = null;
			distance = Double.MAX_VALUE;
			for (Wall wall : room.getWalls()) {
				double curDistance = p.squareDistanceTo(wall.getP1()) + p.squareDistanceTo(wall.getP2());
				if (curDistance < distance) {
					distance = curDistance;
					closest = wall;
				}
			}
			// At this point there still might be collisions, but whatever.
			// Your fault for designing a plan that causes such collisions :^)
			return closest;
		} else {
			return candidates.get(0);
		}
	}
	
	/**
	 * Traces a ray on the horizontal plane from the start point in the
	 * specified direction until it hits the bounding box of the room, the
	 * room's rotation considered.
	 * @param room		the target room.
	 * @param start		starting point of the ray.
	 * @param target	target of the ray, i.e. some point on the ray.
	 */
	public static Vec2 rayTrace(Room room, Vec2 start, Vec2 target) {
		// Retrieve local coordinates of the corners of the room's bounding box:
		// (Minus angle, because this is the reference frame XZY is left-handed)
		Matrix2 rot = Matrix2.rotationMatrix(-room.getRotationY());
		Vec2 roomCenter = new Vec2(room.getOrigin().x, room.getOrigin().z);
		double halfWidth = room.getSize().x/2;
		double halfLength = room.getSize().z/2;
		Vec2 v1 = rot.multiplyLocal(new Vec2(halfWidth, halfLength)).addLocal(roomCenter);
		Vec2 v2 = rot.multiplyLocal(new Vec2(halfWidth, -halfLength)).addLocal(roomCenter);
		Vec2 v3 = rot.multiplyLocal(new Vec2(-halfWidth, -halfLength)).addLocal(roomCenter);
		Vec2 v4 = rot.multiplyLocal(new Vec2(-halfWidth, halfLength)).addLocal(roomCenter);
		Vec2[] vertices = {v1, v2, v3, v4, v1};
		
		// Find the closest intersection of the ray with a line segment [vN,vN+1]
		Vec2 intersection = null, vec = null;
		double distance = Double.MAX_VALUE, curDistance = 0;
		for (int i = 0; i < 4; i++) {
			vec = VectorUtil.rayTrace(start, target, vertices[i], vertices[i+1]);
			if (vec != null) {
				curDistance = vec.distanceTo(start);
				if (curDistance < distance) {
					distance = curDistance;
					intersection = vec;
				}
			}
		}
		return intersection;
	}
	
	/** WARNING: sub-optimal algorithm! */
	public static Room findLowestCommonParent(Room roomA, Room roomB) {
		if (roomA == null || roomB == null) return null;
		if (roomA == roomB) return roomA;
		List<Room> ancestryA = new ArrayList<>();
		while (roomA != null) {
			ancestryA.add(roomA);
			roomA = roomA.getParent();
		}
		List<Room> ancestryB = new ArrayList<>();
		while (roomB != null) {
			ancestryB.add(roomB);
			roomB = roomB.getParent();
		}
		ListIterator<Room> iterA = ancestryA.listIterator(ancestryA.size());
		ListIterator<Room> iterB = ancestryB.listIterator(ancestryB.size());
		Room parent = null;
		while (iterA.hasPrevious() && iterB.hasPrevious()) {
			Room child = iterA.previous();
			if (child == iterB.previous()) {
				parent = child;
			} else {
				return parent;
			}
		}
		return parent;
	}
	
	/** Converts the coordinates from local in-room to the room's immediate parent. */
	public static Vec3 translateToParent(Room room, Vec3 local) {
		return Vec3.from(
				Matrix4.rotationY(room.getRotationY()).multiplyLocal(Vec4.from(local))
			).addLocal(room.getOrigin());
		
	}
}
