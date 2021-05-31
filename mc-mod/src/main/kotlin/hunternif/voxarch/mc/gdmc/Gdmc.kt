package hunternif.voxarch.mc.gdmc

import hunternif.voxarch.builder.MainBuilder
import hunternif.voxarch.mc.MCWorld
import hunternif.voxarch.mc.config.defaultContext
import hunternif.voxarch.sandbox.castle.CastleBlueprint
import hunternif.voxarch.sandbox.castle.setCastleBuilders
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.world.HeightMap.Companion.terrainMap
import net.minecraft.world.World
import kotlin.math.abs

const val defaultRadius = 128.0

fun buildSettlement(
    world: World,
    fromX: Int, fromZ: Int,
    toX: Int, toZ: Int
) {
    val context = defaultContext
    val mcWorld = MCWorld(world)

    context.builders.setCastleBuilders()

    val center = IntVec2((fromX + toX)/2, (fromZ + toZ)/2)
    val area = IntVec2(abs(toX - fromX), abs(toZ - fromZ))
    val terrain = mcWorld.terrainMap(center, area)

    val castle = CastleBlueprint().layout(terrain)

    MainBuilder().build(castle, mcWorld, context)
}