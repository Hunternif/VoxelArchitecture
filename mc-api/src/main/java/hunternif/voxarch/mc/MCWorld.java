package hunternif.voxarch.mc;

import com.google.common.collect.EnumBiMap;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.util.Direction;
import hunternif.voxarch.world.Environment;
import hunternif.voxarch.world.IBlockWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE_WG;

/**
 * Adapter between Minecraft World and IBlockStorage. Note that Direction
 * is transformed into metadata in setBlock(), but the reverse doesn't happen in
 * getBlock().
 * 
 * @author Hunternif
 */
public class MCWorld implements IBlockWorld {

	private Environment env = MCEnvironment.INSTANCE;

	private static final EnumBiMap<Direction, net.minecraft.util.Direction> directionMap =
			EnumBiMap.create(Direction.class, net.minecraft.util.Direction.class);
	static {
		directionMap.put(Direction.EAST, net.minecraft.util.Direction.EAST);
		directionMap.put(Direction.NORTH, net.minecraft.util.Direction.NORTH);
		directionMap.put(Direction.WEST, net.minecraft.util.Direction.WEST);
		directionMap.put(Direction.SOUTH, net.minecraft.util.Direction.SOUTH);
	}

	private final World world;

	public MCWorld(World world) {
		this.world = world;
	}

	@Override
	public BlockData getBlock(int x, int y, int z) {
		BlockState state = world.getBlockState(new BlockPos(x, y, z));
		ExtBlockDataMC result = new ExtBlockDataMC(state.getBlock());
		if (state.has(HORIZONTAL_FACING)) { // Block is rotate-able
			net.minecraft.util.Direction direction =
					state.get(HORIZONTAL_FACING);
			result.setOrientation(directionMap.inverse().get(direction));
		}
		return result;
	}

	@Override
	public void setBlock(int x, int y, int z, BlockData block) {
		if (block instanceof ExtBlockDataMC) {
			((ExtBlockDataMC) block).onPasteIntoWorld(world, x, y, z);
		}
		Block mcBlock = ForgeRegistries.BLOCKS.getValue(
				new ResourceLocation(block.getKey())
		);
		//TODO: make sure rotation is applied correctly for particular kinds of blocks, i.e. Portals
		BlockState state = mcBlock.getDefaultState();
		if (block.hasOrientation() && state.has(HORIZONTAL_FACING)) {
			state = state.with(HORIZONTAL_FACING, directionMap.get(block.getOrientation()));
		}
		// Flag 2 will send the change to clients, but won't cause an immediate block update
		world.setBlockState(new BlockPos(x, y, z), state, 2);
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		world.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState(), 2);
	}

	@Override
	public int getMaxHeight() {
		return world.getHeight();
	}

	@Override
	public int getHeight(int x, int z) {
		return world.getHeight(WORLD_SURFACE_WG, new BlockPos(x, 0, z)).getY();
	}

	@Override
	public int getTerrainHeight(int x, int z) {
		int y = getHeight(x, z);
		while (y > 0) {
			BlockData block = getBlock(x, y, z);
			if (env.isTerrain(block)) break;
			else y--;
		}
		return y;
	}

	@Override
	public int getSeaLevel() {
		return world.getSeaLevel();
	}
}
