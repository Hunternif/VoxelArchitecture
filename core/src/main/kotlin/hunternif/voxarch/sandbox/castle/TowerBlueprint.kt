package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.vector.Vec3

class TowerBlueprint(
    private val config: Config = Config()
) {
    class Config(
        val width: Int = 6,
        val height: Int = 12,

        val roofOffset: Int = 1,

        // [4, ..) Higher values make rounder tower.
        val sideCount: Int = 8,

        val spireHeight: Int = 10,

        val withCrenellation: Boolean = true
    )

    fun layout(origin: IntVec3): Structure {
        val size = config.run { Vec3(width, height, width) }

        val roofOrigin = config.run { Vec3(0, height + 1, 0) }
        val roofSize = config.run { Vec3(width + roofOffset*2, 0, width + roofOffset*2) }

        val hasSpire = config.spireHeight > 0
        val spireOrigin = roofOrigin.clone()
        val spireSize = roofSize.addY(config.spireHeight)

        return Structure().apply {
            centeredRoom(Vec3(origin), size) {
                floor { type = BLD_FOUNDATION }
                floor()
                createTowerWalls()
                // TODO: place corbels as separate nodes
                type = BLD_TOWER_BODY

                // spire:
                if (hasSpire) {
                    centeredRoom(spireOrigin, spireSize) {
                        createTowerWalls()
                        walls.forEach { it.transparent = true }
                        type = BLD_TOWER_SPIRE
                    }
                }

                // overhanging roof:
                if (config.withCrenellation) {
                    centeredRoom(roofOrigin, roofSize) {
                        ceiling()
                        createTowerWalls()
                        type = BLD_TOWER_ROOF
                    }
                }
            }
        }
    }

    private fun Room.createTowerWalls() {
        when (config.sideCount) {
            4 -> createFourWalls()
            else -> createRoundWalls(config.sideCount)
        }
    }
}
