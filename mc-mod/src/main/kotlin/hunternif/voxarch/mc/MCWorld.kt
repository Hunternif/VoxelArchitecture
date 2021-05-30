package hunternif.voxarch.mc

import com.google.common.collect.EnumBiMap
import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.util.Direction
import hunternif.voxarch.world.Environment
import hunternif.voxarch.world.IBlockWorld
import net.minecraft.block.Blocks
import net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE
import net.minecraftforge.registries.ForgeRegistries
import net.minecraft.util.Direction as McDirection

/**
 * Adapter between Minecraft World and IBlockStorage. Note that Direction
 * is transformed into metadata in setBlock(), but the reverse doesn't happen in
 * getBlock().
 *
 * @author Hunternif
 */
class MCWorld(private val world: World) : IBlockWorld {
    private val env: Environment = MCEnvironment

    companion object {
        private val directionMap = EnumBiMap.create(mapOf(
            Direction.EAST to McDirection.EAST,
            Direction.NORTH to McDirection.NORTH,
            Direction.WEST to McDirection.WEST,
            Direction.SOUTH to McDirection.SOUTH
        ))
    }

    override fun getBlock(x: Int, y: Int, z: Int): BlockData {
        val state = world.getBlockState(BlockPos(x, y, z))
        val result = ExtBlockDataMC(state.block)
        if (state.hasProperty(HORIZONTAL_FACING)) { // Block is rotate-able
            val direction = state.getValue(HORIZONTAL_FACING)
            result.orientation = directionMap.inverse()[direction]
        }
        return result
    }

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockData) {
        if (block is ExtBlockDataMC) {
            block.onPasteIntoWorld(world, x, y, z)
        }
        val mcBlock = when(block) {
            is ExtBlockDataMC -> block.block
            else -> ForgeRegistries.BLOCKS.getValue(ResourceLocation(block.key))
                ?: Blocks.AIR
        }
        //TODO: make sure rotation is applied correctly for special blocks, e.g. Portals
        val state = mcBlock.defaultBlockState().apply {
            if (hasProperty(HORIZONTAL_FACING)) {
                block.orientation?.let { directionMap[it] }?.let {
                    setValue(HORIZONTAL_FACING, it)
                }
            }
        }
        // Flag 2 will send the change to clients, but won't cause an immediate block update
        world.setBlock(BlockPos(x, y, z), state, 2)
    }

    override fun clearBlock(x: Int, y: Int, z: Int) {
        world.setBlock(BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2)
    }

    override val maxHeight: Int = world.height

    override fun getHeight(x: Int, z: Int): Int =
        world.getHeight(WORLD_SURFACE, x, z)

    //TODO: try using Heightmap.Type
    override fun getTerrainHeight(x: Int, z: Int): Int {
        var y = getHeight(x, z)
        while (y > 0) {
            val block = getBlock(x, y, z)
            if (env.isTerrain(block)) break else y--
        }
        return y
    }

    override val seaLevel: Int = world.seaLevel
}