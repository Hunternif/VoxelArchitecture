package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.RoomBuilder
import hunternif.voxarch.plan.centeredRoom
import hunternif.voxarch.plan.room
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class RoomClearingTest: BaseSnapshotTest(10, 1, 10) {

    override fun setup() {
        super.setup()
        context.builders.setDefault(RoomBuilder())

        val fillBlock = context.materials.get(MAT_FLOOR)
        for (x in 0 until outWidth) {
            for (z in 0 until outLength) {
                for (y in 0 until outHeight) {
                    out.setBlock(x, y, z, fillBlock)
                }
            }
        }
    }

    @Test
    fun `straight 3x5`() {
        val plan = newStructure().apply {
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
        val plan = newStructure().apply {
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
        val plan = newStructure().apply {
            centeredRoom(Vec3(3.5, 0.0, 4.5), Vec3(3, 0, 5)) {
                createFourWalls()
                walls.forEach { it.transparent = true }
                rotationY = 45.0
            }
        }
        build(plan)
        record(out.sliceY(0))
    }
}