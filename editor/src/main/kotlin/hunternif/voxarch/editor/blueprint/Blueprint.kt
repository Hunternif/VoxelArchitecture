package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.IDomListener
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.IDRegistry
import hunternif.voxarch.editor.util.WithID
import hunternif.voxarch.plan.Node
import imgui.extension.imnodes.ImNodes
import java.util.LinkedList
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

    internal val internalStylesheet = Stylesheet()

    val start: BlueprintNode = createNode(0, "Start", 100f, 100f, DomBuilder()).apply {
        addOutput("node")
    }

    /** Contains nodes with [DomBlueprintOutSlot]. */
    val outNodes = mutableListOf<BlueprintNode>()

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
        if (domBuilder is DomBlueprintOutSlot) {
            outNodes.add(node)
        }
        return node
    }

    fun addNode(
        name: String,
        domBuilder: DomBuilder,
        x: Float = 0f,
        y: Float = 0f,
    ): BlueprintNode = createNode(nodeIDs.newID(), name, x, y, domBuilder).apply {
        addInput("in")
        when (domBuilder) {
            is DomRunBlueprint -> {}
            is DomBlueprintOutSlot -> {}
            else -> addOutput("out", domBuilder)
        }
        domBuilder.slots.forEach { (name, domSlot) ->
            addOutput(name, domSlot)
        }
    }

    fun removeNode(node: BlueprintNode) {
        if (node == start) return
        node.inputs.forEach { it.links.toList().forEach { it.unlink() } }
        node.outputs.forEach { it.links.toList().forEach { it.unlink() } }
        nodes.remove(node)
        outNodes.remove(node)
        // not removing from the ID registry, in case of undo
    }

    fun execute(
        rootNode: Node,
        stylesheet: Stylesheet = defaultStyle,
        seed: Long = 0,
        maxRecursions: Int = 4,
        cleanDummies: Boolean = true,
        hinting: Boolean = false,
        listeners: Collection<IDomListener> = emptyList(),
    ) {
        // copy the incoming stylesheet to keep it clean from generated rules:
        val finalStylesheet = stylesheet.copy()
        finalStylesheet.copyRules(internalStylesheet)
        val root = domRoot()
        root.addChild(start.domBuilder)
        listeners.forEach { root.addListener(it) }
        root.buildDom(finalStylesheet, seed, rootNode, maxRecursions, cleanDummies, hinting)
    }

    override fun toString(): String = name

    /** For all nodes in this BP, and in any referenced BP,
     * maps domBuilder to its parent BP node. */
    fun mapDomBuildersToNodes(): Map<DomBuilder, BlueprintNode> {
        val map = mutableMapOf<DomBuilder, BlueprintNode>()
        val visitedBPs = mutableSetOf<Blueprint>()
        val bpQueue = LinkedList<Blueprint>()
        bpQueue.add(this)
        while (bpQueue.isNotEmpty()) {
            val bp = bpQueue.removeFirst()
            visitedBPs.add(bp)
            bp.nodes.forEach {
                map[it.domBuilder] = it
                if (it.domBuilder is DomRunBlueprint) {
                    val nextBp = it.domBuilder.blueprint
                    if (nextBp !in visitedBPs) bpQueue.add(nextBp)
                }
            }
        }
        return map
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
    /** Nodes derived from this BP node will have this color.
     * For consistency, this color should always have 100% alpha internally. */
    var color: ColorRGBa = defaultColor.copy(),
) : WithID {
    val autoStyleClass = "${name.replace(' ', '_')}_${id}"
    val rule: Rule = Rule(select(autoStyleClass))

    private val _inputs = mutableListOf<BlueprintSlot.In>()
    val inputs: List<BlueprintSlot.In> get() = _inputs

    private val _outputs = mutableListOf<BlueprintSlot.Out>()
    val outputs: List<BlueprintSlot.Out> get() = _outputs

    val isCustomColor: Boolean get() = color != defaultColor

    private val extraClassList = linkedSetOf<String>()
    var extraStyleClass = ""
        set(value) {
            if (field != value) {
                domBuilder.styleClass.removeAll(extraClassList)
                field = value
                extraClassList.apply {
                    clear()
                    addAll(value.split(' '))
                    remove("")
                }
                domBuilder.styleClass.addAll(extraClassList)
            }
        }

    internal fun addInput(name: String): BlueprintSlot.In {
        val id = bp.slotIDs.newID()
        return BlueprintSlot.In(id, name, this).also { addInputSlot(it) }
    }

    fun addInputSlot(slot: BlueprintSlot.In) {
        _inputs.add(slot)
        bp.slotIDs.save(slot)
    }

    /** [domSlot] is the DomBuilder to which children will be attached via this slot */
    internal fun addOutput(
        name: String, domSlot: DomBuilder = domBuilder,
    ): BlueprintSlot.Out {
        return createOutputSlot(name, domSlot).also { addOutputSlot(it) }
    }

    /**
     * This method only creates a slot, but doesn't add it. Call [addOutputSlot].
     * [domSlot] is the DomBuilder to which children will be attached via this slot
     */
    fun createOutputSlot(
        name: String, domSlot: DomBuilder = domBuilder,
    ): BlueprintSlot.Out {
        val id = bp.slotIDs.newID()
        return BlueprintSlot.Out(id, name, this, domSlot)
    }

    fun addOutputSlot(slot: BlueprintSlot.Out) {
        _outputs.add(slot)
        bp.slotIDs.save(slot)
    }

    fun removeSlot(slot: BlueprintSlot) {
        slot.links.forEach { it.unlink() }
        bp.slotIDs.remove(slot)
        _inputs.remove(slot)
        _outputs.remove(slot)
    }

    fun removeAllOutputs() {
        outputs.toList().forEach { removeSlot(it) }
    }

    fun applyImNodesPos() {
        ImNodes.setNodeGridSpacePos(id, x, y)
    }

    companion object {
        val defaultColor = Colors.defaultGeneratedNodeBox.copy(a = 1f)
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
        /** When linking to this slot, children will be attached to this DomBuilder. */
        var domSlot: DomBuilder,
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

data class BlueprintLink(
    override val id: Int,
    val from: BlueprintSlot.Out,
    val to: BlueprintSlot.In,
) : WithID {
    fun unlink() {
        to.unlinkFrom(from)
    }
    /** Runs linking code again.
     * This can be useful if the DomBuilder on the slot has changed. */
    fun relink() {
        unlink()
        from.linkTo(to)
    }
}