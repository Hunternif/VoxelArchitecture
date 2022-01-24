package hunternif.voxarch.plan

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NodeFactoryTest {
    private val factory = NodeFactory()

    @Before
    fun setup() {
        factory.clear()
    }

    @Test
    fun `register new nodes in order`() {
        val root = factory.newStructure()
        assertEquals(0, root.id)

        val node = factory.newNode()
        assertEquals(1, node.id)

        val room = factory.newRoom()
        assertEquals(2, room.id)
    }

    @Test
    fun `load nodes without overwriting`() {
        val node0 = factory.newNode()
        assertEquals(0, node0.id)

        val node1 = factory.newNode()
        assertEquals(1, node1.id)

        val node1Copy = factory.newNode() // this advanced the index to 3
        factory.load(1, node1Copy)
        assertEquals(3, node1Copy.id)

        val node4 = factory.newNode()
        assertEquals(4, node4.id)
    }

    @Test
    fun `load nodes advances index`() {
        val node42 = factory.newNode()
        factory.load(42, node42)
        assertEquals(42, node42.id)

        val node43 = factory.newNode()
        assertEquals(43, node43.id)
    }
}