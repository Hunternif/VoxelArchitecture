package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.MaterialConfig
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.sandbox.castle.CrenellationBuilder
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class CrenellationBuilderTest : BaseSnapshotTest(10, 10, 10) {
    @Test
    fun `crenel 1 with wall height 0`() {
        val wall = Wall(Vec3(0, 0, 0), Vec3(9, 0, 0))
        val builder = CrenellationBuilder(MaterialConfig.WALL)
        builder.build(wall, out, buildContext)
        record(out.sliceZ(0))
    }

    @Test
    fun `crenel 1 merlon 2 with wall height 1`() {
        val wall = Wall(Vec3(0, 0, 0), Vec3(9, 1, 0))
        val builder = CrenellationBuilder(MaterialConfig.WALL, merlonLength = 2)
        builder.build(wall, out, buildContext)
        record(out.sliceZ(0))
    }
}