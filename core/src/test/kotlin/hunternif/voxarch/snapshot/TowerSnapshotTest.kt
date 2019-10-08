package hunternif.voxarch.snapshot

import hunternif.voxarch.gen.Environment
import hunternif.voxarch.plan.ArchPlan
import hunternif.voxarch.plan.Room
import hunternif.voxarch.sandbox.castle.CastleSetup
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class TowerSnapshotTest : BaseSnapshotTest(10, 13, 10) {
    @Test
    fun tower_layer1() {
        val castleSetup = CastleSetup(DEFAULT_ENV)
        castleSetup.setup(gen)
        val plan = groundPlan().apply {
            base.addChild(castleSetup.squareTower().base)
        }
        gen.generate(plan, 5, 0, 5)
        record(out.sliceZ(3))
    }

    @Test
    fun tower_layer2() {
        val castleSetup = CastleSetup(DEFAULT_ENV)
        castleSetup.setup(gen)
        val plan = groundPlan().apply {
            base.addChild(castleSetup.squareTower().base)
        }
        gen.generate(plan, 5, 0, 5)
        record(out.sliceZ(4))
    }

    private fun groundPlan() =
        ArchPlan().apply {
            base.addChild(
                Room(Vec3(0, 0, 0), Vec3(9, 0, 9)).apply {
                    hasCeiling = false
                    type = "ground"
                }
            )
        }

    companion object {
        val DEFAULT_ENV = Environment(listOf())
    }
}