package hunternif.voxarch.util;

import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.vector.Matrix2;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

public class RoomUtil {
	/**
	 * Returns the wall in the room that is closest to the specified point on
	 * the horizontal plane.
	 * @param room
	 * @param point coordinates relative to the parent of the room.
	 */
	public static Wall findClosestWall(Room room, Vec2 point) {
		Vec2 p = new Vec2(point.x - room.getOrigin().x,
						  point.y - room.getOrigin().y);
		Matrix2 rot = Matrix2.rotationMatrix(room.getRotationY());
		rot.multiply(p);
		Wall closest = null;
		double distance = Double.MAX_VALUE;
		for (Wall wall : room.getWalls()) {
			Vec2 dest = VectorUtil.closestPointOnLineSegment(p, wall.getP1(), wall.getP2());
			double curDistance = p.squareDistanceTo(dest);
			if (curDistance < distance) {
				distance = curDistance;
				closest = wall;
			}
		}
		return closest;
	}
	
	/**
	 * Traces a ray on the horizontal plane from the start point in the
	 * specified direction until it hits the bounding box of the room, the
	 * room's rotation considered.
	 * @param room		the target room.
	 * @param start		starting point of the ray.
	 * @param target	target point of the ray, i.e. some point on the ray.
	 */
	public static Vec2 rayTrace(Room room, Vec2 start, Vec2 target) {
		// Retrieve local coordinates of the corners of the room's bounding box:
		// (Minus angle, because this is the reference frame XZY is left-handed)
		Matrix2 rot = Matrix2.rotationMatrix(-room.getRotationY());
		Vec2 roomCenter = new Vec2(room.getOrigin().x, room.getOrigin().z);
		double halfWidth = room.getSize().x/2;
		double halfLength = room.getSize().z/2;
		Vec2 v1 = rot.multiply(new Vec2(halfWidth, halfLength)).add(roomCenter);
		Vec2 v2 = rot.multiply(new Vec2(halfWidth, -halfLength)).add(roomCenter);
		Vec2 v3 = rot.multiply(new Vec2(-halfWidth, -halfLength)).add(roomCenter);
		Vec2 v4 = rot.multiply(new Vec2(-halfWidth, halfLength)).add(roomCenter);
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
	
	/**
	 * Creates a horizontal gate between the specified rooms, aligned to the
	 * closest wall of the 2nd room. The rooms are assumed to have the same
	 * parent, and the 1st room must face the 2nd along one of the horizontal
	 * axes (rotation considered). */
	public static Gate createHorGateBetween(Room from, Room to) {
		// Check every direction this room is facing and see if it faces the
		// target room. The angle is inverted because the Y axis represents
		// inverted Z axis.
		Matrix2 rot = Matrix2.rotationMatrix(-from.getRotationY());
		Vec2 roomCenter = new Vec2(from.getOrigin().x, from.getOrigin().z);
		double halfWidth = from.getSize().x/2;
		double halfLength = from.getSize().z/2;
		Vec2 east = rot.multiply(new Vec2(halfWidth, 0)).add(roomCenter);
		Vec2 north = rot.multiply(new Vec2(0, -halfLength)).add(roomCenter);
		Vec2 west = rot.multiply(new Vec2(-halfWidth, 0)).add(roomCenter);
		Vec2 south = rot.multiply(new Vec2(0, halfLength)).add(roomCenter);
		Vec2[] targets = {east, north, west, south};
		
		Vec2 traced = null; // The point on the bounding box of the target room.
		Vec2 target = null;
		for (int i = 0; i < 4; i++) {
			target = targets[i];
			traced = rayTrace(to, roomCenter, targets[i]);
			if (traced != null) break;
		}
		if (traced == null) return null;
		
		// Place the gate in the middle of the the traced and target points:
		Vec2 point = new Vec2((traced.x + target.x)/2, (traced.y + target.y)/2);
		
		double angle = 0;
		Vec2 size = new Vec2(0, 0);
		// Align and resize the gate to the closest wall of the second room:
		Wall wall = findClosestWall(to, point);
		if (wall == null) {
			// The target room has no walls, align to the 1st room:
			wall = findClosestWall(from, point);
		}
		if (wall != null) {
			angle = wall.getAngleDeg() + wall.getRoom().getRotationY();
			size.x = wall.getLength();
		} else {
			// The 1st room doesn't have any walls either. Align and resize to
			// the 1st room's bounding box:
			// (The first argument is inverted because the Y axis represents
			// inverted Z axis. Then the other one is inverted and both are
			// swapped places because we need to find the angle of a wall.)
			angle = Math.atan2(target.x - roomCenter.x, target.y - roomCenter.y) * 180 / Math.PI;
			size.x = Math.sqrt(from.getSize().x*from.getSize().x + from.getSize().z*from.getSize().z -
					4*(target.x-roomCenter.x)*(target.x-roomCenter.x) - 4*(target.y-roomCenter.y)*(target.y-roomCenter.y));
		}
		
		// The y-level of the gate should be between the floors of the rooms,
		// plus 1 to account for the floor:
		Vec3 gatePos = new Vec3(point.x, (from.getOrigin().y + to.getOrigin().y)/2 + 1, point.y); 
		
		// The gate can't be taller than any of the connecting rooms, minus 1 to
		// account for the ceiling:
		size.y = Math.min(from.getOrigin().y + from.getSize().y, to.getOrigin().y + to.getSize().y) - gatePos.y - 1;
		
		Gate gate = new Gate(from.getParent(), from, to, gatePos, size, Gate.Orientation.HORIZONTAL, angle);
		return gate;
	}
	
	//TODO: create vertical gate
}
