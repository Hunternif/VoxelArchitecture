package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.MAT_ROOF
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.plan.centeredPolyRoom
import hunternif.voxarch.sandbox.castle.builder.PyramidBuilder
import hunternif.voxarch.util.circle
import hunternif.voxarch.util.square
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class PyramidBuilderTest : BaseSnapshotTest(9, 9, 9) {
    override fun setup() {
        super.setup()
        context.builders.set<Room>(TYPE_PYRAMID to PyramidBuilder(MAT_ROOF))
        context.builders.set<Room>(
            TYPE_PYRAMID_UPSIDE_DOWN to
                PyramidBuilder(MAT_ROOF, upsideDown = true)
        )
    }

    @Test
    fun `square 45deg pyramid`() {
        build(squarePyramid(4))
        recordVox()
        record(out.sliceY(0))
        record(out.sliceX(4))
        record(out.sliceZ(4))
    }

    @Test
    fun `square 60deg pyramid`() {
        build(squarePyramid(8))
        recordVox()
        record(out.sliceY(0))
        record(out.sliceX(4))
        record(out.sliceZ(4))
    }

    @Test
    fun `round 45deg pyramid`() {
        build(roundPyramid(4, 8))
        recordVox()
        record(out.sliceY(0))
        record(out.sliceX(4))
        record(out.sliceX(6))
    }

    @Test
    fun `round 60deg pyramid`() {
        build(roundPyramid(8, 8))
        recordVox()
        record(out.sliceY(0))
        record(out.sliceX(4))
        record(out.sliceX(6))
    }

    @Test
    fun `square 45deg pyramid upside down`() {
        val pyramid = Structure().apply {
            centeredPolyRoom(Vec3(4, 4, 4), Vec3(8, 4, 8)) {
                tags += TYPE_PYRAMID_UPSIDE_DOWN
                polygon.square(8.0)
                walls.forEach { it.transparent = true }
            }
        }
        build(pyramid)
        recordVox()
        record(out.sliceX(4))
    }

    private fun squarePyramid(height: Int) = Structure().apply {
        centeredPolyRoom(Vec3(4, 0, 4), Vec3(8, height, 8)) {
            tags += TYPE_PYRAMID
            polygon.square(8.0)
            walls.forEach { it.transparent = true }
        }
    }

    private fun roundPyramid(height: Int, sides: Int) = Structure().apply {
        centeredPolyRoom(Vec3(4, 0, 4), Vec3(8, height, 8)) {
            tags += TYPE_PYRAMID
            polygon.circle(8.0, sides)
            walls.forEach { it.transparent = true }
        }
    }

    companion object {
        private const val TYPE_PYRAMID = "pyramid"
        private const val TYPE_PYRAMID_UPSIDE_DOWN = "pyramid_upside_down"
    }
}