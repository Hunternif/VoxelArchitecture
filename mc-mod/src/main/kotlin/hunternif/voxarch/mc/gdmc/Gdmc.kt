package hunternif.voxarch.mc.gdmc

import hunternif.voxarch.builder.MainBuilder
import hunternif.voxarch.mc.MCWorld
import hunternif.voxarch.mc.config.defaultContext
import hunternif.voxarch.sandbox.castle.setCastleBuilders
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.world.HeightMap
import hunternif.voxarch.world.HeightMap.Companion.terrainMap
import hunternif.voxarch.world.Mountain
import hunternif.voxarch.world.detectMountains
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
    val seed = world.server?.worldData?.worldGenSettings()?.seed()
        ?: System.currentTimeMillis()

    val terrain = mcWorld.terrainMap(from, to)

    val mountain = terrain.findBiggestMountain()
    val castle = mountain?.let {
        mountainCastle(terrain, it, seed)
    } ?: defaultCastle(terrain, seed)

    MainBuilder().build(castle, mcWorld, context)
}

private fun HeightMap.findBiggestMountain(): Mountain? =
    detectMountains()
        // minimum allowed area
        .filter { it.top.size > towerWidth*towerWidth*2 }
        // find widest * tallest mountain
        .maxBy { it.top.size * averageIn(it.top) }

