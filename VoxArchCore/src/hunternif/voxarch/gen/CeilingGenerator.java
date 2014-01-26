package hunternif.voxarch.gen;

import hunternif.voxarch.util.RoomConstrainedStorage;
import hunternif.voxarch.vector.Vec2;

public interface CeilingGenerator {
	/** The origin is assumed to be in the corner of the room at ceiling height. */
	void generateCeiling(RoomConstrainedStorage dest, Vec2 size, Materials materials);
}
