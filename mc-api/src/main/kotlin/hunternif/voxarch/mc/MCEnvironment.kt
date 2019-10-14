package hunternif.voxarch.mc

import hunternif.voxarch.gen.Environment
import net.minecraft.block.*
import net.minecraft.init.Blocks

class MCEnvironment {
    companion object {
        lateinit var environment: Environment
        fun init() {
            val buildThroughBlocks = mutableSetOf<Int>()
            for (block in Block.blockRegistry) {
                if (block is BlockLeaves ||
                    block is BlockLiquid ||
                    block is BlockBush ||
                    block is BlockFire ||
                    block is BlockBush ||
                    block is BlockRedstoneWire ||
                    block is BlockRedstoneRepeater ||
                    block is BlockRedstoneComparator ||
                    block is BlockDaylightDetector ||
                    block is BlockDoublePlant
                ) {
                    buildThroughBlocks.add(Block.getIdFromBlock(block as Block))
                }
            }
            listOf(
                Blocks.air,
                Blocks.sapling,
                Blocks.sponge,
                Blocks.bed,
                Blocks.golden_rail,
                Blocks.detector_rail,
                Blocks.web,
                Blocks.standing_sign,
                Blocks.ladder,
                Blocks.rail,
                Blocks.lever,
                Blocks.stone_pressure_plate,
                Blocks.unlit_redstone_torch,
                Blocks.redstone_torch,
                Blocks.cactus,
                Blocks.reeds,
                Blocks.oak_fence,
                Blocks.spruce_fence,
                Blocks.birch_fence,
                Blocks.jungle_fence,
                Blocks.dark_oak_fence,
                Blocks.acacia_fence,
                Blocks.pumpkin,
                Blocks.cake,
                Blocks.trapdoor,
                Blocks.monster_egg,
                Blocks.melon_block,
                Blocks.pumpkin_stem,
                Blocks.melon_stem,
                Blocks.vine,
                Blocks.oak_fence_gate,
                Blocks.spruce_fence_gate,
                Blocks.birch_fence_gate,
                Blocks.jungle_fence_gate,
                Blocks.dark_oak_fence_gate,
                Blocks.acacia_fence_gate,
                Blocks.stone_slab,
                Blocks.brick_block,
                Blocks.torch,
                Blocks.oak_door,
                Blocks.spruce_door,
                Blocks.birch_door,
                Blocks.jungle_door,
                Blocks.acacia_door,
                Blocks.dark_oak_door,
                Blocks.oak_stairs,
                Blocks.stone_stairs,
                Blocks.wall_sign,
                Blocks.stone_button,
                Blocks.snow_layer,
                Blocks.brick_stairs,
                Blocks.stone_brick_stairs,
                Blocks.waterlily,
                Blocks.wooden_slab,
                Blocks.cocoa,
                Blocks.sandstone_stairs,
                Blocks.tripwire_hook,
                Blocks.tripwire,
                Blocks.flower_pot,
                Blocks.carrots,
                Blocks.potatoes,
                Blocks.wooden_button,
                Blocks.light_weighted_pressure_plate,
                Blocks.heavy_weighted_pressure_plate,
                Blocks.barrier,
                Blocks.iron_trapdoor,
                Blocks.carpet,
                Blocks.acacia_stairs,
                Blocks.dark_oak_stairs,
                Blocks.standing_banner,
                Blocks.stone_slab2
            ).map {
                buildThroughBlocks.add(Block.getIdFromBlock(it))
            }
            environment = Environment(buildThroughBlocks.toList())
        }
    }
}
