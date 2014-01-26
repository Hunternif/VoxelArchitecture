package hunternif.voxarch.gen.impl;

import hunternif.voxarch.gen.CeilingGenerator;
import hunternif.voxarch.gen.Materials;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.util.MathUtil;
import hunternif.voxarch.util.RoomConstrainedStorage;
import hunternif.voxarch.vector.Vec2;

public class SimpleCeilingGenerator implements CeilingGenerator {

	@Override
	public void generateCeiling(RoomConstrainedStorage dest, Vec2 size, Materials materials) {
		int width = MathUtil.ceiling(size.x);
		int length = MathUtil.ceiling(size.y);
		BlockData block = materials.ceilingBlocks()[0];
		for (int x = 0; x < width; x++) {
			for (int z = 0; z < length; z++) {
				dest.setBlock(x, 0, z, block);
			}
		}
	}

}
