package hunternif.voxarch.mc.config

import hunternif.voxarch.builder.*
import hunternif.voxarch.mc.ExtBlockDataMC
import hunternif.voxarch.plan.Node
import net.minecraft.init.Blocks

val defaultContext = BuildContext().apply {
    materials.apply {
        set(MaterialConfig.FLOOR) { ExtBlockDataMC(Blocks.cobblestone) }
        set(MaterialConfig.WALL) { ExtBlockDataMC(Blocks.planks) }
        set(MaterialConfig.ROOF) { ExtBlockDataMC(Blocks.log) }
    }
    builders.apply {
        setDefault(SimpleFloorBuilder(MaterialConfig.FLOOR))
        setDefault(SimpleWallBuilder(MaterialConfig.WALL))
        setDefault(RoomBuilder())
        setDefault(SimpleGateBuilder())
        setDefault(SimpleHatchBuilder())
        setDefault<Node>(Builder())
    }
}