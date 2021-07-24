package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.SnakePathBuilder
import hunternif.voxarch.plan.Path
import hunternif.voxarch.sandbox.castle.MAT_WALL
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class PathBuilderTest : BaseSnapshotTest(10, 10, 10) {

    @Test
    fun `square path`() {
        val path = squarePath()
        val builder = SnakePathBuilder(MAT_WALL)
        builder.build(path, out, context)
        record(out.sliceY(0))
    }

    @Test
    fun `square path step 2`() {
        val path = squarePath()
        val builder = SnakePathBuilder(MAT_WALL, 2.0)
        builder.build(path, out, context)
        record(out.sliceY(0))
    }

    @Test
    fun `square path step 3`() {
        val path = squarePath()
        val builder = SnakePathBuilder(MAT_WALL, 3.0)
        builder.build(path, out, context)
        record(out.sliceY(0))
    }

    @Test
    fun `square path rotated 45`() {
        val path = square45Path()
        val builder = SnakePathBuilder(MAT_WALL)
        builder.build(path, out, context)
        record(out.sliceY(0))
    }

    @Test
    fun `square path rotated 45 step 2_828`() {
        val path = square45Path()
        val builder = SnakePathBuilder(MAT_WALL, 2.828)
        builder.build(path, out, context)
        record(out.sliceY(0))
    }

    @Test
    fun `square path rotated 45 step 4_243`() {
        val path = square45Path()
        val builder = SnakePathBuilder(MAT_WALL, 4.243)
        builder.build(path, out, context)
        record(out.sliceY(0))
    }

    companion object {
        fun squarePath() = Path(Vec3.ZERO,
            Vec3(8, 0, 8),
            Vec3(8, 0, 1),
            Vec3(1, 0, 1),
            Vec3(1, 0, 8),
            Vec3(8, 0, 8)
        )

        fun square45Path() = Path(Vec3.ZERO,
            Vec3(8, 0, 4),
            Vec3(4, 0, 0),
            Vec3(0, 0, 4),
            Vec3(4, 0, 8),
            Vec3(8, 0, 4)
        )
    }

}