package hunternif.voxarch.mc.config

import hunternif.voxarch.builder.*
import hunternif.voxarch.mc.ExtBlockDataMC
import hunternif.voxarch.mc.MCEnvironment
import hunternif.voxarch.plan.Node
import hunternif.voxarch.sandbox.TorchStandBuilder
import net.minecraft.block.BlockPlanks
import net.minecraft.init.Blocks
import kotlin.random.Random

val defaultContext = BuildContext(MCEnvironment).apply {
    materials.apply {
        set(MaterialConfig.FLOOR) { ExtBlockDataMC(Blocks.stone) }
        set(MaterialConfig.WALL) {
            listOf(
                ExtBlockDataMC(Blocks.cobblestone),
                ExtBlockDataMC(Blocks.stone)
            ).takeRandom()
        }
        set(MaterialConfig.WALL_DECORATION) { ExtBlockDataMC(Blocks.stonebrick) }
        set(MaterialConfig.ROOF) { ExtBlockDataMC(Blocks.planks, BlockPlanks.EnumType.DARK_OAK.metadata) }
        set(MaterialConfig.TORCH) { ExtBlockDataMC(Blocks.torch) }
        set(MaterialConfig.POST) { ExtBlockDataMC(Blocks.oak_fence) }
    }
    builders.apply {
        setDefault(SimpleFloorBuilder(MaterialConfig.FLOOR))
        setDefault(SimpleWallBuilder(MaterialConfig.WALL))
        setDefault(RoomBuilder())
        setDefault(SimpleGateBuilder())
        setDefault(SimpleHatchBuilder())
        setDefault<Node>(Builder())
        set(TORCH_STAND to TorchStandBuilder())
    }
}

private fun <T> List<T>.takeRandom(): T {
    return if (this.size == 1) first()
    else get(Random.nextInt(this.size))
}

const val TORCH_STAND = "torchStand"