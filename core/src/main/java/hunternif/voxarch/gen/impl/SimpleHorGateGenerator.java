package hunternif.voxarch.gen.impl;

import hunternif.voxarch.gen.ElementGenerator;
import hunternif.voxarch.gen.Materials;
import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.util.MathUtil;

/**
 * Removes any blocks from where the gate is supposed to go. Digs a little
 * tunnel along the Z axis, in case of really thick walls or other sort of
 * obstacle obscuring the passage.
 * @author Hunternif
 */
public class SimpleHorGateGenerator implements ElementGenerator.HorGate {

	public int minWidth = 1;
	public int minHeight = 2;
	/** Passage will be cleared this far along the Z axis in both directions. */
	public int clearance = 1;
	
	@Override
	public void generateGate(IBlockStorage dest, Gate gate, Materials materials) {
		int height = Math.max(minHeight, MathUtil.roundDown(gate.getSize().y));
		int width = Math.max(minWidth, MathUtil.roundDown(gate.getSize().x));
		// Including boundaries (<=) because a room occupies size + 1 blocks.
		// Offset of 1 from both boundaries because a the width of the gate spans
		// all available space, including where the walls, floor and ceiling of
		// the room are.
		for (int x = 1; x < width; x++) { // <= width - 1
			for (int y = 1; y < height; y++) { // <= height - 1
				for (int z = -clearance; z <= clearance; z++) {
					dest.clearBlock(x, y, z);
				}
			}
		}
	}

}
