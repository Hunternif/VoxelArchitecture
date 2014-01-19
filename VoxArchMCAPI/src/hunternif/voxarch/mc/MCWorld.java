package hunternif.voxarch.mc;

import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.util.IntVec3;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.World;

/** Adapter between Minecraft World and IBlockStorage. */
public class MCWorld implements IBlockStorage {

	/** BlockData that is returned in every call, to keep the memory overhead
	 * at the very minimum. */
	private final BlockData reusableData = new BlockData(0);
	
	/** Map in which ExtBlockDataMC is stored, because it can't be retrieved
	 * from the world via block id and metadata. */
	private final Map<IntVec3, ExtBlockDataMC> extBlocks = new HashMap<IntVec3, ExtBlockDataMC>();
	
	private final World world;
	
	public MCWorld(World world) {
		this.world = world;
	}
	
	@Override
	public BlockData getBlock(int x, int y, int z) {
		ExtBlockDataMC extBlock = extBlocks.get(new IntVec3(x, y, z));
		if (extBlock != null) return extBlock;
		reusableData.setId(world.getBlockId(x, y, z));
		if (reusableData.getId() == 0) return null;
		reusableData.setMetadata(world.getBlockMetadata(x, y, z));
		return reusableData;
	}

	@Override
	public void setBlock(int x, int y, int z, BlockData block) {
		// Flag 2 will send the change to clients
		if (block instanceof ExtBlockDataMC) {
			((ExtBlockDataMC) block).onPasteIntoWorld(world, x, y, z);
			extBlocks.put(new IntVec3(x, y, z), (ExtBlockDataMC) block);
		} else {
			world.setBlock(x, y, z, block.getId(), block.getMetadata(), 2);
		}
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		world.setBlock(x, y, z, 0, 0, 2);
	}

}
