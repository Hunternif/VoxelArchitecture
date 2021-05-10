package hunternif.voxarch.snapshot

import hunternif.voxarch.plan.Structure
import hunternif.voxarch.sandbox.castle.CastleBlueprint
import hunternif.voxarch.sandbox.castle.setCastleBuilders
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class TowerSnapshotTest : BaseSnapshotTest(10, 13, 10) {
    private var castleBlueprint = CastleBlueprint()

    override fun setup() {
        super.setup()
        context.builders.setCastleBuilders()
    }

    @Test
    fun tower_layer1() {
        val structure = Structure().apply {
            ground()
            addChild(castleBlueprint.squareTower(), Vec3(5, 1, 5))
        }
        build(structure)
        record(out.sliceZ(3))
    }

    @Test
    fun tower_layer2() {
        val structure = Structure().apply {
            ground()
            addChild(castleBlueprint.squareTower(), Vec3(5, 1, 5))
        }
        build(structure)
        record(out.sliceZ(4))
    }

    @Test
    fun `odd size`() {
        val structure = Structure().apply {
            ground()
            addChild(
                castleBlueprint.squareTower(
                    foundationSide = 7, wallSide = 5
                ),
                Vec3(5, 1, 5)
            )
        }
        build(structure)
        record(out.sliceZ(3))
    }

    @Test
    fun `rotated diagonal view`() {
        val structure = Structure().apply {
            ground()
            addChild(
                castleBlueprint.squareTower().apply { rotationY = 45.0 },
                Vec3(5, 1, 5)
            )
        }
        build(structure)
        record(out.sliceX(5))
    }

    @Test
    fun `rotated top view`() {
        val structure = Structure().apply {
            ground()
            addChild(
                castleBlueprint.squareTower(wallSide = 6).apply { rotationY = 45.0 },
                Vec3(5, 1, 5)
            )
        }
        build(structure)
        record(out.sliceY(9))
    }
}