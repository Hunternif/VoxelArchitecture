package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.MaterialConfig
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Box2D
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.vector.Vec3
import hunternif.voxarch.world.*
import kotlin.math.max

class CastleBlueprint(
    private val env: Environment,
    private val config: Config = Config()
) {
    class Config(
        val bigTowerMinWidth: Int = 2,
        val bigTowerHeight: Int = 6,
        val wallTowerWidth: Int = 3,
        val wallTowerHeight: Int = 4,
        val wallSectionLength: Int = 10
        )

    fun setup(context: BuildContext) {
        context.builders.apply {
            set(FOUNDATION to FloorFoundationBuilder(MaterialConfig.WALL, env))
            set(TOWER_MAIN to CrenellationBuilder(MaterialConfig.WALL))
        }
    }

    fun layout(terrain: HeightMap): Structure {
        val mountains = terrain.detectMountains()
        // set origin at start of heightmap but at Y=0, because
        // all further points will have XZ relative to heightmap, but absolute Y.
        val structure = Structure(Vec3(terrain.start.x, 0, terrain.start.y))
        for (m in mountains) {
            structure.spreadCurtainWall(m, terrain)
//            when {
//                m.rank > 0.9 -> structure.bigTower(m, terrain)
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
        val boxes = m.topWithFlatPerimeter.spreadBoxesAlongPerimeter(
            IntVec2(config.wallTowerWidth, config.wallTowerWidth),
            (config.wallSectionLength + config.wallTowerWidth).toDouble()
        )
        for (box in boxes) {
            if (box.width < config.wallTowerWidth || box.length < config.wallTowerWidth) continue
            towerFromBox(box, config.wallTowerHeight, terrain)
        }
        // TODO build walls between towers
        // TODO have a global collection of towers to reduce clustering from adjacent mountains
    }

    /**
     * Fill the entire top with a single tower, stretching it as wide as possible.
     * ```
     *  +---+
     *  |___|
     * /     \
     * ```
     */
    private fun Structure.bigTower(m: Mountain, terrain: HeightMap) {
        val box = m.top.fitBox()
        if (box.width < config.bigTowerMinWidth || box.length < config.bigTowerMinWidth) return
        towerFromBox(box, config.bigTowerHeight, terrain)
    }

    private fun Structure.towerFromBox(box: Box2D, towerHeight: Int, terrain: HeightMap) {
        val maxHeight = box.fold(0) { acc, p -> max(acc, terrain[p]) }
        val start = Vec3(box.start.x, maxHeight, box.start.y)
        val end = Vec3(box.end.x, maxHeight, box.end.y)
        floor(start, end) { type = FOUNDATION }
        room(start, end.addY(towerHeight)) {
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