package hunternif.voxarch.gen.impl;

import hunternif.voxarch.gen.Materials;
import hunternif.voxarch.gen.WallGenerator;
import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.util.MathUtil;

public class SimpleWallGenerator implements WallGenerator {

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
