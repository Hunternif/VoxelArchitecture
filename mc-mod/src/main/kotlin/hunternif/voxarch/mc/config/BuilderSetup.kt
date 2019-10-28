package hunternif.voxarch.mc.config

import hunternif.voxarch.builder.*
import hunternif.voxarch.mc.ExtBlockDataMC
import hunternif.voxarch.plan.Node
import hunternif.voxarch.sandbox.TorchStandBuilder
import net.minecraft.init.Blocks

val defaultContext = BuildContext().apply {
    materials.apply {
        set(MaterialConfig.FLOOR) { ExtBlockDataMC(Blocks.stone) }
        set(MaterialConfig.WALL) { ExtBlockDataMC(Blocks.cobblestone) }
        set(MaterialConfig.ROOF) { ExtBlockDataMC(Blocks.log) }
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

const val TORCH_STAND = "torchStand"