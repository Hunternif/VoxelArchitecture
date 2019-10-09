package hunternif.voxarch.gen.impl;

import hunternif.voxarch.gen.ElementGenerator;
import hunternif.voxarch.gen.Materials;
import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Hatch;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.util.MathUtil;

/**
 * Removes any blocks from where the gate is supposed to be. Digs a little
 * tunnel along the Y axis, in case of really thick floor/ceiling or other sort
 * of obstacle obscuring the passage.
 * @author Hunternif
 */
public class SimpleVerGateGenerator implements ElementGenerator.Hatch {

	public int minWidth = 1;
	public int minLength = 1;
	/** Passage will be cleared this far along the Y axis in both directions. */
	public int clearance = 1;
	
	@Override
	public void generateGate(IBlockStorage dest, Hatch gate, Materials materials) {
		int width = Math.max(minWidth, MathUtil.roundDown(gate.getSize().x));
		int length = Math.max(minLength, MathUtil.roundDown(gate.getSize().y));
		// Including boundaries (<=) because a room occupies size + 1 blocks.
		// Offset of 1 from both boundaries because a the width and length
		// of the gate span all available space, including where the walls
		// of the room are.
		for (int x = 1; x < width; x++) { // <= width - 1
			for (int z = 1; z < length; z++) { // <= length - 1
				for (int y = -clearance; y <= clearance; y++) {
					dest.clearBlock(x, y, z);
				}
			}
		}
	}

}
