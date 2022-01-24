package hunternif.voxarch.plan

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NodeRegistryTest {
    @Before
    fun setup() {
        Node.staticRegistry.clear()
    }

    @Test
    fun `register new nodes in order`() {
        val root = Structure()
        assertEquals(0, root.id)

        val node = Node()
        assertEquals(1, node.id)

        val room = Room()
        assertEquals(2, room.id)
    }

    @Test
    fun `load nodes without overwriting`() {
        val node0 = Node()
        assertEquals(0, node0.id)

        val node1 = Node()
        assertEquals(1, node1.id)

        val node1Copy = Node() // this advanced the index to 3
        Node.staticRegistry.load(1, node1Copy)
        assertEquals(3, node1Copy.id)

        val node4 = Node()
        assertEquals(4, node4.id)
    }

    @Test
    fun `load nodes advances index`() {
        val node42 = Node()
        Node.staticRegistry.load(42, node42)
        assertEquals(42, node42.id)

        val node43 = Node()
        assertEquals(43, node43.id)
    }
}