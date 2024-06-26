package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.MAT_FLOOR
import hunternif.voxarch.plan.Floor
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.plan.floor
import hunternif.voxarch.plan.room
import hunternif.voxarch.sandbox.castle.builder.FloorFoundationBuilder
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class FloorTest : BaseSnapshotTest(10, 1, 10) {
    @Test
    fun `simple floor`() {
        val structure = Structure().apply {
            room(Vec3(2, 0, 2), Vec3(outWidth - 3, 0, outDepth - 3)) {
                floor()
            }
        }
        build(structure)
        record(out.sliceY(0))
    }

    @Test
    fun `foundation floor`() {
        out.safeBoundary = true
        context.builders.apply {
            setDefault<Floor>(FloorFoundationBuilder(MAT_FLOOR))
        }
        `simple floor`()
    }
}