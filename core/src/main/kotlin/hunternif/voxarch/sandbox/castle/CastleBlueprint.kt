package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.MaterialConfig
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3
import hunternif.voxarch.world.*
import kotlin.math.max

class CastleBlueprint(
    private val env: Environment,
    private val config: Config = Config()
) {
    class Config(
        val minTowerWidth: Int = 2,
        val bigTowerHeight: Int = 4
    )

    fun setup(context: BuildContext) {
        context.builders.apply {
            set(FOUNDATION to FloorFoundationBuilder(MaterialConfig.WALL, env))
            set(TOWER_MAIN to CrenellationBuilder(MaterialConfig.WALL))
        }
    }

    fun layout(terrain: HeightMap): Structure {
        val mountains = terrain.detectMountains()
        // set origin at start of heightmap but at 0 Y, because
        // all further points will have XZ relative to heightmap, but absolute Y.
        val structure = Structure(Vec3(terrain.start.x, 0, terrain.start.y))
        for (m in mountains) {
            structure.fillTower(m, terrain)
//            when {
//                m.rank > 0.9 -> structure.fillTower(m, terrain)
//                else -> structure.spreadCurtainWall(m, terrain)
//            }
        }
        return structure
    }

    /**
     * Spread small towers as far apart as possible, and connect them with walls.
     * ```
     * ||====||====||...
     * ```
     */
    private fun Structure.spreadCurtainWall(m: Mountain, terrain: HeightMap) {
        TODO("not implemented")
    }

    /**
     * Fill all top with towers, stretching them as wide as possible.
     * For now just 1 tower.
     * ```
     *  +---+_
     *  |___|_|
     * /    \  \
     * ```
     */
    private fun Structure.fillTower(m: Mountain, terrain: HeightMap) {
        val box = m.top.expandBox()
        if (box.width < config.minTowerWidth || box.length < config.minTowerWidth) return
        val maxHeight = box.fold(0) { acc, p -> max(acc, terrain[p]) }
        val start = Vec3(box.start.x, maxHeight, box.start.y)
        val end = Vec3(box.end.x, maxHeight, box.end.y)
        floor(start, end) { type = FOUNDATION }
        room(start, end.addY(config.bigTowerHeight)) {
            floor()
            ceiling()
            createFourWalls()
            type = TOWER_MAIN
        }
    }

    /**
     * Widths and heights are given as distance between blocks.
     * I.e. width 2 will result in 3 blocks.
     */
    fun squareTower(
        foundationHeight: Int = 2,
        foundationSide: Int = 6,
        wallSide: Int = 4,
        wallHeight: Int = 6
    ) = Structure().apply {
        centeredFloor(
            Vec3(0, foundationHeight, 0),
            Vec3(foundationSide, 0, foundationSide)
        ) {
            type = FOUNDATION
        }
        centeredRoom(
            Vec3(0, foundationHeight, 0),
            Vec3(wallSide, wallHeight, wallSide)
        ) {
            floor()
            ceiling()
            createFourWalls()
            type = TOWER_MAIN
        }
    }

    companion object {
        const val FOUNDATION = "foundation"
        const val TOWER_MAIN = "tower_main"
    }
}