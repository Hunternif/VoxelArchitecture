package hunternif.voxarch.plan.gate;

import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Room;

/**
 * Creates gates between rooms.
 * @author Hunternif
 */
public interface IGateFactory {
	/**
	 * Determine size, position and rotation of the gate between the 2 rooms.
	 * The 2nd room should be treated as the "base", the 1st room serving as
	 * auxiliary attachment to it.
	 * The gate is positioned relative to the parent of the rooms (assuming that
	 * both rooms share the same parent or none).
	 */
	Gate create(Room from, Room to);
}
