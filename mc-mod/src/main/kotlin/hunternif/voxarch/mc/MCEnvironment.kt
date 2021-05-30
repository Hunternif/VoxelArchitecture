package hunternif.voxarch.mc

import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.world.Environment
import net.minecraft.block.*
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries

private val BlockData.mcBlock: Block
    get() = when (this) {
        is ExtBlockDataMC -> this.block
        else -> ForgeRegistries.BLOCKS.getValue(ResourceLocation(key))
            ?: Blocks.AIR
    }

object MCEnvironment : Environment {
    private val buildThroughBlocks = mutableSetOf<Block>()
    private val seaBlocks = mutableSetOf<Block>()

    override fun isTerrain(block: BlockData?): Boolean =
        block?.mcBlock?.let { it in seaBlocks || it !in buildThroughBlocks } ?: false

    override fun shouldBuildThrough(block: BlockData?): Boolean =
        block?.mcBlock in buildThroughBlocks

    init {
        for (block in ForgeRegistries.BLOCKS.values) {
            if (block is FlowingFluidBlock)
                seaBlocks.add(block)

            if (
                // broad categories
                block is FlowingFluidBlock ||
                block is HorizontalBlock ||
                block is HorizontalFaceBlock ||
                // transparent blocks in nature
                block is AirBlock ||
                block is FireBlock ||
                block is SnowBlock ||
                block is BarrierBlock ||
                // plants
                block is LeavesBlock ||
                block is SaplingBlock ||
                block is BushBlock ||
                block is StemBlock ||
                block is SpongeBlock ||
                block is DoublePlantBlock ||
                block is CactusBlock ||
                block is SugarCaneBlock ||
                block is StemGrownBlock ||
                block is VineBlock ||
                // fauna
                block is WebBlock ||
                block is TurtleEggBlock ||
                // decorations
                block is CakeBlock ||
                block is FlowerPotBlock ||
                block is AbstractSignBlock ||
                block is AbstractSkullBlock ||
                block is LanternBlock ||
                block is CampfireBlock ||
                block is CarpetBlock ||
                block is AbstractBannerBlock ||
                // buttons, redstone etc
                block is AbstractButtonBlock ||
                block is AbstractPressurePlateBlock ||
                block is LeverBlock ||
                block is RedstoneWireBlock ||
                block is RedstoneDiodeBlock ||
                block is TripWireBlock ||
                block is TripWireHookBlock ||
                block is DaylightDetectorBlock ||
                block is AbstractRailBlock ||
                // semi-transparent structures
                block is SlabBlock ||
                block is StairsBlock ||
                block is WallBlock ||
                block is FenceBlock ||
                block is FenceGateBlock ||
                block is TorchBlock ||
                block is LadderBlock ||
                block is DoorBlock
            ) {
                buildThroughBlocks.add(block)
            }
            // Tree trunks. Could be building materials!
            listOf(
                Blocks.ACACIA_LOG,
                Blocks.BIRCH_LOG,
                Blocks.DARK_OAK_LOG,
                Blocks.JUNGLE_LOG,
                Blocks.OAK_LOG,
                Blocks.SPRUCE_LOG
            ).map {
                buildThroughBlocks.add(it)
            }
        }
    }
}
