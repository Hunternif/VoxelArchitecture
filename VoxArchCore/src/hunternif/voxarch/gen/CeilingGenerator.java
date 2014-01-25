package hunternif.voxarch.gen;

import hunternif.voxarch.util.RoomConstrainedStorage;
import hunternif.voxarch.vector.Vec2;

public interface CeilingGenerator {
	void generateCeiling(RoomConstrainedStorage dest, Vec2 size, Materials materials);
}
