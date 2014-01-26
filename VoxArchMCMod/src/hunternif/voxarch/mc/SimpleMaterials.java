package hunternif.voxarch.mc;

import hunternif.voxarch.gen.Materials;
import hunternif.voxarch.storage.BlockData;
import net.minecraft.block.Block;

public class SimpleMaterials implements Materials {

	@Override
	public BlockData[] floorBlocks() {
		return new BlockData[]{new BlockData(Block.cobblestone.blockID)};
	}

	@Override
	public BlockData[] ceilingBlocks() {
		return new BlockData[]{new BlockData(Block.wood.blockID)};
	}

	@Override
	public BlockData[] wallBlocks() {
		return new BlockData[]{new BlockData(Block.planks.blockID)};
	}

	@Override
	public BlockData[] gateBlocks() {
		// 2 means the log is aligned to the Z axis:
		return new BlockData[]{new BlockData(Block.wood.blockID, 2)};
	}

	@Override
	public BlockData[] stairsBlocks(double slope) {
		if (slope < 0.5) {
			// 3 means cobblestone:
			return new BlockData[]{new BlockData(Block.stoneSingleSlab.blockID, 3)};
		} else {
			return new BlockData[]{new BlockData(Block.stairsCobblestone.blockID)};
		}
	}

	@Override
	public BlockData[] decorationBlocks() {
		return new BlockData[]{new BlockData(Block.brick.blockID)};
	}

}
