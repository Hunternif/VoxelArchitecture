package hunternif.voxarch.plan.gate;

import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Room;

/**
 * Creates gates between rooms.
 * @author Hunternif
 */
public interface IGateFactory {
	/** Determine size, position and rotation of the gate between the 2 rooms. */
	Gate create(Room from, Room to);
}
