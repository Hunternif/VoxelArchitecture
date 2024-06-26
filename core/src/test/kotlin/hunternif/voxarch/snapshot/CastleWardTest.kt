package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.*
import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolyShape
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

    private fun castleWard(): Node {
        val style = defaultStyle.add {
            style("main_turret") {
                size(7.vx, 5.vx, 7.vx)
                alignXZ { center() }
                alignY { bottom() }
                offsetY { 15.vx }
                shape { set(PolyShape.SQUARE) }
                roofShape { set(RoofShape.FLAT_BORDERED) }
                bottomShape { set(BottomShape.FOUNDATION) }
            }
            style("outer_ward") {
                shape { set(PolyShape.ROUND) }
                size(49.vx, 7.vx, 49.vx)
                position(30.vx, 0.vx, 30.vx)
                sideCount { set(6) }
                snapOrigin { floorCenter() }
            }
            style("inner_ward") {
                shape { set(PolyShape.SQUARE) }
                size(17.vx, 9.vx, 17.vx)
                alignY { bottom() }
                offsetY { 4.vx }
                snapOrigin { floorCenter() }
            }
            style("outer_ward_turret") {
                shape { set(PolyShape.ROUND) }
                roofShape { set(RoofShape.FLAT_BORDERED) }
                bottomShape { set(BottomShape.TAPERED) }
                size(9.vx, 11.vx, 9.vx)
                rotation { set(0.0) }
            }
            style("inner_ward_turret") {
                shape { set(PolyShape.SQUARE) }
                roofShape { set(RoofShape.SPIRE) }
                bottomShape { set(BottomShape.FOUNDATION) }
                size(5.vx, 13.vx, 5.vx)
            }
        }
        return domRoot {
            polyRoom("outer_ward") {
                allCorners { turret("outer_ward_turret") }
                allWalls { wall(BLD_CURTAIN_WALL) }
                polyRoom("inner_ward") {
                    allCorners { turret("inner_ward_turret") }
                    allWalls { wall(BLD_CURTAIN_WALL) }
                    randomWall { turret("main_turret") }
                }
            }
        }.buildDom(style, 0)
    }
}