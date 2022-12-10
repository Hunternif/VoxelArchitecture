package hunternif.voxarch.snapshot

import hunternif.voxarch.dom.DOM_TURRET
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.turret
import hunternif.voxarch.generator.TurretGenerator
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape
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
        private fun turret(width: Int): Node {
            val style = defaultStyle.apply {
                styleFor<PolygonRoom>(DOM_TURRET) {
                    position(5.vx, 5.vx, 5.vx)
                    diameter { width.vx }
                    height { 5.vx }
                    shape { set(PolygonShape.SQUARE) }
                }
                styleFor<TurretGenerator> {
                    roofShape { set(RoofShape.FLAT_BORDERED) }
                    bottomShape { set(BottomShape.TAPERED) }
                    roofOffset { 1.vx }
                    spireRatio { set(1.5) }
                    taperRatio { set(0.75) }
                }
            }
            return domRoot(style) {
                turret()
            }.buildDom()
        }
    }
}