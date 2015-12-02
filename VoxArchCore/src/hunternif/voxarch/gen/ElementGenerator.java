package hunternif.voxarch.gen;

import hunternif.voxarch.plan.FlightOfStairs;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.util.RoomConstrainedStorage;
import hunternif.voxarch.vector.Vec2;

/** A collection of interfaces for generating various elements of a room.
 * @author Hunternif
 */
public class ElementGenerator {
	public static interface Ceiling {
		/** The origin is assumed to be in the corner of the room at ceiling height. */
		void generateCeiling(RoomConstrainedStorage dest, Vec2 size, Materials materials);
	}
	public static interface Floor {
		/** The origin is assumed to be in the corner of the room at floor height. */
		void generateFloor(RoomConstrainedStorage dest, Vec2 size, Materials materials);
	}
	public static interface Gate {
		void generateGate(IBlockStorage dest, hunternif.voxarch.plan.Gate gate, Materials materials);
	}
	public static interface HorGate extends ElementGenerator.Gate {
		/** The gate is oriented so that the passage is along the Z axis, the
		 * coordinates have been set up to point to the bottom left corner. */
		void generateGate(IBlockStorage dest, hunternif.voxarch.plan.Gate gate, Materials materials);
	}
	public static interface VerGate extends ElementGenerator.Gate {
		/** The coordinates have been set up to point to the bottom left corner. */
		void generateGate(IBlockStorage dest, hunternif.voxarch.plan.Gate gate, Materials materials);
	}
	public static interface Stairs {
		void generateStairs(IBlockStorage dest, FlightOfStairs stairs, Materials materials);
	}
	public static interface Wall {
		/** The wall runs along the X axis. */
		void generateWall(IBlockStorage dest, hunternif.voxarch.plan.Wall wall, Materials materials);
	}
	public static interface Prop {
		/** A fixed-size prop, i.e. a statue or a simple torch. */
		void generateProp(IBlockStorage dest, hunternif.voxarch.plan.Prop prop, Materials materials);
	}
}
