package hunternif.voxarch.snapshot

import hunternif.voxarch.dom.DOM_TURRET
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.turret
import hunternif.voxarch.dom.builder.DomTurretDecor
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.PolyShape
import hunternif.voxarch.builder.setCastleBuilders
import hunternif.voxarch.sandbox.castle.turret.*
import org.junit.Test

class TaperedTurretTest : BaseSnapshotTest(10, 15, 10) {
    override fun setup() {
        super.setup()
        context.builders.setCastleBuilders()
    }

    @Test
    fun `tapered turret`() {
        val structure = turret(5)
        build(structure)
        recordVox()
        record(out.sliceZ(5))
    }

    companion object {
        private fun turret(width: Int): Node {
            val style = defaultStyle.add {
                styleFor<PolyRoom>(DOM_TURRET) {
                    position(5.vx, 5.vx, 5.vx)
                    diameter { width.vx }
                    height { 6.vx }
                    shape { set(PolyShape.SQUARE) }
                }
                styleFor<DomTurretDecor> {
                    roofShape { set(RoofShape.FLAT_BORDERED) }
                    bottomShape { set(BottomShape.TAPERED) }
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