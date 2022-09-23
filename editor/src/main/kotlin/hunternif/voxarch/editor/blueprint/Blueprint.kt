package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.util.IDRegistry
import hunternif.voxarch.editor.util.WithID
import hunternif.voxarch.generator.ChainedGenerator
import hunternif.voxarch.generator.GenPassthrough
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

    val nodeIDs = IDRegistry<BlueprintNode>()
    val slotIDs = IDRegistry<BlueprintSlot>()
    internal val linkIDs = IDRegistry<BlueprintLink>()

    val start: BlueprintNode = createNode("Start", GenPassthrough()).apply {
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
        node.inputs.forEach { it.unlink() }
        node.outputs.forEach { it.unlink() }
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
            (node.generator as? ChainedGenerator)?.nextGens?.add(dest.node.generator)
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