package hunternif.voxarch.snapshot

import hunternif.voxarch.gen.Environment
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.sandbox.castle.CastleSetup
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class TowerSnapshotTest : BaseSnapshotTest(10, 13, 10) {
    private val castleSetup = CastleSetup(DEFAULT_ENV)

    @Test
    fun tower_layer1() {
        castleSetup.setup(buildContext)
        val structure = Structure().apply {
            ground()
            addChild(castleSetup.squareTower(), Vec3(5, 1, 5))
        }
        build(structure)
        record(out.sliceZ(3))
    }

    @Test
    fun tower_layer2() {
        castleSetup.setup(buildContext)
        val structure = Structure().apply {
            ground()
            addChild(castleSetup.squareTower(), Vec3(5, 1, 5))
        }
        build(structure)
        record(out.sliceZ(4))
    }

    @Test
    fun `odd size`() {
        castleSetup.setup(buildContext)
        val structure = Structure().apply {
            ground()
            addChild(
                castleSetup.squareTower(
                    foundationSide = 7, wallSide = 5
                ),
                Vec3(5, 1, 5)
            )
        }
        build(structure)
        record(out.sliceZ(3))
    }

    @Test
    fun rotated() {
        castleSetup.setup(buildContext)
        val structure = Structure().apply {
            ground()
            addChild(
                castleSetup.squareTower().apply { rotationY = 45.0 },
                Vec3(5, 1, 5)
            )
        }
        build(structure)
        record(out.sliceX(5))
    }

    companion object {
        val DEFAULT_ENV = Environment(listOf())
    }
}