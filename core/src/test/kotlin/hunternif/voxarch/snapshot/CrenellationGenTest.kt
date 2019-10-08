package hunternif.voxarch.snapshot

import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.sandbox.castle.CrenellationGen
import hunternif.voxarch.vector.Vec2
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class CrenellationGenTest : BaseSnapshotTest(10, 10, 10) {
    @Test
    fun `crenel 1-2 with wall height 0`() {
        val wallGen = CrenellationGen()
        val room = Room(Vec3(5, 0, 0), Vec3(9, 0, 0))
        val wall = Wall(room, Vec2(0, 0), Vec2(9, 0))
        wallGen.generateWall(out, wall, DEFAULT_MATERIALS)
        record(out.sliceZ(0))
    }
}