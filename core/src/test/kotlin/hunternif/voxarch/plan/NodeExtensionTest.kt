package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Test

class NodeExtensionTest {
    @Test
    fun finGlobalPosition() {
        val root = Node(Vec3(1, 0, 0))
        val child1 = Node(Vec3(-1, 2, 3))
        val child2 = Node(Vec3(3, 1, -6))
        root.addChild(child1)
        child1.addChild(child2)

        assertEquals(Vec3(1, 0, 0), root.findGlobalPosition())
        assertEquals(Vec3(0, 2, 3), child1.findGlobalPosition())
        assertEquals(Vec3(3, 3, -3), child2.findGlobalPosition())

        child1.origin.y += 10
        assertEquals(Vec3(0, 12, 3), child1.findGlobalPosition())
        assertEquals(Vec3(3, 13, -3), child2.findGlobalPosition())
    }
}