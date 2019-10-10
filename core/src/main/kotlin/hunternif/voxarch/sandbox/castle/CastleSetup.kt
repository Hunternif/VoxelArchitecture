package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.gen.Environment
import hunternif.voxarch.gen.Generator
import hunternif.voxarch.plan.ArchPlan
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3

class CastleSetup(private val env: Environment) {

    fun setup(gen: Generator) {
        gen.setFloorGeneratorForType(FOUNDATION, FloorFoundationGen(env))
        gen.setWallGeneratorForType(TOWER_FLOOR, CrenellationGen())
    }

    fun squareTower(
        foundationHeight: Int = 3,
        foundationSide: Int = 7,
        wallSide: Int = 5,
        wallHeight: Int = 6
    ) = ArchPlan().apply {
        base.addChild(
            Room(
                Vec3(0, foundationHeight, 0),
                Vec3(foundationSide - 1, 0, foundationSide - 1)
            ).apply {
                type = FOUNDATION
                hasFloor = true
                hasCeiling = false
            }
        )
        base.addChild(
            Room(
                Vec3(0, foundationHeight, 0),
                Vec3(wallSide - 1, wallHeight, wallSide - 1)
            ).apply {
                type = TOWER_FLOOR
                hasFloor = true
                hasCeiling = true
                createFourWalls()
            }
        )
    }

    companion object {
        const val FOUNDATION = "foundation"
        const val TOWER_FLOOR = "tower_floor"
    }
}