package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.plan.Path
import hunternif.voxarch.sandbox.castle.MAT_WALL
import hunternif.voxarch.snapshot.PathBuilderTest.Companion.squarePath
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.PathHugger
import hunternif.voxarch.vector.TransformationStack
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class HairyPathBuilderTest : BaseSnapshotTest(10, 10, 10) {

    @Test
    fun `hairy square path`() {
        val path = squarePath()
        val builder = HairyPathBuilder(MAT_WALL)
        builder.build(path, trans, out, context)
        record(out.sliceY(0))
    }

    @Test
    fun `hairy square path step 2`() {
        val path = squarePath()
        val builder = HairyPathBuilder(MAT_WALL, 2.0)
        builder.build(path, trans, out, context)
        record(out.sliceY(0))
    }

    @Test
    fun `hairy square path step 3`() {
        val path = squarePath()
        val builder = HairyPathBuilder(MAT_WALL, 3.0)
        builder.build(path, trans, out, context)
        record(out.sliceY(0))
    }

    @Test
    fun `hairy square path rotated 45`() {
        val path = smallSquare45Path()
        val builder = HairyPathBuilder(MAT_WALL, 1.414, 1.414, 1.414)
        builder.build(path, trans, out, context)
        record(out.sliceY(0))
    }

    @Test
    fun `hairy square path rotated 45 step 2_828`() {
        val path = smallSquare45Path()
        val builder = HairyPathBuilder(MAT_WALL, 2.828, 1.414, 1.414)
        builder.build(path, trans, out, context)
        record(out.sliceY(0))
    }

    @Test
    fun `hairy square path rotated 45 step 4_243`() {
        val path = smallSquare45Path()
        val builder = HairyPathBuilder(MAT_WALL, 4.243, 1.414, 1.414)
        builder.build(path, trans, out, context)
        record(out.sliceY(0))
    }

    companion object {
        fun smallSquare45Path() = Path(Vec3.ZERO,
            Vec3(7, 0, 4),
            Vec3(4, 0, 1),
            Vec3(1, 0, 4),
            Vec3(4, 0, 7),
            Vec3(7, 0, 4)
        )
    }
}

internal class HairyPathBuilder(
    private val material: String,
    private val step: Double = 1.0,
    private val hairLength: Double = 1.0,
    private val hairStep: Double = 1.0
): Builder<Path>() {
    override fun build(node: Path, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        val hugger = PathHugger(node, trans, world)
        var x = 0.0
        while (x < node.totalLength) {
            var z = 0.0
            while (z <= hairLength) {
                val block = context.materials.get(material)
                hugger.setBlock(x, 0.0, z, block)
                z += hairStep
            }
            x += step
        }
    }
}