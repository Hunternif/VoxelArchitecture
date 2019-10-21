package hunternif.voxarch.plan.gate;

import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.util.RoomUtil;
import hunternif.voxarch.vector.Matrix2;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

/**
 * The gate created is horizontal, aligned to the closest wall of the 2nd room.
 * The rooms are assumed to have the same parent, and the 1st room must face the
 * 2nd room along one of the horizontal axes (rotation considered).
 * <p>
 * The size of the gate is the same as the size of the closest wall of the 2nd
 * room. If the 2nd room has no walls, the 1st room's closest wall is used. If
 * neither room has walls, then the 1st room's axis-aligned bounding boxes is
 * used.
 * @author Hunternif
 */
public class WallAlignedHorGateFactory implements IGateFactory {

	@Override
	public Gate create(Room from, Room to) {
		// Check every direction this room is facing and see if it is facing
		// the target room. The angle is inverted because the Y axis represents
		// inverted Z axis.
		Matrix2 rot = Matrix2.rotationMatrix(-from.getRotationY());
		Vec2 room1Center = new Vec2(from.getOrigin().x, from.getOrigin().z);
		double halfWidth = from.getSize().x/2;
		double halfLength = from.getSize().z/2;
		Vec2 east = rot.multiplyLocal(new Vec2(halfWidth, 0)).addLocal(room1Center);
		Vec2 north = rot.multiplyLocal(new Vec2(0, -halfLength)).addLocal(room1Center);
		Vec2 west = rot.multiplyLocal(new Vec2(-halfWidth, 0)).addLocal(room1Center);
		Vec2 south = rot.multiplyLocal(new Vec2(0, halfLength)).addLocal(room1Center);
		Vec2[] targets = {east, north, west, south};
		
		Vec2 traced = null; // The point on the bounding box of the target room.
		Vec2 target = null;
		RoomUtil roomUtil = new RoomUtil();
		for (int i = 0; i < 4; i++) {
			target = targets[i];
			traced = roomUtil.rayTrace(to, room1Center, target);
			if (traced != null) break;
		}
		if (traced == null) return null;
		
		// Place the gate between the traced and target points:
		Vec2 point = new Vec2((traced.x + target.x)/2, (traced.y + target.y)/2);
		
		double angle = 0;
		Vec2 size = new Vec2(0, 0);
		// Align and resize the gate to the closest wall of the second room:
		Wall wall = roomUtil.findClosestWall(to, point);
		if (wall == null) {
			// The target room has no walls, align to the 1st room:
			wall = roomUtil.findClosestWall(from, point);
		}
		if (wall != null) {
			angle = wall.getRotationY() + wall.getParent().getRotationY();
			size.x = wall.getLength();
			//TODO test gate size when walls are present.
		} else {
			// The 1st room doesn't have any walls either. Align and resize to
			// the 1st room's bounding box:
			// (The first argument is inverted because the Y axis replaces
			// an inverted Z axis. Then the other one is inverted and both are
			// swapped places because we need to find the angle of a wall.)
			// --
			// ...what the heck was that supposed to mean?
			angle = Math.atan2(target.x - room1Center.x, target.y - room1Center.y) * 180 / Math.PI;
			size.x = Math.sqrt(from.getSize().x*from.getSize().x + from.getSize().z*from.getSize().z -
					4*(target.x-room1Center.x)*(target.x-room1Center.x) - 4*(target.y-room1Center.y)*(target.y-room1Center.y));
			//TODO test gate size when no walls. Wtf is this math?
		}
		
		// The get can't be lower than any of the rooms' floors:
		Vec3 gatePos = new Vec3(point.x, Math.max(from.getOrigin().y, to.getOrigin().y), point.y); 
		
		// The gate can't be taller than any of the rooms' ceilings:
		size.y = Math.min(from.getOrigin().y + from.getSize().y, to.getOrigin().y + to.getSize().y) - gatePos.y;
		
		Gate gate = new Gate(to.getParent(), from, to, gatePos, size, angle);
		return gate;
	}

}
