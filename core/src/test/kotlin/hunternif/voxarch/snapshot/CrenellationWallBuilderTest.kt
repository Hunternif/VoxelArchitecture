package hunternif.voxarch.snapshot

import hunternif.voxarch.plan.Wall
import hunternif.voxarch.sandbox.castle.MAT_WALL
import hunternif.voxarch.sandbox.castle.builder.CrenellationSizes
import hunternif.voxarch.sandbox.castle.builder.CrenellationWallBuilder
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class CrenellationWallBuilderTest : BaseSnapshotTest(10, 10, 1) {
    @Test
    fun `crenel 1 with wall height 0`() {
        val wall = Wall(Vec3(0, 0, 0), Vec3(9, 0, 0))
        val builder = CrenellationWallBuilder(MAT_WALL, downToGround = true)
        builder.build(wall, trans, out, context)
        record(out.sliceZ(0))
    }

    @Test
    fun `crenel 1 merlon 2 with wall height 1`() {
        val wall = Wall(Vec3(0, 0, 0), Vec3(9, 1, 0))
        val builder = CrenellationWallBuilder(MAT_WALL, CrenellationSizes(merlonLength = 2), downToGround = true)
        builder.build(wall, trans, out, context)
        record(out.sliceZ(0))
    }
}