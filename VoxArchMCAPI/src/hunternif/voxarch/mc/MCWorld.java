package hunternif.voxarch.mc;

import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import net.minecraft.world.World;

/** Adapter between Minecraft World and IBlockStorage. */
public class MCWorld implements IBlockStorage {

	/** BlockData that is returned in every call, to keep the memory overhead
	 * at the very minimum. */
	private final BlockData reusableData = new BlockData(0);
	
	private final World world;
	
	public MCWorld(World world) {
		this.world = world;
	}
	
	@Override
	public BlockData getBlock(int x, int y, int z) {
		reusableData.setId(world.getBlockId(x, y, z));
		if (reusableData.getId() == 0) return null;
		reusableData.setMetadata(world.getBlockMetadata(x, y, z));
		return reusableData;
	}

	@Override
	public void setBlock(int x, int y, int z, BlockData block) {
		// Flag 2 will send the change to clients
		if (block instanceof ExtBlockDataMC) {
			((ExtBlockDataMC)block).onPasteIntoWorld(world, x, y, z);
		} else {
			world.setBlock(x, y, z, block.getId(), block.getMetadata(), 2);
		}
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		world.setBlock(x, y, z, 0, 0, 2);
	}

}
