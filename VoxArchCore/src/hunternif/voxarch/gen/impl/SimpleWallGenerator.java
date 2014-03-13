package hunternif.voxarch.gen.impl;

import hunternif.voxarch.gen.ElementGenerator;
import hunternif.voxarch.gen.Materials;
import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.util.MathUtil;

/**
 * Creates a straight 1 block wide wall made of one single material.
 * @author Hunternif
 */
public class SimpleWallGenerator implements ElementGenerator.Wall {

	@Override
	public void generateWall(IBlockStorage dest, Wall wall, Materials materials) {
		int length = MathUtil.ceiling(wall.getLength());
		int height = MathUtil.ceiling(wall.getHeight());
		BlockData block = materials.wallBlocks()[0];
		for (int x = 0; x < length; x++) {
			for (int y = 0; y < height; y++) {
				dest.setBlock(x, y, 0, block);
			}
		}
	}

}
