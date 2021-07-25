package hunternif.voxarch.snapshot

import hunternif.voxarch.plan.Structure
import hunternif.voxarch.sandbox.castle.innerWard
import hunternif.voxarch.sandbox.castle.outerWard
import hunternif.voxarch.sandbox.castle.setCastleBuilders
import hunternif.voxarch.sandbox.castle.turret.*
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class CastleWardTest: BaseSnapshotTest(60, 50, 60) {
    override fun setup() {
        super.setup()
        context.builders.setCastleBuilders()
        out.safeBoundary = true
    }

    @Test
    fun `castle ward top 1`() {
        build(castleWard())
        record(out.sliceY(0))
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
        val turret = createTurret(
            origin = Vec3.ZERO,
            size = Vec3(6, 4, 6),
            roofShape = RoofShape.FLAT_BORDERED,
            bodyShape = BodyShape.SQUARE,
            bottomShape = BottomShape.FOUNDATION,
            style = TowerStyle(),
            level = 4
        )
        val innerWard = innerWard(turret, 1)
        val outerWard = outerWard(innerWard, 2)
        return Structure().apply {
            addChild(outerWard, Vec3(30, 0, 30))
        }
    }
}