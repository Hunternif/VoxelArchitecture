package hunternif.voxarch.snapshot

import hunternif.voxarch.plan.Path
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.sandbox.castle.builder.CrenellationPathBuilder
import hunternif.voxarch.sandbox.castle.builder.CrenellationSizes
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class CrenellationPathBuilderTest : BaseSnapshotTest(10, 10, 1) {
    @Test
    fun `crenel 1`() {
        val path = Path(Vec3.ZERO,
            Vec3(0, 0, 0), Vec3(9, 0, 0)
        )
        val builder = CrenellationPathBuilder(MAT_WALL)
        builder.build(path, trans, out, context)
        record(out.sliceZ(0))
    }

    @Test
    fun `crenel 1 merlon 2`() {
        val path = Path(Vec3.ZERO,
            Vec3(0, 0, 0), Vec3(9, 0, 0)
        )
        val builder = CrenellationPathBuilder(MAT_WALL, CrenellationSizes(merlonLength = 2))
        builder.build(path, trans, out, context)
        record(out.sliceZ(0))
    }
}