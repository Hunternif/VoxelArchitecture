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
    fun `castle ward`() {
        val turret = createTurret(
            origin = Vec3.ZERO,
            size = Vec3(4, 4, 4),
            roofShape = RoofShape.FLAT_BORDERED,
            bodyShape = BodyShape.SQUARE,
            bottomShape = BottomShape.FOUNDATION,
            style = TowerStyle(),
            level = 4
        )
        val innerWard = innerWard(turret, 1)
        val outerWard = outerWard(innerWard, 2)
        val structure = Structure().apply {
            addChild(outerWard, Vec3(30, 0, 30))
        }
        build(structure)
        record(out.sliceY(0))
    }
}