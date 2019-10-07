package hunternif.voxarch.gen.impl;

import hunternif.voxarch.gen.ElementGenerator;
import hunternif.voxarch.gen.Materials;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.util.MathUtil;
import hunternif.voxarch.vector.Vec2;

/**
 * Creates a simple flat floor made of one single material.
 * @author Hunternif
 */
public class SimpleFloorGenerator implements ElementGenerator.Floor {

	@Override
	public void generateFloor(IBlockStorage dest, Vec2 size, Materials materials) {
		int width = MathUtil.ceiling(size.x);
		int length = MathUtil.ceiling(size.y);
		BlockData block = materials.floorBlocks()[0];
		// Including boundaries (<=) because a room occupies size + 1 blocks.
		for (int x = 0; x <= width; x++) {
			for (int z = 0; z <= length; z++) {
				dest.setBlock(x, 0, z, block);
			}
		}
	}

}
