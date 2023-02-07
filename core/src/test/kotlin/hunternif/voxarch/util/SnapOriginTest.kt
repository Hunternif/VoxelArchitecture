package hunternif.voxarch.util

import hunternif.voxarch.plan.Node
import hunternif.voxarch.util.SnapOrigin.*
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.assertEquals
import org.junit.Test

class SnapOriginTest {
    @Test
    fun `snap origin`() {
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

    @Test
    fun `snap origin on rotated node`() {
        val node = Node()
        node.size = Vec3(20, 40, 60)
        node.rotationY = 90.0
        val child = Node(Vec3(1, 1, 1))
        node.addChild(child)

        node.start = Vec3(100, 100, 100)
        node.snapOrigin(OFF)
        assertEquals(Vec3(0, 0, 0), node.origin)
        assertEquals(Vec3(100, 100, 100), node.start)
        assertEquals(Vec3(1, 1, 1), child.origin)

        node.snapOrigin(CORNER)
        assertEquals(Vec3(100, 100, -100), node.origin)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(-99, -99, -99), child.origin)

        node.snapOrigin(FLOOR_CENTER)
        assertEquals(Vec3(130, 100, -110), node.origin)
        assertEquals(Vec3(-10, 0, -30), node.start)
        assertEquals(Vec3(-109, -99, -129), child.origin)

        node.snapOrigin(CENTER)
        assertEquals(Vec3(130, 120, -110), node.origin)
        assertEquals(Vec3(-10, -20, -30), node.start)
        assertEquals(Vec3(-109, -119, -129), child.origin)

        node.snapOrigin(CORNER)
        assertEquals(Vec3(100, 100, -100), node.origin)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(-99, -99, -99), child.origin)

        node.snapOrigin(OFF)
        assertEquals(Vec3(100, 100, -100), node.origin)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(-99, -99, -99), child.origin)
    }

    @Test
    fun `snap start`() {
        val node = Node()
        node.size = Vec3(20, 40, 60)
        val child = Node(Vec3(1, 1, 1))
        node.addChild(child)

        node.start = Vec3(100, 100, 100)
        node.snapStart(OFF)
        assertEquals(Vec3(0, 0, 0), node.origin)
        assertEquals(Vec3(100, 100, 100), node.start)
        assertEquals(Vec3(1, 1, 1), child.origin)

        node.snapStart(CORNER)
        assertEquals(Vec3(0, 0, 0), node.origin)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(1, 1, 1), child.origin)

        node.snapStart(FLOOR_CENTER)
        assertEquals(Vec3(0, 0, 0), node.origin)
        assertEquals(Vec3(-10, 0, -30), node.start)
        assertEquals(Vec3(1, 1, 1), child.origin)

        node.snapStart(CENTER)
        assertEquals(Vec3(0, 0, 0), node.origin)
        assertEquals(Vec3(-10, -20, -30), node.start)
        assertEquals(Vec3(1, 1, 1), child.origin)

        node.snapStart(CORNER)
        assertEquals(Vec3(0, 0, 0), node.origin)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(1, 1, 1), child.origin)

        node.snapStart(OFF)
        assertEquals(Vec3(0, 0, 0), node.origin)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(1, 1, 1), child.origin)
    }
}