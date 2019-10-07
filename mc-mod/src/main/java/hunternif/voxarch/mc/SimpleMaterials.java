package hunternif.voxarch.mc;

import hunternif.voxarch.gen.Materials;
import hunternif.voxarch.storage.BlockData;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Blocks;

public class SimpleMaterials implements Materials {

	private final Map<String, BlockData> oneBlockProps = new HashMap<String, BlockData>();
	
	public SimpleMaterials() {
		oneBlockProps.put("torch", new ExtBlockDataMC(Blocks.torch));
	}
	
	@Override
	public BlockData[] floorBlocks() {
		return new BlockData[]{new ExtBlockDataMC(Blocks.cobblestone)};
	}

	@Override
	public BlockData[] ceilingBlocks() {
		return new BlockData[]{new ExtBlockDataMC(Blocks.log)};
	}

	@Override
	public BlockData[] wallBlocks() {
		return new BlockData[]{new ExtBlockDataMC(Blocks.planks)};
	}

	@Override
	public BlockData[] gateBlocks() {
		// 2 means the log is aligned to the Z axis:
		return new BlockData[]{new ExtBlockDataMC(Blocks.log, 2)};
	}

	@Override
	public BlockData[] stairsBlocks(double slope) {
		if (slope < 0.5) {
			// 3 means cobblestone:
			return new BlockData[]{new ExtBlockDataMC(Blocks.stone_slab, 3)};
		} else {
			return new BlockData[]{new ExtBlockDataMC(Blocks.stone_stairs)};
		}
	}

	@Override
	public BlockData oneBlockProp(String name) {
		return oneBlockProps.get(name);
	}

}
