package hunternif.voxarch.mc;

import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.util.BlockOrientation;

import java.util.EnumMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Adapter between Minecraft World and IBlockStorage. Note that BlockOrientation
 * is transformed into metadata in setBlock(), but the reverse doesn't happen in
 * getBlock().
 * @author Hunternif
 */
public class MCWorld implements IBlockStorage {

	/** Bridges Forge block rotation enum with my custom one. Used to apply
	 * correct metadata when pasting rotated blocks into the world. */
	private static final EnumMap<BlockOrientation, ForgeDirection> forgeOrientMap
			= new EnumMap<BlockOrientation, ForgeDirection>(BlockOrientation.class);
	static {
		forgeOrientMap.put(BlockOrientation.NONE, ForgeDirection.UNKNOWN);
		forgeOrientMap.put(BlockOrientation.SOUTH, ForgeDirection.SOUTH);
		forgeOrientMap.put(BlockOrientation.NORTH, ForgeDirection.NORTH);
		forgeOrientMap.put(BlockOrientation.EAST, ForgeDirection.EAST);
		forgeOrientMap.put(BlockOrientation.WEST, ForgeDirection.WEST);
	}
	
	/** BlockData that is returned in every call, to keep the memory overhead
	 * at the very minimum. */
	private final BlockData reusableData = new BlockData(0);
	
	private final World world;
	
	public MCWorld(World world) {
		this.world = world;
	}
	
	@Override
	public BlockData getBlock(int x, int y, int z) {
		reusableData.setId(Block.getIdFromBlock(world.getBlock(x, y, z)));
		if (reusableData.getId() == 0) return null;
		reusableData.setMetadata(world.getBlockMetadata(x, y, z));
		//TODO: read block rotations
		return reusableData;
	}

	@Override
	public void setBlock(int x, int y, int z, BlockData block) {
		if (block instanceof ExtBlockDataMC) {
			((ExtBlockDataMC) block).onPasteIntoWorld(world, x, y, z);
		}
		Block mcBlock = Block.getBlockById(block.getId());
		// Flag 2 will send the change to clients
		world.setBlock(x, y, z, mcBlock, block.getMetadata(), 2);
		// Apply rotation:
		mcBlock.rotateBlock(world, x, y, z, forgeOrientMap.get(block.getOrientaion()));
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		world.setBlock(x, y, z, Blocks.air, 0, 2);
	}

}
