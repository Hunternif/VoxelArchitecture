package hunternif.voxarch.snapshot

import hunternif.voxarch.plan.Structure
import hunternif.voxarch.plan.floor
import hunternif.voxarch.plan.room
import hunternif.voxarch.sandbox.castle.MAT_FLOOR
import hunternif.voxarch.sandbox.castle.builder.FloorFoundationBuilder
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class FloorTest : BaseSnapshotTest(10, 1, 10) {
    @Test
    fun `simple floor offset 1`() {
        val structure = Structure().apply {
            room(Vec3(2, 0, 2), Vec3(width - 3, 0, length - 3)) {
                floor(Vec3( 1, 0, 1), Vec3(123, 0, 56))
            }
        }
        build(structure)
        record(out.sliceY(0))
    }

    @Test
    fun `simple floor offset 2`() {
        val structure = Structure().apply {
            room(Vec3(2, 0, 2), Vec3(width - 3, 0, length - 3)) {
                floor(Vec3( 234, 0, -56), Vec3(34, 0, 2))
            }
        }
        build(structure)
        record(out.sliceY(0))
    }

    @Test
    fun `foundation floor offset 1`() {
        out.safeBoundary = true
        context.builders.apply {
            setDefault(FloorFoundationBuilder(MAT_FLOOR))
        }
        `simple floor offset 1`()
    }

    @Test
    fun `foundation floor offset 2`() {
        out.safeBoundary = true
        context.builders.apply {
            setDefault(FloorFoundationBuilder(MAT_FLOOR))
        }
        `simple floor offset 2`()
    }
}