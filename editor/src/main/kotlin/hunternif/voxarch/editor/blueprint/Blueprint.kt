package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.util.IDRegistry
import hunternif.voxarch.editor.util.WithID
import hunternif.voxarch.generator.ChainedGenerator
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.Node
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
    var start: BlueprintNode? = null

    val nodeIDs = IDRegistry<BlueprintNode>()
    val slotIDs = IDRegistry<BlueprintSlot>()
    internal val linkIDs = IDRegistry<BlueprintLink>()

    fun newNode(generator: ChainedGenerator, x: Float = 0f, y: Float = 0f): BlueprintNode {
        val node = BlueprintNode(nodeIDs.newID(), generator, this)
        node.addInput("input")
        node.addOutput("out 1")
        node.x = x
        node.y = y
        nodeIDs.save(node)
        nodes.add(node)
        if (start == null) {
            start = node
        }
        return node
    }

    fun removeNode(node: BlueprintNode) {
        if (node == start) {
            start = node.outputs.firstOrNull()?.link?.to?.node
                ?: nodes.firstOrNull { it != node }
        }
        node.inputs.forEach { it.unlink() }
        node.outputs.forEach { it.unlink() }
        nodes.remove(node)
        // not removing from the ID registry, in case of undo
    }

    fun execute(root: Node) {
        start?.generator?.generateFinal(root)
    }
}

class BlueprintNode(
    override val id: Int,
    val generator: IGenerator,
    val bp: Blueprint,
) : WithID {
    val inputs = mutableListOf<BlueprintSlot.In>()
    val outputs = mutableListOf<BlueprintSlot.Out>()
    var x: Float = 0f
    var y: Float = 0f

    val name: String = generator.javaClass.simpleName

    internal fun addInput(name: String): BlueprintSlot.In =
        BlueprintSlot.In(name, this).also { inputs.add(it) }

    internal fun addOutput(name: String): BlueprintSlot.Out =
        BlueprintSlot.Out(name, this).also { outputs.add(it) }
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
    var link: BlueprintLink? = null

    class In(name: String, node: BlueprintNode) : BlueprintSlot(name, node.bp, node)

    class Out(name: String, node: BlueprintNode) : BlueprintSlot(name, node.bp, node) {
        fun linkTo(dest: In) {
            unlink()
            val newLink = BlueprintLink(bp.linkIDs.newID(), this, dest)
            bp.linkIDs.save(newLink)
            bp.links.add(newLink)
            this.link = newLink
            dest.link = newLink
        }
    }

    fun unlink() {
        link?.run {
            to.link = null
            from.link = null
            // remove from ID Registry, because links are cheap to re-create
            bp.linkIDs.remove(this)
            bp.links.remove(this)
        }
        link = null
    }
}

class BlueprintLink(
    override val id: Int,
    val from: BlueprintSlot.Out,
    val to: BlueprintSlot.In,
) : WithID