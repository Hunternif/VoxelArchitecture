package hunternif.voxarch.util;

import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.vector.Matrix2;
import hunternif.voxarch.vector.Vec2;

public class RoomUtil {
	/**
	 * Returns the wall in the room that is closest to the specified point.
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
			Vec2 wallVector = new Vec2(wall.getP2().x - wall.getP1().x,
									   wall.getP2().y - wall.getP1().y);
			double t = new Vec2(p).subtract(wall.getP1()).dotProduct(wallVector) /
					wallVector.dotProduct(wallVector);
			// We're measuring the distance to a line segment defined by wallVector,
			// therefore we constrain the how far the orthogonally projected point
			// can go along the line.
			if (t < 0) t = 0;
			if (t > 1) t = 1;
			Vec2 dest = new Vec2(wall.getP1()).add(wallVector.multiply(t));
			double curDistance = p.squareDistanceTo(dest);
			if (curDistance < distance) {
				distance = curDistance;
				closest = wall;
			}
		}
		return closest;
	}
}
