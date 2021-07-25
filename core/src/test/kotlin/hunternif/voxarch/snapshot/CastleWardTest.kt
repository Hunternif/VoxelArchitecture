package hunternif.voxarch.snapshot

import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.builder.DomRoot
import hunternif.voxarch.dom.builder.Ward
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
    fun `castle ward top 0`() {
        //TODO: tapered bottom has an uneven round shape. fix it!
        build(castleWard())
        record(out.sliceY(0))
    }

    @Test
    fun `castle ward top 1`() {
        build(castleWard())
        record(out.sliceY(1))
    }

    @Test
    fun `castle ward top 2`() {
        build(castleWard())
        record(out.sliceY(13))
    }

    @Test
    fun `castle ward top 3`() {
        build(castleWard())
        record(out.sliceY(19))
    }

    @Test
    fun `castle ward profile 1`() {
        build(castleWard())
        record(out.sliceX(30))
    }

    @Test
    fun `castle ward profile 2`() {
        build(castleWard())
        record(out.sliceX(9))
    }

    @Test
    fun `castle ward profile 3`() {
        build(castleWard())
        record(out.sliceX(22))
    }

    private fun castleWard(): Structure {
        val style = defaultStyle.apply {
            styleFor<Turret>("turret") {
                size(6.vx, 4.vx, 6.vx)
                x { 50.pct } // TODO introduce 'align'
                y { 15.vx }
                roofShape = RoofShape.FLAT_BORDERED
                bodyShape = BodyShape.SQUARE
                bottomShape = BottomShape.FOUNDATION
            }
            styleFor<Ward>("outer_ward") {
                shape = PolygonShape.ROUND
                size(48.vx, 6.vx, 48.vx)
                position(30.vx, 0.vx, 30.vx)
                edgeLength { 50.pct }
            }
            styleFor<Ward>("inner_ward") {
                shape = PolygonShape.SQUARE
                size(16.vx, 8.vx, 16.vx)
                y { 4.vx }
            }
            styleFor<Turret>("outer_ward_turret") {
                bodyShape = BodyShape.ROUND
                roofShape = RoofShape.FLAT_BORDERED
                bottomShape = BottomShape.TAPERED
                size(8.vx, 10.vx, 8.vx)
            }
            styleFor<Turret>("inner_ward_turret") {
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
                    randomWall { turret("turret") }
                }
            }
        }.build()
    }
}