package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.editor.util.IDRegistry
import hunternif.voxarch.editor.util.WithID
import hunternif.voxarch.plan.Node
import imgui.extension.imnodes.ImNodes
import kotlin.collections.LinkedHashSet

/**
 * Represents a graph of DomBuilders.
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

    private val internalStylesheet = Stylesheet()

    val start: BlueprintNode = createNode(0, "Start", 100f, 100f, DomBuilder()).apply {
        addOutput("node")
    }

    internal fun createNode(
        id: Int,
        name: String,
        x: Float = 0f,
        y: Float = 0f,
        domBuilder: DomBuilder,
    ): BlueprintNode {
        val node = BlueprintNode(id, name, this, domBuilder, x, y)
        nodeIDs.save(node)
        nodes.add(node)
        domBuilder.addStyle(node.autoStyleClass)
        internalStylesheet.addRule(node.rule)
        return node
    }

    fun addNode(
        name: String,
        domBuilder: DomBuilder,
        x: Float = 0f,
        y: Float = 0f,
    ): BlueprintNode = createNode(nodeIDs.newID(), name, x, y, domBuilder).apply {
        addInput("in")
        addOutput("out", domBuilder)
        domBuilder.slots.forEach { (name, domSlot) ->
            addOutput(name, domSlot)
        }
    }

    fun removeNode(node: BlueprintNode) {
        if (node == start) return
        node.inputs.forEach { it.links.toList().forEach { it.unlink() } }
        node.outputs.forEach { it.links.toList().forEach { it.unlink() } }
        nodes.remove(node)
        // not removing from the ID registry, in case of undo
    }

    fun execute(
        rootNode: Node,
        stylesheet: Stylesheet = defaultStyle,
        seed: Long = 0,
        maxRecursions: Int = 4,
    ) {
        // copy the incoming stylesheet to keep it clean from generated rules:
        val finalStylesheet = stylesheet.copy()
        finalStylesheet.copyRules(internalStylesheet)
        val root = domRoot(rootNode)
        root.addChild(start.domBuilder)
        root.buildDom(finalStylesheet, seed, maxRecursions)
    }
}

/**
 * [DomBuilder] is the main payload of this node. It will be created at runtime.
 */
class BlueprintNode(
    override val id: Int,
    val name: String,
    val bp: Blueprint,
    val domBuilder: DomBuilder,
    var x: Float = 0f,
    var y: Float = 0f,
) : WithID {
    val autoStyleClass = "${name}_${id}"
    val rule: Rule = Rule(select(autoStyleClass))
    val inputs = mutableListOf<BlueprintSlot.In>()
    val outputs = mutableListOf<BlueprintSlot.Out>()

    private val extraClassList = linkedSetOf<String>()
    var extraStyleClass = ""
        set(value) {
            field = value
            domBuilder.styleClass.removeAll(extraClassList)
            extraClassList.apply {
                clear()
                addAll(value.split(' '))
                remove("")
            }
            domBuilder.styleClass.addAll(extraClassList)
        }

    internal fun addInput(name: String): BlueprintSlot.In {
        val id = bp.slotIDs.newID()
        return BlueprintSlot.In(id, name, this).also {
            inputs.add(it)
            bp.slotIDs.save(it)
        }
    }

    internal fun addOutput(
        name: String, domSlot: DomBuilder = domBuilder,
    ): BlueprintSlot.Out {
        val id = bp.slotIDs.newID()
        return BlueprintSlot.Out(id, name, this, domSlot).also {
            outputs.add(it)
            bp.slotIDs.save(it)
        }
    }

    fun applyImNodesPos() {
        ImNodes.setNodeGridSpacePos(id, x, y)
    }
}

sealed class BlueprintSlot(
    override val id: Int,
    val name: String,
    val bp: Blueprint,
    val node: BlueprintNode,
) : WithID {
    val links = LinkedHashSet<BlueprintLink>()

    class In(id: Int, name: String, node: BlueprintNode)
        : BlueprintSlot(id, name, node.bp, node)

    class Out(
        id: Int,
        name: String,
        node: BlueprintNode,
        val domSlot: DomBuilder,
    ) : BlueprintSlot(id, name, node.bp, node) {
        fun linkTo(dest: In): BlueprintLink {
            val existingLink = links.firstOrNull { it.from == this && it.to == dest }
            if (existingLink != null) return existingLink

            val newLink = BlueprintLink(bp.linkIDs.newID(), this, dest)
            bp.linkIDs.save(newLink)
            bp.links.add(newLink)
            this.links.add(newLink)
            dest.links.add(newLink)
            domSlot.addChild(dest.node.domBuilder)
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
            it.from.domSlot.removeChild(it.to.node.domBuilder)
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