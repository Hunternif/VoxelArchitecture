package hunternif.voxarch.mc;

import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.util.BlockOrientation;
import hunternif.voxarch.util.IntVec3;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.RotationHelper;

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
			// Apply rotation:
			RotationHelper.rotateVanillaBlock(Block.blocksList[block.getId()],
					world, x, y, z, forgeOrientMap.get(block.getOrientaion()));
		}
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		world.setBlock(x, y, z, 0, 0, 2);
	}

}
