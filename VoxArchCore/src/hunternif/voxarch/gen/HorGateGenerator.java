package hunternif.voxarch.gen;

import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.storage.IBlockStorage;

public interface HorGateGenerator {
	/** The gate is oriented so that the passage is along the Z axis. */
	void generateHorGate(IBlockStorage dest, Gate gate, Materials materials);
}
