package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.style.Rule
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.editor.util.IDRegistry
import hunternif.voxarch.editor.util.WithID
import hunternif.voxarch.plan.Node
import imgui.extension.imnodes.ImNodes
import kotlin.collections.LinkedHashSet

/**
 * Represents a graph of DOM Generators.
 */
class Blueprint(
    override val id: Int,
    var name: String,
) : WithID {
    val nodes = LinkedHashSet<BlueprintNode>()
    val links = LinkedHashSet<BlueprintLink>()

    val nodeIDs = IDRegistry<BlueprintNode>()
    val slotIDs = IDRegistry<BlueprintSlot>()
    internal val linkIDs = IDRegistry<BlueprintLink>()

    val start: BlueprintNode = createNode("Start", 100f, 100f) { it }.apply {
        addOutput("node")
    }

    private fun createNode(
        name: String,
        x: Float = 0f,
        y: Float = 0f,
        createBuilder: (parent: DomBuilder) -> DomBuilder,
    ): BlueprintNode {
        val node = BlueprintNode(nodeIDs.newID(), name, this, createBuilder)
        node.x = x
        node.y = y
        nodeIDs.save(node)
        nodes.add(node)
        return node
    }

    fun addNode(
        name: String,
        x: Float = 0f,
        y: Float = 0f,
        createBuilder: (parent: DomBuilder) -> DomBuilder,
    ): BlueprintNode = createNode(name, x, y, createBuilder).apply {
        addInput("in")
        addOutput("out")
    }

    fun removeNode(node: BlueprintNode) {
        if (node == start) return
        node.inputs.forEach { it.links.toList().forEach { it.unlink() } }
        node.outputs.forEach { it.links.toList().forEach { it.unlink() } }
        nodes.remove(node)
        // not removing from the ID registry, in case of undo
    }

    fun execute(
        stylesheet: Stylesheet = defaultStyle,
        seed: Long = 0,
        rootNode: Node,
    ) {
        DomBuilder.cycleCounter.clear()
        // Creates a new detached DOM root and generates on it.
        // Not recommended, because this will ignore styles or nested generators.
        val root = domRoot(stylesheet, seed, rootNode)
        start.assembleDom(root)
        root.buildDom()
    }
}

/**
 * [DomBuilder] is the main payload of this node. It will be created at runtime.
 */
class BlueprintNode(
    override val id: Int,
    val name: String,
    val bp: Blueprint,
    private val createBuilder: (parent: DomBuilder) -> DomBuilder,
) : WithID {
    val rule: Rule = Rule()
    val inputs = mutableListOf<BlueprintSlot.In>()
    val outputs = mutableListOf<BlueprintSlot.Out>()
    var x: Float = 0f
    var y: Float = 0f

    internal fun addInput(name: String): BlueprintSlot.In =
        BlueprintSlot.In(name, this).also { inputs.add(it) }

    internal fun addOutput(name: String): BlueprintSlot.Out =
        BlueprintSlot.Out(name, this).also { outputs.add(it) }

    fun applyImNodesPos() {
        ImNodes.setNodeGridSpacePos(id, x, y)
    }

    /** Assembles a DOM tree out of the connected BP nodes. */
    fun assembleDom(parent: DomBuilder): DomBuilder =
        assembleDomRecursive(parent, mutableMapOf())

    /** Guards against recursion. */
    private fun assembleDomRecursive(
        parent: DomBuilder,
        visited: MutableMap<BlueprintNode, DomBuilder>,
    ): DomBuilder {
        val bld = createBuilder(parent)
        visited[this] = bld
        if (parent != bld) parent.addChild(bld)
        outputs.flatMap { it.links }.map { it.to.node }.forEach { next ->
            if (next in visited) bld.addChild(visited[next]!!) // cycle
            else next.assembleDomRecursive(bld, visited)
        }
        return bld
    }
}

sealed class BlueprintSlot(
    val name: String,
    val bp: Blueprint,
    val node: BlueprintNode,
    override val id: Int = bp.slotIDs.newID(),
) : WithID {
    init {
        bp.slotIDs.save(this)
    }
    val links = LinkedHashSet<BlueprintLink>()

    class In(name: String, node: BlueprintNode) : BlueprintSlot(name, node.bp, node)

    class Out(name: String, node: BlueprintNode) : BlueprintSlot(name, node.bp, node) {
        fun linkTo(dest: In): BlueprintLink {
            val existingLink = links.firstOrNull { it.from == this && it.to == dest }
            if (existingLink != null) return existingLink

            val newLink = BlueprintLink(bp.linkIDs.newID(), this, dest)
            bp.linkIDs.save(newLink)
            bp.links.add(newLink)
            this.links.add(newLink)
            dest.links.add(newLink)
            return newLink
        }
    }

    fun unlinkFrom(other: BlueprintSlot) {
        val link = links.firstOrNull {
            it.from == this && it.to == other || it.from == other && it.to == this
        }
        link?.let {
            this.links.remove(it)
            other.links.remove(it)
            // remove from ID Registry, because links are cheap to re-create
            bp.linkIDs.remove(it)
            bp.links.remove(it)
        }
    }
}

class BlueprintLink(
    override val id: Int,
    val from: BlueprintSlot.Out,
    val to: BlueprintSlot.In,
) : WithID {
    fun unlink() {
        to.unlinkFrom(from)
    }
}