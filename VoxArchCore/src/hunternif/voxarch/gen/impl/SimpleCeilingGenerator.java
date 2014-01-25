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
		int halfWidth = MathUtil.ceiling(size.x/2);
		int halfLength = MathUtil.ceiling(size.y/2);
		BlockData block = materials.ceilingBlocks()[0];
		for (int x = -halfWidth; x < halfWidth; x++) {
			for (int z = -halfLength; z < halfLength; z++) {
				dest.setBlock(x, 0, z, block);
			}
		}
	}

}
