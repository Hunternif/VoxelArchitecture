package hunternif.voxarch.gen;

import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.storage.IBlockStorage;

public interface GateGenerator {
	void generateGate(IBlockStorage dest, Gate gate, Materials materials);
}
