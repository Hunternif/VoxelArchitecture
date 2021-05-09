package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.MaterialConfig
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.vector.Vec3

class TowerBlueprint(
    private val config: Config = Config()
) {
    class Config(
        val width: Int = 6,
        val height: Int = 12,

        val roofOffset: Int = 2,

        // [4, ..) Higher values make rounder tower.
        val sideCount: Int = 8,

        val spireHeight: Int = 10,

        val withCrenellation: Boolean = true
    )

    fun setup(context: BuildContext) {
        context.builders.apply {
            set(FOUNDATION to FloorFoundationBuilder(MaterialConfig.WALL))
            set(TOWER_BODY to CorbelWallBuilder(
                MaterialConfig.WALL,
                MaterialConfig.WALL_DECORATION,
                corbelDepth = config.roofOffset
            ))
            set(TOWER_ROOF to CrenellationBuilder(MaterialConfig.WALL_DECORATION))
            set(TOWER_SPIRE to PyramidBuilder(MaterialConfig.ROOF, config.sideCount))
        }
    }

    fun layout(origin: IntVec3): Structure {
        val size = config.run { Vec3(width, height, width) }

        val roofOrigin = config.run { Vec3(0, height + 1, 0) }
        val roofSize = config.run { Vec3(width + roofOffset*2, 0, width + roofOffset*2) }

        val hasSpire = config.spireHeight > 0
        val spireOrigin = config.run { Vec3(0, height + 1, 0) }
        val spireSize = roofSize.addY(config.spireHeight)

        return Structure().apply {
            centeredRoom(Vec3(origin), size) {
                floor { type = FOUNDATION }
                floor()
                createTowerWalls()
                type = TOWER_BODY

                // spire:
                if (hasSpire) {
                    centeredRoom(spireOrigin, spireSize) {
                        type = TOWER_SPIRE
                    }
                }

                // overhanging roof:
                if (config.withCrenellation) {
                    centeredRoom(roofOrigin, roofSize) {
                        ceiling()
                        createTowerWalls()
                        type = TOWER_ROOF
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

    companion object {
        const val FOUNDATION = "foundation"
        const val TOWER_BODY = "tower_body"
        const val TOWER_ROOF = "tower_roof"
        const val TOWER_SPIRE = "tower_spire"
    }
}
