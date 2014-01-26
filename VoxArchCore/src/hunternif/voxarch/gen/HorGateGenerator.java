package hunternif.voxarch.gen;

import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.storage.IBlockStorage;

public interface HorGateGenerator extends GateGenerator {
	/** The gate is oriented so that the passage is along the Z axis, the
	 * coordinates have been set up to point to the bottom left corner. */
	void generateGate(IBlockStorage dest, Gate gate, Materials materials);
}
