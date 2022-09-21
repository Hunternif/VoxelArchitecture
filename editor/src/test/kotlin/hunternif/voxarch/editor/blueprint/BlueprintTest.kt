package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.generator.ChainedGenerator
import hunternif.voxarch.plan.Node
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BlueprintTest {
    private lateinit var bp: Blueprint
    private lateinit var node1: BlueprintNode
    private lateinit var node2: BlueprintNode
    private lateinit var node3: BlueprintNode

    @Before
    fun setup() {
        bp = Blueprint(0, "test blueprint")
        node1 = bp.newNode(generator)
        node2 = bp.newNode(generator)
        node3 = bp.newNode(generator)
    }

    @Test
    fun `create link`() {
        node1.output.linkTo(node2.input)
        assertEquals(1, bp.linkIDs.map.size)
        val link12 = node1.output.link!!
        assertEquals(link12, node2.input.link)
        assertEquals(node1.output, link12.from)
        assertEquals(node2.input, link12.to)
        assertEquals(setOf(link12), bp.links)
    }

    @Test
    fun `unlink from source slot`() {
        node1.output.linkTo(node2.input)
        assertEquals(1, bp.linkIDs.map.size)
        node1.output.unlink()
        assertNull(node1.output.link)
        assertNull(node2.input.link)
        assertEquals("link should be deleted from registry",
            0, bp.linkIDs.map.size)
        assertEquals(emptySet<BlueprintLink>(), bp.links)
    }

    @Test
    fun `unlink from destination slot`() {
        node1.output.linkTo(node2.input)
        assertEquals(1, bp.linkIDs.map.size)
        node2.input.unlink()
        assertNull(node1.output.link)
        assertNull(node2.input.link)
        assertEquals("link should be deleted from registry",
            0, bp.linkIDs.map.size)
        assertEquals(emptySet<BlueprintLink>(), bp.links)
    }

    @Test
    fun `remove node should delete link`() {
        node1.output.linkTo(node2.input)
        assertEquals(1, bp.linkIDs.map.size)
        assertEquals(setOf(node1, node2, node3), bp.nodes)

        bp.removeNode(node2)
        assertEquals(setOf(node1, node3), bp.nodes)
        assertNull(node1.output.link)
        assertEquals("link should be deleted from registry",
            0, bp.linkIDs.map.size)
        assertEquals(emptySet<BlueprintLink>(), bp.links)
    }

    @Test
    fun `relink to another node`() {
        node1.output.linkTo(node2.input)
        val link12 = node1.output.link!!
        assertEquals(1, bp.linkIDs.map.size)
        assertEquals(setOf(link12), bp.links)

        node1.output.linkTo(node3.input)
        val link13 = node1.output.link!!
        assertEquals(1, bp.linkIDs.map.size)
        assertEquals(setOf(link13), bp.links)

        assertEquals(link13, node3.input.link)
        assertEquals(node1.output, link13.from)
        assertEquals(node3.input, link13.to)
        assertNull(node2.input.link)
    }

    private val generator = object : ChainedGenerator() {
        override fun generateChained(
            parent: DomBuilder<Node?>,
            nextBlock: DomBuilder<Node?>.() -> Unit,
        ) {}
    }
}