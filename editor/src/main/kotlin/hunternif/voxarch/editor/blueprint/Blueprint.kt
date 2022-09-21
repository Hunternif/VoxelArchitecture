package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.util.IDRegistry
import hunternif.voxarch.editor.util.WithID
import hunternif.voxarch.generator.ChainedGenerator
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
    internal val slotIDs = IDRegistry<BlueprintSlot>()
    internal val linkIDs = IDRegistry<BlueprintLink>()

    fun newNode(generator: ChainedGenerator): BlueprintNode {
        val node = BlueprintNode(nodeIDs.newID(), generator, this)
        nodeIDs.save(node)
        nodes.add(node)
        if (start == null) {
            start = node
        }
        return node
    }

    fun removeNode(node: BlueprintNode) {
        node.input.unlink()
        node.output.unlink()
        nodes.remove(node)
        // not removing from the ID registry, in case of undo
    }

    fun execute(root: Node) {
        start?.generator?.generateFinal(root)
    }
}

class BlueprintNode(
    override val id: Int,
    val generator: ChainedGenerator,
    bp: Blueprint,
) : WithID {
    val input = BlueprintSlot.In(bp)
    val output = BlueprintSlot.Out(bp)

    val name: String = generator.javaClass.simpleName
}

sealed class BlueprintSlot(
    val bp: Blueprint,
    override val id: Int = bp.slotIDs.newID(),
) : WithID {
    init {
        bp.slotIDs.save(this)
    }
    var link: BlueprintLink? = null

    class In(bp: Blueprint) : BlueprintSlot(bp)

    class Out(bp: Blueprint) : BlueprintSlot(bp) {
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