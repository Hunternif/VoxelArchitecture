package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.MaterialConfig
import hunternif.voxarch.gen.Environment
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3

class CastleSetup(private val env: Environment) {

    fun setup(context: BuildContext) {
        context.builders.apply {
            set(FOUNDATION to FloorFoundationBuilder(MaterialConfig.WALL, env))
            set(TOWER_FLOOR to CrenellationBuilder(MaterialConfig.WALL))
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
            type = TOWER_FLOOR
        }
    }

    companion object {
        const val FOUNDATION = "foundation"
        const val TOWER_FLOOR = "tower_floor"
    }
}