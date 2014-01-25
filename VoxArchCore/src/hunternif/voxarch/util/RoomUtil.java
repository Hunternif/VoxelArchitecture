package hunternif.voxarch.util;

import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.vector.Matrix2;
import hunternif.voxarch.vector.Vec2;

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
		Matrix2 rot = Matrix2.rotationMatrix(room.getRotationY());
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
}
