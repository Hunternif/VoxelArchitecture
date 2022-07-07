package hunternif.voxarch.snapshot

import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.builder.DomRoot
import hunternif.voxarch.dom.builder.Ward
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.PolygonRoom
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
            styleFor<PolygonRoom>("main_turret") {
                size(6.vx, 4.vx, 6.vx)
                align {
                    center()
                    bottom(15.vx)
                }
                roofShape = RoofShape.FLAT_BORDERED
                bodyShape = BodyShape.SQUARE
                bottomShape = BottomShape.FOUNDATION
            }
            styleFor<Ward>("outer_ward") {
                shape = PolygonShape.POLYGON
                size(48.vx, 6.vx, 48.vx)
                position(30.vx, 0.vx, 30.vx)
                edgeLength { 50.pct }
            }
            styleFor<Ward>("inner_ward") {
                shape = PolygonShape.SQUARE
                size(16.vx, 8.vx, 16.vx)
                align { bottom(4.vx) }
            }
            styleFor<PolygonRoom>("outer_ward_turret") {
                bodyShape = BodyShape.ROUND
                roofShape = RoofShape.FLAT_BORDERED
                bottomShape = BottomShape.TAPERED
                size(8.vx, 10.vx, 8.vx)
            }
            styleFor<PolygonRoom>("inner_ward_turret") {
                bodyShape = BodyShape.SQUARE
                roofShape = RoofShape.SPIRE
                bottomShape = BottomShape.FOUNDATION
                size(4.vx, 12.vx, 4.vx)
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
        }.build()
    }
}