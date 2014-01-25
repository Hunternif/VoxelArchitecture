package hunternif.voxarch.gen.impl;

import hunternif.voxarch.gen.HorGateGenerator;
import hunternif.voxarch.gen.Materials;
import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.util.MathUtil;

public class SimpleHorGateGenerator implements HorGateGenerator {

	public int minWidth = 1;
	public int minHeight = 2;
	
	@Override
	public void generateGate(IBlockStorage dest, Gate gate, Materials materials) {
		int height = Math.max(minHeight, MathUtil.roundDown(gate.getSize().y - 1));
		int width = Math.max(minWidth, MathUtil.roundDown(gate.getSize().x - 2));
		int startX = width/2, endX = width - startX;
		for (int x = startX; x < endX; x++) {
			for (int y = 0; y < height; y++) {
				dest.clearBlock(x, y, 0);
			}
		}
	}

}
