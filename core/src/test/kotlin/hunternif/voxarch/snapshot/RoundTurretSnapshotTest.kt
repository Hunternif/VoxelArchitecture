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

class RoundTurretSnapshotTest : BaseSnapshotTest(10, 20, 10) {
    override fun setup() {
        super.setup()
        context.builders.setCastleBuilders()
    }

    @Test
    fun `crenellations width 2`() {
        val structure = turret(2)
        build(structure)
        record(out.sliceY(8))
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
    fun `corbels width 2`() {
        val structure = turret(2)
        build(structure)
        record(out.sliceY(4))
    }

    @Test
    fun `corbels width 4`() {
        val structure = turret(4)
        build(structure)
        record(out.sliceY(4))
    }

    @Test
    fun `corbels width 6`() {
        val structure = turret(6)
        build(structure)
        record(out.sliceY(4))
    }

    companion object {
        private fun turret(width: Int): Structure {
            val style = defaultStyle.apply {
                style2For<PolygonRoom>(DOM_TURRET) {
                    position2(5.vx, 0.vx, 5.vx)
                    diameter2 { width.vx }
                    height2 { 5.vx }
                    shape2 { set(PolygonShape.ROUND) }
                }
                styleForGen<TurretGenerator> {
                    roofShape = RoofShape.SPIRE_BORDERED
                    bottomShape = BottomShape.FLAT
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