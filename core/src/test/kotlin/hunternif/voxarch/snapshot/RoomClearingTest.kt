package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.RoomBuilder
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.plan.room
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class RoomClearingTest: BaseSnapshotTest(10, 1, 10) {

    override fun setup() {
        super.setup()
        context.builders.setDefault(RoomBuilder())

        val fillBlock = context.materials.get(MAT_FLOOR)
        for (x in 0 until width) {
            for (z in 0 until length) {
                for (y in 0 until height) {
                    out.setBlock(x, y, z, fillBlock)
                }
            }
        }
    }

    @Test
    fun `straight 3x5`() {
        val plan = Structure().apply {
            room(Vec3.ZERO, Vec3(3, 0, 5)) {
                createFourWalls()
                walls.forEach { it.transparent = true }
            }
        }
        build(plan)
        record(out.sliceY(0))
    }

    @Test
    fun `straight 4x6`() {
        val plan = Structure().apply {
            room(Vec3.ZERO, Vec3(4, 0, 6)) {
                createFourWalls()
                walls.forEach { it.transparent = true }
            }
        }
        build(plan)
        record(out.sliceY(0))
    }

    @Test
    fun `rotated 3x5 by 45`() {
        val plan = Structure().apply {
            room(Vec3(2, 0, 2), Vec3(5, 0, 7)) {
                createFourWalls()
                walls.forEach { it.transparent = true }
                rotationY = 45.0
            }
        }
        build(plan)
        record(out.sliceY(0))
    }
}