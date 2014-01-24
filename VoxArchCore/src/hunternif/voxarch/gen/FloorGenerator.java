package hunternif.voxarch.gen;

import hunternif.voxarch.util.RoomConstrainedStorage;
import hunternif.voxarch.vector.Vec2;

public interface FloorGenerator {
	void generateFloor(RoomConstrainedStorage dest, Vec2 size, Materials materials);
}
