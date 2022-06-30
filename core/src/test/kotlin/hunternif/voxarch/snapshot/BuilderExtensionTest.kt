package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.line2
import hunternif.voxarch.sandbox.castle.MAT_ROOF
import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class BuilderExtensionTest : BaseSnapshotTest(10, 10, 10) {
    @Test
    fun `line x`() {
        line2(Vec3.ZERO, Vec3(9, 0, 0)) {
            putBlock(it)
        }
        record(out.sliceY(0))
    }

    @Test
    fun `line z`() {
        line2(Vec3.ZERO, Vec3(0, 0, 9)) {
            putBlock(it)
        }
        record(out.sliceY(0))
    }

    @Test
    fun `line xz`() {
        line2(Vec3.ZERO, Vec3(9, 0, 9)) {
            putBlock(it)
        }
        record(out.sliceY(0))
    }

    @Test
    fun `line xz with offset`() {
        line2(Vec3(1, 0, 1), Vec3(8, 0, 8)) {
            putBlock(it)
        }
        record(out.sliceY(0))
    }

    @Test
    fun `line xy`() {
        line2(Vec3.ZERO, Vec3(9, 9, 0)) {
            putBlock(it)
        }
        record(out.sliceZ(0))
    }

    private fun putBlock(pos: IntVec3) {
        val block = context.materials.get(MAT_ROOF)
        out.setBlock(pos, block)
    }
}