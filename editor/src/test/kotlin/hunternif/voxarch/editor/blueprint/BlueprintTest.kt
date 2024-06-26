package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.DomBuildContext
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.style.StyledElement
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
        bp = Blueprint("test blueprint")
        node1 = bp.addNode("test", DomBuilderCounter("one"))
        node2 = bp.addNode("test", DomBuilderCounter("two"))
        node3 = bp.addNode("test", DomBuilderCounter("three"))
        generatedCount = 0
    }

    @Test
    fun `create link`() {
        node1.outputs[0].linkTo(node2.inputs[0])
        assertEquals(1, bp.linkIDs.map.size)
        val link12 = node1.outputs[0].links.first()
        assertEquals(setOf(link12), node2.inputs[0].links)
        assertEquals(node1.outputs[0], link12.from)
        assertEquals(node2.inputs[0], link12.to)
        assertEquals(setOf(link12), bp.links)
    }

    @Test
    fun `unlink from source slot`() {
        val link12 = node1.outputs[0].linkTo(node2.inputs[0])
        assertEquals(1, bp.linkIDs.map.size)
        link12.unlink()
        assertEquals(emptySet<BlueprintLink>(), node1.outputs[0].links)
        assertEquals(emptySet<BlueprintLink>(), node2.inputs[0].links)
        assertEquals("link should be deleted from registry",
            0, bp.linkIDs.map.size)
        assertEquals(emptySet<BlueprintLink>(), bp.links)
    }

    @Test
    fun `unlink from destination slot`() {
        val link12 = node1.outputs[0].linkTo(node2.inputs[0])
        assertEquals(1, bp.linkIDs.map.size)
        link12.unlink()
        assertEquals(emptySet<BlueprintLink>(), node1.outputs[0].links)
        assertEquals(emptySet<BlueprintLink>(), node2.inputs[0].links)
        assertEquals("link should be deleted from registry",
            0, bp.linkIDs.map.size)
        assertEquals(emptySet<BlueprintLink>(), bp.links)
    }

    @Test
    fun `remove source node should delete link`() {
        node1.outputs[0].linkTo(node2.inputs[0])
        assertEquals(1, bp.linkIDs.map.size)
        assertEquals(setOf(bp.start, node1, node2, node3), bp.nodes)

        bp.removeNode(node1)
        assertEquals(setOf(bp.start, node2, node3), bp.nodes)
        assertEquals(emptySet<BlueprintLink>(), node1.outputs[0].links)
        assertEquals("link should be deleted from registry",
            0, bp.linkIDs.map.size)
        assertEquals(emptySet<BlueprintLink>(), bp.links)
    }

    @Test
    fun `remove destination node should delete link`() {
        node1.outputs[0].linkTo(node2.inputs[0])
        assertEquals(1, bp.linkIDs.map.size)
        assertEquals(setOf(bp.start, node1, node2, node3), bp.nodes)

        bp.removeNode(node2)
        assertEquals(setOf(bp.start, node1, node3), bp.nodes)
        assertEquals(emptySet<BlueprintLink>(), node1.outputs[0].links)
        assertEquals("link should be deleted from registry",
            0, bp.linkIDs.map.size)
        assertEquals(emptySet<BlueprintLink>(), bp.links)
    }

    @Test
    fun `relink to another node`() {
        node1.outputs[0].linkTo(node2.inputs[0])
        val link12 = node1.outputs[0].links.first()
        assertEquals(1, bp.linkIDs.map.size)
        assertEquals(setOf(link12), bp.links)

        node1.outputs[0].linkTo(node3.inputs[0])
        val link13 = node3.inputs[0].links.first()
        assertEquals(2, bp.linkIDs.map.size)
        assertEquals(setOf(link12, link13), bp.links)

        assertEquals(setOf(link13), node3.inputs[0].links)
        assertEquals(node1.outputs[0], link13.from)
        assertEquals(node3.inputs[0], link13.to)
        assertEquals(setOf(link12), node2.inputs[0].links)
        assertEquals(setOf(link12, link13), node1.outputs[0].links)
    }

    @Test
    fun `execute a blueprint with a cycle`() {
        bp.start.outputs[0].linkTo(node1.inputs[0])
        node1.outputs[0].linkTo(node2.inputs[0])
        node2.outputs[0].linkTo(node3.inputs[0])
        node3.outputs[0].linkTo(node1.inputs[0])
        assertEquals(0, generatedCount)
        bp.execute(rootNode = Node(), maxRecursions = 20)
        assertEquals(60, generatedCount)
        // execute twice to ensure the counters are cleared
        generatedCount = 0
        bp.execute(rootNode = Node(), maxRecursions = 20)
        assertEquals(60, generatedCount)
    }

    private class DomBuilderCounter(val name: String) : DomBuilder() {
        override fun prepareForLayout(ctx: DomBuildContext): StyledElement<*> {
            generatedCount++
            val count = generatedCountMap.getOrDefault(this, 0)
            generatedCountMap[this] = count + 1
            return super.prepareForLayout(ctx)
        }
    }

    companion object {
        private var generatedCount = 0
        private val generatedCountMap = mutableMapOf<DomBuilder, Int>()
    }
}