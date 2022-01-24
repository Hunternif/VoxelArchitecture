package hunternif.voxarch.snapshot

import hunternif.voxarch.plan.polygonRoom
import hunternif.voxarch.sandbox.castle.MAT_ROOF
import hunternif.voxarch.sandbox.castle.builder.PyramidBuilder
import hunternif.voxarch.util.circle
import hunternif.voxarch.util.square
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class PyramidBuilderTest : BaseSnapshotTest(9, 9, 9) {
    override fun setup() {
        super.setup()
        context.builders.set(TYPE_PYRAMID to PyramidBuilder(MAT_ROOF))
        context.builders.set(
            TYPE_PYRAMID_UPSIDE_DOWN to
                PyramidBuilder(MAT_ROOF, upsideDown = true)
        )
    }

    @Test
    fun `square 45deg pyramid base`() {
        build(squarePyramid(4))
        record(out.sliceY(0))
    }

    @Test
    fun `square 45deg pyramid profile x`() {
        build(squarePyramid(4))
        record(out.sliceX(4))
    }

    @Test
    fun `square 45deg pyramid profile z`() {
        build(squarePyramid(4))
        record(out.sliceZ(4))
    }

    @Test
    fun `square 60deg pyramid base`() {
        build(squarePyramid(8))
        record(out.sliceY(0))
    }

    @Test
    fun `square 60deg pyramid profile x`() {
        build(squarePyramid(8))
        record(out.sliceX(4))
    }

    @Test
    fun `round 45deg pyramid base`() {
        build(roundPyramid(4, 8))
        record(out.sliceY(0))
    }

    @Test
    fun `round 45deg pyramid profile x1`() {
        build(roundPyramid(4, 8))
        record(out.sliceX(4))
    }

    @Test
    fun `round 45deg pyramid profile x2`() {
        build(roundPyramid(4, 8))
        record(out.sliceX(6))
    }

    @Test
    fun `round 60deg pyramid base`() {
        build(roundPyramid(8, 8))
        record(out.sliceY(0))
    }

    @Test
    fun `round 60deg pyramid profile x1`() {
        build(roundPyramid(8, 8))
        record(out.sliceX(4))
    }

    @Test
    fun `round 60deg pyramid profile x2`() {
        build(roundPyramid(8, 8))
        record(out.sliceX(6))
    }

    @Test
    fun `square 45deg pyramid upside down profile x`() {
        val pyramid = newStructure().apply {
            polygonRoom(Vec3(0, 4, 0), Vec3(8, 8, 8)).apply {
                type = TYPE_PYRAMID_UPSIDE_DOWN
                polygon.square(8.0)
                walls.forEach { it.transparent = true }
            }
        }
        build(pyramid)
        record(out.sliceX(4))
    }

    private fun squarePyramid(height: Int) = newStructure().apply {
        polygonRoom(Vec3.ZERO, Vec3(8, height, 8)).apply {
            type = TYPE_PYRAMID
            polygon.square(8.0)
            walls.forEach { it.transparent = true }
        }
    }

    private fun roundPyramid(height: Int, sides: Int) = newStructure().apply {
        polygonRoom(Vec3.ZERO, Vec3(8, height, 8)).apply {
            type = TYPE_PYRAMID
            polygon.circle(8.0, sides)
            walls.forEach { it.transparent = true }
        }
    }

    companion object {
        private const val TYPE_PYRAMID = "pyramid"
        private const val TYPE_PYRAMID_UPSIDE_DOWN = "pyramid_upside_down"
    }
}