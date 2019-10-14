package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.MaterialConfig
import hunternif.voxarch.gen.Environment
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3

class CastleSetup2(private val env: Environment) {

    fun setup(context: BuildContext) {
        context.builders.apply {
            set(FOUNDATION to FloorFoundationBuilder(MaterialConfig.WALL, env))
            set(TOWER_FLOOR to CrenellationBuilder(MaterialConfig.WALL))
        }
    }

    /**
     * Widths and heights are given in actual number of blocks.
     * I.e. if you query `floor.width`, you will get (this number - 1).
     */
    fun squareTower(
        foundationHeight: Int = 3,
        foundationSide: Int = 7,
        wallSide: Int = 5,
        wallHeight: Int = 6
    ) = Structure().apply {
        centeredFloor(
            Vec3(0, foundationHeight, 0),
            Vec3(foundationSide - 1, 0, foundationSide - 1)
        ) {
            type = FOUNDATION
        }
        centeredRoom(
            Vec3(0, foundationHeight, 0),
            Vec3(wallSide - 1, wallHeight, wallSide - 1)
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