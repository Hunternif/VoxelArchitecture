package hunternif.voxarch.plan.gate;

import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Hatch;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.util.Box;
import hunternif.voxarch.vector.Matrix2;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

/**
 * The gate created is vertical, aligned to rotation of the 2nd room.
 * The horizontal position and size of the gate corresponding to the bounding
 * box of the 1st room.
 * The vertical position of the gate is between the ceiling of the lower of the
 * 2 rooms and the floor of the higher. In ambiguous cases the highest possible
 * position will be used.
 * The rotation of the 1st room is disregarded, as it is assumed to be equal to
 * rotation as the 2nd room.
 * The gate cannot extend beyond the bounding box of the 2nd room.
 * @author Hunternif
 */
public class AlignedVerGateFactory {
	public Hatch create(Room from, Room to) {
		// Find out which of the rooms is above. Do so by comparing the
		// difference between one room's floor and the other's ceiling level:
		Box box1 = from.getBoundingBox();
		Box box2 = to.getBoundingBox();
		double floor1ToCeil2 = box2.maxY - box1.minY;
		double floor2ToCeil1 = box1.maxY - box2.minY;
		double y = Math.abs(floor1ToCeil2) < Math.abs(floor2ToCeil1) ?
				box1.minY + floor1ToCeil2/2 :
				box2.minY + floor2ToCeil1/2;
		
		// Find horizontal origin of the 1st room relative to the 2nd:
		Vec2 ro1 = new Vec2(from.getOrigin().x, from.getOrigin().z);
		ro1.subtractLocal(to.getOrigin().x, to.getOrigin().z);
		Matrix2.rotationMatrix(to.getRotationY()).multiplyLocal(ro1);
		
		// Update box1 with the relative origin. Using 0 floor level, because
		// we're only interested in their horizontal intersection:
		Box box1rel2 = new Box(new Vec3(ro1.x, 0, ro1.y), from.getSize());
		// box2 relative "to itself":
		Box box2rel2 = new Box(new Vec3(0, 0, 0), to.getSize());
		Box isn = Box.intersect(box1rel2, box2rel2);
		if (isn == null) { // Not intersecting
			return null;
		}
		// Gate origin relative to the 2nd room:
		Vec2 rg = new Vec2((isn.minX + isn.maxX)/2, (isn.minZ + isn.maxZ)/2);
		Matrix2.rotationMatrix(-to.getRotationY()).multiplyLocal(rg);
		Vec3 position = new Vec3(to.getOrigin().x + rg.x, y, to.getOrigin().z + rg.y);
		Vec2 size = new Vec2(isn.maxX - isn.minX, isn.maxZ - isn.minZ);

		Hatch gate = new Hatch(to.getParent(), from, to, position, size, to.getRotationY());
		return gate;
	}
}
