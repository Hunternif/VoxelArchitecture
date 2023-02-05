package hunternif.voxarch.util

import hunternif.voxarch.plan.Node
import hunternif.voxarch.util.OriginSnap.*
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.assertEquals
import org.junit.Test

class OriginSnapTest {
    @Test
    fun `snap Origin`() {
        val node = Node()
        node.size = Vec3(20, 40, 60)
        val child = Node(Vec3(1, 1, 1))
        node.addChild(child)

        node.start = Vec3(100, 100, 100)
        node.snapOrigin(OFF)
        assertEquals(Vec3(0, 0, 0), node.origin)
        assertEquals(Vec3(100, 100, 100), node.start)
        assertEquals(Vec3(1, 1, 1), child.origin)

        node.snapOrigin(CORNER)
        assertEquals(Vec3(100, 100, 100), node.origin)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(-99, -99, -99), child.origin)

        node.snapOrigin(FLOOR_CENTER)
        assertEquals(Vec3(110, 100, 130), node.origin)
        assertEquals(Vec3(-10, 0, -30), node.start)
        assertEquals(Vec3(-109, -99, -129), child.origin)

        node.snapOrigin(CENTER)
        assertEquals(Vec3(110, 120, 130), node.origin)
        assertEquals(Vec3(-10, -20, -30), node.start)
        assertEquals(Vec3(-109, -119, -129), child.origin)

        node.snapOrigin(CORNER)
        assertEquals(Vec3(100, 100, 100), node.origin)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(-99, -99, -99), child.origin)

        node.snapOrigin(OFF)
        assertEquals(Vec3(100, 100, 100), node.origin)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(-99, -99, -99), child.origin)
    }
}