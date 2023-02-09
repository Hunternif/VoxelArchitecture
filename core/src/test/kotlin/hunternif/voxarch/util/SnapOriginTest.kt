package hunternif.voxarch.util

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.findGlobalPosition
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
        assertEquals(Vec3(0, 0, 0), node.position)
        assertEquals(Vec3(100, 100, 100), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(101, 101, 101), child.findGlobalPosition())

        node.snapOrigin(CORNER)
        assertEquals(Vec3(100, 100, 100), node.position)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(101, 101, 101), child.findGlobalPosition())

        node.snapOrigin(FLOOR_CENTER)
        assertEquals(Vec3(110, 100, 130), node.position)
        assertEquals(Vec3(-10, 0, -30), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(101, 101, 101), child.findGlobalPosition())

        node.snapOrigin(CENTER)
        assertEquals(Vec3(110, 120, 130), node.position)
        assertEquals(Vec3(-10, -20, -30), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(101, 101, 101), child.findGlobalPosition())

        node.snapOrigin(CORNER)
        assertEquals(Vec3(100, 100, 100), node.position)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(101, 101, 101), child.findGlobalPosition())

        node.snapOrigin(OFF)
        assertEquals(Vec3(100, 100, 100), node.position)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(101, 101, 101), child.findGlobalPosition())
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
        assertEquals(Vec3(0, 0, 0), node.position)
        assertEquals(Vec3(100, 100, 100), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(101, 101, 101), child.findGlobalPosition())

        node.snapOrigin(CORNER)
        assertEquals(Vec3(100, 100, -100), node.position)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(101, 101, 101), child.findGlobalPosition())

        node.snapOrigin(FLOOR_CENTER)
        assertEquals(Vec3(130, 100, -110), node.position)
        assertEquals(Vec3(-10, 0, -30), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(101, 101, 101), child.findGlobalPosition())

        node.snapOrigin(CENTER)
        assertEquals(Vec3(130, 120, -110), node.position)
        assertEquals(Vec3(-10, -20, -30), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(101, 101, 101), child.findGlobalPosition())

        node.snapOrigin(CORNER)
        assertEquals(Vec3(100, 100, -100), node.position)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(101, 101, 101), child.findGlobalPosition())

        node.snapOrigin(OFF)
        assertEquals(Vec3(100, 100, -100), node.position)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(101, 101, 101), child.findGlobalPosition())
    }

    @Test
    fun `snap start`() {
        val node = Node()
        node.size = Vec3(20, 40, 60)
        val child = Node(Vec3(1, 1, 1))
        node.addChild(child)

        node.start = Vec3(100, 100, 100)
        node.snapStart(OFF)
        assertEquals(Vec3(0, 0, 0), node.position)
        assertEquals(Vec3(100, 100, 100), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(101, 101, 101), child.findGlobalPosition())

        node.snapStart(CORNER)
        assertEquals(Vec3(0, 0, 0), node.position)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(1, 1, 1), child.findGlobalPosition())

        node.snapStart(FLOOR_CENTER)
        assertEquals(Vec3(0, 0, 0), node.position)
        assertEquals(Vec3(-10, 0, -30), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(-9, 1, -29), child.findGlobalPosition())

        node.snapStart(CENTER)
        assertEquals(Vec3(0, 0, 0), node.position)
        assertEquals(Vec3(-10, -20, -30), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(-9, -19, -29), child.findGlobalPosition())

        node.snapStart(CORNER)
        assertEquals(Vec3(0, 0, 0), node.position)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(1, 1, 1), child.findGlobalPosition())

        node.snapStart(OFF)
        assertEquals(Vec3(0, 0, 0), node.position)
        assertEquals(Vec3(0, 0, 0), node.start)
        assertEquals(Vec3(1, 1, 1), child.position)
        assertEquals(Vec3(1, 1, 1), child.findGlobalPosition())
    }
}