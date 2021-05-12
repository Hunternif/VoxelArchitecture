package hunternif.voxarch.mc.config

import hunternif.voxarch.builder.*
import hunternif.voxarch.mc.ExtBlockDataMC
import hunternif.voxarch.mc.MCEnvironment
import hunternif.voxarch.plan.Node
import hunternif.voxarch.sandbox.TorchStandBuilder
import hunternif.voxarch.sandbox.castle.*
import net.minecraft.block.BlockPlanks
import net.minecraft.init.Blocks
import kotlin.random.Random

val defaultContext = BuildContext(MCEnvironment).apply {
    materials.apply {
        set(MAT_FLOOR) { ExtBlockDataMC(Blocks.stone) }
        set(MAT_WALL) {
            arrayOf(
                ExtBlockDataMC(Blocks.cobblestone),
                ExtBlockDataMC(Blocks.stone)
            ).random()
        }
        set(MAT_WALL_DECORATION) { ExtBlockDataMC(Blocks.stonebrick) }
        set(MAT_ROOF) { ExtBlockDataMC(Blocks.planks, BlockPlanks.EnumType.DARK_OAK.metadata) }
        set(MAT_TORCH) { ExtBlockDataMC(Blocks.torch) }
        set(MAT_POST) { ExtBlockDataMC(Blocks.oak_fence) }
    }
    builders.apply {
        setDefault(SimpleFloorBuilder(MAT_FLOOR))
        setDefault(SimpleWallBuilder(MAT_WALL))
        setDefault(RoomBuilder())
        setDefault(SimpleGateBuilder())
        setDefault(SimpleHatchBuilder())
        setDefault<Node>(Builder())
        set(TORCH_STAND to TorchStandBuilder())
    }
}

const val TORCH_STAND = "torchStand"