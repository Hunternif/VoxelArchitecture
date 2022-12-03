package hunternif.voxarch.snapshot

import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.builder.DomRoot
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.sandbox.castle.BLD_CURTAIN_WALL
import hunternif.voxarch.sandbox.castle.setCastleBuilders
import hunternif.voxarch.sandbox.castle.turret.*
import org.junit.Test

class CastleWardTest: BaseSnapshotTest(60, 50, 60) {
    override fun setup() {
        super.setup()
        context.builders.setCastleBuilders()
        out.safeBoundary = true
    }

    @Test
    fun `castle ward`() {
        build(castleWard())
        //TODO: tops of outer turrets are asymmetric
        recordVox()
        record(out.sliceY(0))
        record(out.sliceY(1))
        record(out.sliceY(8))
        record(out.sliceY(13))
        record(out.sliceY(19))
        record(out.sliceX(9))
        record(out.sliceX(22))
        record(out.sliceX(30))
    }

    private fun castleWard(): Structure {
        val style = defaultStyle.apply {
            style2("main_turret") {
                size2(6.vx, 4.vx, 6.vx)
                alignXZ { center() }
                alignY { bottom() }
                y2 { 15.vx }
                shape2 { set(PolygonShape.SQUARE) }
                roofShape2 { set(RoofShape.FLAT_BORDERED) }
                bottomShape2 { set(BottomShape.FOUNDATION) }
            }
            style2("outer_ward") {
                shape2 { set(PolygonShape.ROUND) }
                size2(48.vx, 6.vx, 48.vx)
                position2(30.vx, 0.vx, 30.vx)
                edgeLength2 { 50.pct }
            }
            style2("inner_ward") {
                shape2 { set(PolygonShape.SQUARE) }
                size2(16.vx, 8.vx, 16.vx)
                alignY { bottom() }
                y2 { 4.vx }
            }
            style2("outer_ward_turret") {
                shape2 { set(PolygonShape.ROUND) }
                roofShape2 { set(RoofShape.FLAT_BORDERED) }
                bottomShape2 { set(BottomShape.TAPERED) }
                size2(8.vx, 10.vx, 8.vx)
            }
            style2("inner_ward_turret") {
                shape2 { set(PolygonShape.SQUARE) }
                roofShape2 { set(RoofShape.SPIRE) }
                bottomShape2 { set(BottomShape.FOUNDATION) }
                size2(4.vx, 12.vx, 4.vx)
            }
        }
        return DomRoot(style, 0).apply {
            ward("outer_ward") {
                allCorners { turret("outer_ward_turret") }
                allWalls { wall(BLD_CURTAIN_WALL) }
                ward("inner_ward") {
                    allCorners { turret("inner_ward_turret") }
                    allWalls { wall(BLD_CURTAIN_WALL) }
                    randomWall { turret("main_turret") }
                }
            }
        }.buildDom()
    }
}