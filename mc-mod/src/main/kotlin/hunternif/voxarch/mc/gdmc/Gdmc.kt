package hunternif.voxarch.mc.gdmc

import hunternif.voxarch.builder.MainBuilder
import hunternif.voxarch.mc.MCWorld
import hunternif.voxarch.mc.config.defaultContext
import hunternif.voxarch.sandbox.castle.CastleBlueprint
import hunternif.voxarch.sandbox.castle.setCastleBuilders
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.world.HeightMap.Companion.terrainMap
import net.minecraft.world.World

const val defaultRadius = 128.0

fun buildSettlement(
    world: World,
    from: IntVec2,
    to: IntVec2
) {
    val mcWorld = MCWorld(world)
    val context = defaultContext.apply {
        builders.setCastleBuilders()
    }

    val terrain = mcWorld.terrainMap(from, to)

    val castle = CastleBlueprint().layout(terrain)

    MainBuilder().build(castle, mcWorld, context)
}