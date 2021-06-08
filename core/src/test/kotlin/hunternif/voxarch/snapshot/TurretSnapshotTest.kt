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
        val structure = turret(4)
        build(structure)
        record(out.sliceZ(2))
    }

    @Test
    fun turret_layer3() {
        val structure = turret(4)
        build(structure)
        record(out.sliceZ(3))
    }

    @Test
    fun turret_layer5() {
        val structure = turret(4)
        build(structure)
        record(out.sliceZ(5))
    }

    @Test
    fun `crenellations width 4`() {
        val structure = turret(4)
        build(structure)
        record(out.sliceY(8))
    }

    @Test
    fun `crenellations width 6`() {
        val structure = turret(6)
        build(structure)
        record(out.sliceY(8))
    }

    @Test
    fun `turret corbels width 4`() {
        val structure = turret(4)
        build(structure)
        record(out.sliceY(4))
    }

    @Test
    fun `turret corbels width 6`() {
        val structure = turret(6)
        build(structure)
        record(out.sliceY(4))
    }

    companion object {
        private fun turret(width: Int) = Structure().apply {
            turret(
                origin = Vec3(5, 0, 5),
                size = Vec3(width, 5, width),
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