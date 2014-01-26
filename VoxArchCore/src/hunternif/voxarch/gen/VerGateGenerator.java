package hunternif.voxarch.gen;

import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.storage.IBlockStorage;

public interface VerGateGenerator extends GateGenerator {
	/** The coordinates have been set up to point to the bottom left corner. */
	void generateGate(IBlockStorage dest, Gate gate, Materials materials);
}
