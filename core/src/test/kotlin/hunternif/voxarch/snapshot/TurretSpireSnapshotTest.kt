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
import hunternif.voxarch.sandbox.castle.setCastleBuilders
import hunternif.voxarch.sandbox.castle.turret.*
import org.junit.Test

class TurretSpireSnapshotTest : BaseSnapshotTest(10, 20, 10) {
    override fun setup() {
        super.setup()
        context.builders.setCastleBuilders()
    }

    @Test
    fun `spire ratio 0_6`() {
        val structure = turret(0.6)
        build(structure)
        recordVox()
    }

    @Test
    fun `spire ratio 1`() {
        val structure = turret(1.0)
        build(structure)
        recordVox()
    }

    @Test
    fun `spire ratio 2`() {
        val structure = turret(2.0)
        build(structure)
        recordVox()
    }

    @Test
    fun `spire ratio 3`() {
        val structure = turret(3.0)
        build(structure)
        recordVox()
    }

    companion object {
        private fun turret(spireRatio: Double): Node {
            val style = defaultStyle.add {
                styleFor<PolyRoom>(DOM_TURRET) {
                    position(5.vx, 0.vx, 5.vx)
                    diameter { 4.vx }
                    height { 2.vx }
                    shape { set(PolyShape.SQUARE) }
                }
                styleFor<DomTurretDecor> {
                    roofShape { set(RoofShape.SPIRE) }
                    spireRatio { set(spireRatio) }
                }
            }
            return domRoot {
                turret()
            }.buildDom(style)
        }
    }
}