package hunternif.voxarch.gen;

import hunternif.voxarch.util.RoomBlockOutput;
import hunternif.voxarch.vector.Vec2;

public interface CeilingGenerator {
	void generateFloor(RoomBlockOutput dest, Vec2 size, Materials materials);
}
