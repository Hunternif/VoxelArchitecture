package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.plan.*
import hunternif.voxarch.util.RoomUtil
import hunternif.voxarch.vector.Box2D
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.vector.Vec3
import hunternif.voxarch.world.*
import kotlin.math.max

class CastleBlueprint(
    private val config: Config = Config()
) {
    class Config(
        val bigTowerMinWidth: Int = 6,
        val bigTowerHeight: Int = 8,
        val wallTowerWidth: Int = 4,
        val wallTowerHeight: Int = 6,
        val wallSectionLength: Int = 10,
        val wallHeight: Int = 4
        )

    private val roomUtil = RoomUtil()

    fun layout(terrain: HeightMap): Structure {
        val mountains = terrain.detectMountains()
        // set origin at start of heightmap but at Y=0, because
        // all further points will have XZ relative to heightmap, but absolute Y.
        val structure = Structure(Vec3(terrain.start.x, 0, terrain.start.y))
        for (m in mountains) {
//            if (m.rank > 0.7) {
//                structure.bigTower(m, terrain)
//            }
            structure.spreadCurtainWall(m, terrain)
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
        val boxes = m.top.spreadBoxesAlongPerimeter(
            IntVec2(config.wallTowerWidth, config.wallTowerWidth),
            (config.wallSectionLength + config.wallTowerWidth).toDouble()
        )
        // TODO have a global collection of towers to reduce clustering from adjacent mountains
        val towers = mutableListOf<Room>()
        for (box in boxes) {
            if (box.width < config.wallTowerWidth || box.length < config.wallTowerWidth) continue
            towers.add(towerFromBox(box, config.wallTowerHeight, terrain))
        }
        // elevate all towers to the same height
        val maxElevation = towers.map { it.origin.y }.max() ?: 0.0
        towers.forEach { it.origin.y = maxElevation }
        buildWallsBetween(towers, m.top)
    }

    private fun Structure.buildWallsBetween(towers: List<Room>, area: Area) {
        if (towers.isEmpty()) return
        // TODO connect towers in order of minimum distance
        for (i in 0 until towers.count()-1) {
            buildWallBetween(towers[i], towers[i+1], area)
        }
        buildWallBetween(towers.last(), towers[0], area)
    }

    private fun Structure.buildWallBetween(t1: Room, t2: Room, area: Area) {
        // TODO consider inside-outside of the wall
        if (t1 == t2) return
        val c1 = t1.origin.toXZ()
        val c2 = t2.origin.toXZ()
        val middle = t1.origin.add(t2.origin).multiplyLocal(0.5)
        val p1 = roomUtil.rayTrace(t1, middle.toXZ(), c1) ?: c1
        val p2 = roomUtil.rayTrace(t2, middle.toXZ(), c2) ?: c2
        // TODO sometimes the wall goes too far deep into the tower

        //TODO ensure that walls don't go outside the mountain top?
//        val length = p2.distanceTo(p1)
//        val wallVec = p2.subtract(p1).normalizeLocal()
//        val w = p1.clone()
//        var outsideArea = false
//        for (i in 0..length.toInt()) {
//            if (w.toInt() !in area) { outsideArea = true; break }
//            w.addLocal(wallVec)
//        }
//        if (outsideArea) return

        val y = max(t1.origin.y, t2.origin.y)
        wall(Vec3(p1.x, y, p1.y), Vec3(p2.x, y + config.wallHeight, p2.y)) {
            type = BLD_CURTAIN_WALL
        }
    }

    /**
     * Fill the entire top with a single tower, stretching it as wide as possible.
     * @return null if not enough space to fit a big tower as defined in [config]
     * ```
     *  +---+
     *  |___|
     * /     \
     * ```
     */
    private fun Structure.bigTower(m: Mountain, terrain: HeightMap): Room? {
        val box = m.topWithFlatPerimeter.fitBox()
        if (box.width < config.bigTowerMinWidth || box.length < config.bigTowerMinWidth) return null
        return towerFromBox(box, config.bigTowerHeight, terrain)
    }

    private fun Structure.towerFromBox(box: Box2D, towerHeight: Int, terrain: HeightMap): Room {
        val maxHeight = box.fold(0) { acc, p -> max(acc, terrain[p]) }
        val start = Vec3(box.start.x, maxHeight, box.start.y)
        val end = Vec3(box.end.x, maxHeight, box.end.y)
        return room(start, end.addY(towerHeight)) {
            floor { type = BLD_FOUNDATION }
            floor()
            ceiling()
            createFourWalls()
            type = BLD_TOWER_MAIN
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
            type = BLD_FOUNDATION
        }
        centeredRoom(
            Vec3(0, foundationHeight, 0),
            Vec3(wallSide, wallHeight, wallSide)
        ) {
            floor()
            ceiling()
            createFourWalls()
            type = BLD_TOWER_MAIN
        }
    }
}