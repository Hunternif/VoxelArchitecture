package hunternif.voxarch.gen;

import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.storage.IBlockStorage;

public interface VerGateGenerator {
	void generateVerGate(IBlockStorage dest, Gate gate, Materials materials);
}
