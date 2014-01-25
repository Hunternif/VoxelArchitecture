package hunternif.voxarch.gen;

import hunternif.voxarch.util.RoomConstrainedStorage;
import hunternif.voxarch.vector.Vec2;

public interface FloorGenerator {
	/** The origin is assumed to be in the center of the room at floor height. */
	void generateFloor(RoomConstrainedStorage dest, Vec2 size, Materials materials);
}
