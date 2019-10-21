package hunternif.voxarch.plan.gate;

import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Node;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.util.RoomUtil;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

/**
 * The gate assumes position and size of closest wall of the 2nd room.
 * The gate is placed within the lowest common parent of the rooms, if it exists.
 * @author Hunternif
 */
public class WholeWallHorGateFactory implements IGateFactory {

	@Override
	public Gate create(Room from, Room to) {
		RoomUtil roomUtil = new RoomUtil();
		Wall wall = roomUtil.findClosestWall(to, new Vec2(from.getOrigin().x, from.getOrigin().z));
		if (wall == null) return null;
		Node parent = roomUtil.findLowestCommonParent(from, to);
		
		// Find position of the wall:
		Vec3 wallCenter = new Vec3((wall.getP1().x + wall.getP2().x)/2, 0, (wall.getP1().y + wall.getP2().y)/2);
		Vec3 origin = roomUtil.translateToParent(to, wallCenter);
		
		Vec2 size = new Vec2(wall.getLength(), wall.getHeight());
		
		// Find angle
		//TODO: make sure the direction of the gate is correct
		Vec3 p1 = roomUtil.translateToParent(to, new Vec3(wall.getP1().x, 0, wall.getP1().y));
		Vec3 p2 = roomUtil.translateToParent(to, new Vec3(wall.getP2().x, 0, wall.getP2().y));
		double angle = Math.atan2(-(p2.z - p1.z), p2.x - p1.x) * 180 / Math.PI;
		
		Gate gate = new Gate(parent, from, to, origin, size, angle);
		return gate;
	}

}