package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.util.IDRegistry
import hunternif.voxarch.editor.util.WithID
import hunternif.voxarch.generator.ChainedGenerator
import hunternif.voxarch.generator.GenPassthrough
import hunternif.voxarch.generator.IGenerator
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

    val start: BlueprintNode = createNode("Start", GenPassthrough(), 100f, 100f).apply {
        addOutput("node")
    }

    private fun createNode(
        name: String,
        generator: IGenerator,
        x: Float = 0f,
        y: Float = 0f
    ): BlueprintNode {
        val node = BlueprintNode(nodeIDs.newID(), name, generator, this)
        node.x = x
        node.y = y
        nodeIDs.save(node)
        nodes.add(node)
        return node
    }

    fun addNode(
        name: String,
        generator: IGenerator,
        x: Float = 0f,
        y: Float = 0f
    ): BlueprintNode = createNode(name, generator, x, y).apply {
        addInput("in")
        if (generator is ChainedGenerator) {
            addOutput("out")
        }
    }

    fun removeNode(node: BlueprintNode) {
        if (node == start) return
        node.inputs.forEach { it.links.toList().forEach { it.unlink() } }
        node.outputs.forEach { it.links.toList().forEach { it.unlink() } }
        nodes.remove(node)
        // not removing from the ID registry, in case of undo
    }

    fun execute(root: Node) {
        start.generator.generateFinal(root)
    }
}

class BlueprintNode(
    override val id: Int,
    val name: String,
    val generator: IGenerator,
    val bp: Blueprint,
) : WithID {
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
            (node.generator as? ChainedGenerator)?.nextGens?.add(dest.node.generator)
            return newLink
        }
    }

    fun unlinkFrom(other: BlueprintSlot) {
        val link = links.firstOrNull {
            it.from == this && it.to == other || it.from == other && it.to== this
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