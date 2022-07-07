package hunternif.voxarch.snapshot

import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.plan.polygonRoom
import hunternif.voxarch.util.circle
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class PathExtensionsTest: BaseSnapshotTest(20, 1, 20) {
    @Test
    fun `circles of various sizes`() {
        for (w in 2..19) {
            out.clearAll()
            val plan = Structure().apply {
                polygonRoom(
                    Vec3(1, 0, 1),
                    Vec3(w, 0, w)
                ) {
                    shape = PolygonShape.ROUND
                    polygon.circle(w.toDouble())
                    createWalls()
                }
            }
            build(plan)
            record(out.sliceY(0), "circle $w")
        }
    }
}