package hunternif.voxarch.mc;

import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.util.BlockOrientation;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.google.common.collect.EnumBiMap;

/**
 * Adapter between Minecraft World and IBlockStorage. Note that BlockOrientation
 * is transformed into metadata in setBlock(), but the reverse doesn't happen in
 * getBlock().
 * 
 * @author Hunternif
 */
public class MCWorld implements IBlockStorage {

	/**
	 * Bridges Forge block rotation enum with my custom one. Used to apply
	 * correct metadata when pasting rotated blocks into the world.
	 */
	private static final EnumBiMap<BlockOrientation, EnumFacing> forgeOrientMap = EnumBiMap.create(BlockOrientation.class, EnumFacing.class);
	static {
		forgeOrientMap.put(BlockOrientation.SOUTH, EnumFacing.SOUTH);
		forgeOrientMap.put(BlockOrientation.NORTH, EnumFacing.NORTH);
		forgeOrientMap.put(BlockOrientation.EAST, EnumFacing.EAST);
		forgeOrientMap.put(BlockOrientation.WEST, EnumFacing.WEST);
	}
	
	//Stole this from BlockAnvil etc.
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	/**
	 * BlockData that is returned in every call, to keep the memory overhead at
	 * the very minimum.
	 */
	private final BlockData reusableData = new BlockData(0);

	private final World world;

	public MCWorld(World world) {
		this.world = world;
	}

	@Override
	public BlockData getBlock(int x, int y, int z) {
		IBlockState state = world.getBlockState(new BlockPos(x, y, z));
		reusableData.setId(Block.getIdFromBlock(state.getBlock()));
		if (reusableData.getId() == 0)
			return null;
		reusableData.setMetadata(state.getBlock().getMetaFromState(state));
		if (state.getProperties().containsKey(FACING)) { // Block is rotate-able
			EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
			reusableData.setOrientaion(forgeOrientMap.inverse().get(enumfacing));
		}
		return reusableData;
	}

	@Override
	public void setBlock(int x, int y, int z, BlockData block) {
		if (block instanceof ExtBlockDataMC) {
			((ExtBlockDataMC) block).onPasteIntoWorld(world, x, y, z);
		}
		Block mcBlock = Block.getBlockById(block.getId());
		//TODO: make sure rotation is applied correctly for particular kinds of blocks, i.e. Portals
		IBlockState state = mcBlock.getDefaultState();
		if (block.hasOrientation() && state.getPropertyNames().contains(FACING)) {
			state = state.withProperty(FACING, forgeOrientMap.get(block.getOrientaion()));
		}
		// Flag 2 will send the change to clients, but won't cause an immediate block update
		world.setBlockState(new BlockPos(x, y, z), state, 2);
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		world.setBlockState(new BlockPos(x, y, z), Blocks.air.getDefaultState(), 2);
	}

}
