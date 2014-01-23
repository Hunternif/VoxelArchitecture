package hunternif.voxarch.gen;

import hunternif.voxarch.plan.FlightOfStairs;
import hunternif.voxarch.storage.IBlockStorage;

public interface StairsGenerator {
	void generateFloor(IBlockStorage dest, FlightOfStairs stairs, Materials materials);
}
