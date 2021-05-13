package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.line
import hunternif.voxarch.sandbox.castle.MAT_ROOF
import hunternif.voxarch.vector.Vec3
import org.junit.Test
import kotlin.math.round

class BuilderExtensionTest : BaseSnapshotTest(10, 10, 10) {
    @Test
    fun `line x`() {
        line(Vec3.ZERO, Vec3(9, 0, 0), 1.0) {
            putBlock(it)
        }
        record(out.sliceY(0))
    }

    @Test
    fun `line z`() {
        line(Vec3.ZERO, Vec3(0, 0, 9), 1.0) {
            putBlock(it)
        }
        record(out.sliceY(0))
    }

    @Test
    fun `line xz`() {
        line(Vec3.ZERO, Vec3(9, 0, 9), 0.5) {
            putBlock(it)
        }
        record(out.sliceY(0))
    }

    @Test
    fun `line xz with offset`() {
        line(Vec3.ZERO, Vec3(9, 0, 9), 0.5, 1.0) {
            putBlock(it)
        }
        record(out.sliceY(0))
    }

    @Test
    fun `line xy`() {
        line(Vec3.ZERO, Vec3(9, 9, 0), 0.5) {
            putBlock(it)
        }
        record(out.sliceZ(0))
    }

    private fun putBlock(pos: Vec3) {
        val x = round(pos.x).toInt()
        val y = round(pos.y).toInt()
        val z = round(pos.z).toInt()
        val block = context.materials.get(MAT_ROOF)
        out.setBlock(x, y, z, block)
    }
}