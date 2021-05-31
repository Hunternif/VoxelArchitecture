package hunternif.voxarch.mc.config

import hunternif.voxarch.builder.*
import hunternif.voxarch.mc.ExtBlockDataMC
import hunternif.voxarch.mc.MCEnvironment
import hunternif.voxarch.plan.Node
import hunternif.voxarch.sandbox.TorchStandBuilder
import hunternif.voxarch.sandbox.castle.*
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