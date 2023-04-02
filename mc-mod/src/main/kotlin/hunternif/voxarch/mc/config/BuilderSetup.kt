package hunternif.voxarch.mc.config

import hunternif.voxarch.builder.*
import hunternif.voxarch.plan.Prop
import hunternif.voxarch.mc.ExtBlockDataMC
import hunternif.voxarch.mc.MCEnvironment
import net.minecraft.block.Blocks

val defaultContext get() = BuildContext(MCEnvironment).apply {
    materials.apply {
        set(MAT_FLOOR) { ExtBlockDataMC(Blocks.STONE) }
        set(MAT_WALL) {
            arrayOf(
                ExtBlockDataMC(Blocks.COBBLESTONE),
                ExtBlockDataMC(Blocks.STONE)
            ).random()
        }
        set(MAT_WALL_DECORATION) { ExtBlockDataMC(Blocks.STONE_BRICKS) }
        set(MAT_ROOF) { ExtBlockDataMC(Blocks.DARK_OAK_PLANKS) }
        set(MAT_TORCH) { ExtBlockDataMC(Blocks.TORCH) }
        set(MAT_POST) { ExtBlockDataMC(Blocks.OAK_FENCE) }
    }
    builders.apply {
        setDefaultBuilders()
        set<Prop>(TORCH_STAND to DefaultBuilders.TorchStand)
    }
}

const val TORCH_STAND = "torchStand"