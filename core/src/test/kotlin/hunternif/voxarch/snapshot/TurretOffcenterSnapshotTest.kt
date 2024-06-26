package hunternif.voxarch.snapshot

import hunternif.voxarch.dom.DOM_TURRET
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.turret
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.PolyShape
import hunternif.voxarch.builder.setCastleBuilders
import hunternif.voxarch.sandbox.castle.turret.*
import org.junit.Test

class TurretOffcenterSnapshotTest : BaseSnapshotTest(10, 20, 10) {
    override fun setup() {
        super.setup()
        context.builders.setCastleBuilders()
        out.safeBoundary = true
    }

    @Test
    fun turret() {
        val structure = turret(5)
        build(structure)
        recordVox()
        record(out.sliceZ(2))
        record(out.sliceZ(3))
        record(out.sliceZ(5))
    }

    @Test
    fun `crenellations width 4`() {
        val structure = turret(5)
        build(structure)
        record(out.sliceY(8))
    }

    @Test
    fun `crenellations width 6`() {
        val structure = turret(7)
        build(structure)
        record(out.sliceY(8))
    }

    @Test
    fun `turret corbels width 4`() {
        val structure = turret(5)
        build(structure)
        record(out.sliceY(4))
    }

    @Test
    fun `turret corbels width 6`() {
        val structure = turret(7)
        build(structure)
        record(out.sliceY(4))
    }

    companion object {
        private fun turret(width: Int): Node {
            val style = defaultStyle.add {
                styleFor<PolyRoom>(DOM_TURRET) {
                    position(1.vx, 0.vx, 1.vx)
                    start(0.vx, 0.vx, 0.vx)
                    diameter { width.vx }
                    height { 6.vx }
                    shape { set(PolyShape.SQUARE) }
                }
                style(selectDescendantOf(DOM_TURRET)) {
                    roofShape { set(RoofShape.SPIRE_BORDERED) }
                    bottomShape { set(BottomShape.FLAT) }
                    roofOffset { 1.vx }
                    spireRatio { set(2.5) }
                    taperRatio { set(1.3) }
                }
            }
            return domRoot {
                turret()
            }.buildDom(style)
        }
    }
}