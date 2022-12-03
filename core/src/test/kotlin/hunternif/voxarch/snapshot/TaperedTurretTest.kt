package hunternif.voxarch.snapshot

import hunternif.voxarch.dom.DOM_TURRET
import hunternif.voxarch.dom.builder.DomRoot
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.turret
import hunternif.voxarch.generator.TurretGenerator
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.sandbox.castle.setCastleBuilders
import hunternif.voxarch.sandbox.castle.turret.*
import org.junit.Test

class TaperedTurretTest : BaseSnapshotTest(10, 15, 10) {
    override fun setup() {
        super.setup()
        context.builders.setCastleBuilders()
    }

    @Test
    fun `tapered turret`() {
        val structure = turret(4)
        build(structure)
        recordVox()
        record(out.sliceZ(5))
    }

    companion object {
        private fun turret(width: Int): Structure {
            val style = defaultStyle.apply {
                style2For<PolygonRoom>(DOM_TURRET) {
                    position2(5.vx, 5.vx, 5.vx)
                    diameter2 { width.vx }
                    height2 { 5.vx }
                    shape2 { set(PolygonShape.SQUARE) }
                }
                styleForGen<TurretGenerator> {
                    roofShape = RoofShape.FLAT_BORDERED
                    bottomShape = BottomShape.TAPERED
                    roofOffset { 1.vx }
                    spireRatio = 1.5
                    taperRatio = 0.75
                }
            }
            return DomRoot(style).apply {
                turret()
            }.buildDom()
        }
    }
}