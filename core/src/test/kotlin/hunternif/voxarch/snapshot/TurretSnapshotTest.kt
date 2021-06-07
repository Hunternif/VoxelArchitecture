package hunternif.voxarch.snapshot

import hunternif.voxarch.plan.Structure
import hunternif.voxarch.sandbox.castle.setCastleBuilders
import hunternif.voxarch.sandbox.castle.turret.*
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class TurretSnapshotTest : BaseSnapshotTest(10, 20, 10) {
    override fun setup() {
        super.setup()
        context.builders.setCastleBuilders()
    }

    @Test
    fun turret_layer2() {
        val structure = turret()
        build(structure)
        record(out.sliceZ(2))
    }

    @Test
    fun turret_layer3() {
        val structure = turret()
        build(structure)
        record(out.sliceZ(3))
    }

    @Test
    fun turret_layer5() {
        val structure = turret()
        build(structure)
        record(out.sliceZ(5))
    }

    companion object {
        private fun turret() = Structure().apply {
            turret(
                origin = Vec3(5, 0, 5),
                size = Vec3(4, 5, 4),
                roofShape = RoofShape.SPIRE_BORDERED,
                bodyShape = BodyShape.SQUARE,
                bottomShape = BottomShape.FLAT,
                positionType = TurretPosition.NONE,
                style = TowerStyle(
                    roofOffset = 1,
                    spireRatio = 1.5,
                    turretTaperRatio = 0.75
                )
            )
        }
    }
}