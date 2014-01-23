package hunternif.voxarch.gen;

import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.storage.IBlockStorage;

public interface HorGateGenerator {
	void generateFloor(IBlockStorage dest, Gate gate, Materials materials);
}
