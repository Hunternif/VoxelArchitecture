package hunternif.voxarch.snapshot

import hunternif.voxarch.dom.DOM_TURRET
import hunternif.voxarch.dom.builder.DomRoot
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.turret
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.sandbox.castle.setCastleBuilders
import hunternif.voxarch.sandbox.castle.turret.*
import org.junit.Test

class TurretOffcenterSnapshotTest : BaseSnapshotTest(10, 20, 10) {
    override fun setup() {
        super.setup()
        context.builders.setCastleBuilders()
        out.safeBoundary = true
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
        private fun turret(width: Int): Structure {
            val style = Stylesheet().apply {
                styleFor<PolygonRoom>(DOM_TURRET) {
                    position(1.vx, 0.vx, 1.vx)
                    start(0.vx, 0.vx, 0.vx)
                    diameter { width.vx }
                    height { 5.vx }
                    roofShape = RoofShape.SPIRE_BORDERED
                    bodyShape = BodyShape.SQUARE
                    bottomShape = BottomShape.FLAT
                    roofOffset { 1.vx }
                    spireRatio = 1.5
                    taperRatio = 0.75
                }
            }
            return DomRoot(style).apply {
                turret()
            }.build()
        }
    }
}