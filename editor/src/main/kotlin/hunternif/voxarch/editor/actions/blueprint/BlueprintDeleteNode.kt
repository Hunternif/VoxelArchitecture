package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.*
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintDeleteNode(private val node: BlueprintNode) : HistoryAction(
    "Delete blueprint node",
    FontAwesomeIcons.TrashAlt
) {
    private lateinit var oldLinks: List<BlueprintLink>

    /**
     * Map of slots to "Blueprint" nodes that reference this slot,
     * if [node] is an "out slot" node.
     */
    private lateinit var outSlots: Map<BlueprintSlot.Out, DomRunBlueprint>

    private fun init(app: EditorAppImpl) {
        // If node type is "out slot", we must delete its slot on all
        // blueprints that reference it:
        outSlots = mutableMapOf<BlueprintSlot.Out, DomRunBlueprint>().apply {
            if (node.domBuilder is DomBlueprintOutSlot) {
                val usage = app.state.blueprintLibrary.usage(node.bp)
                usage.delegators.forEach { refNode ->
                    val refDomBuilder = refNode.domBuilder as DomRunBlueprint
                    val slot = refDomBuilder.outSlots.first {
                        (it.domSlot as DomBlueprintOutSlotInstance).source === node.domBuilder
                    }
                    put(slot, refDomBuilder)
                }
            }
        }

        oldLinks = mutableListOf<BlueprintLink>().apply {
            addAll(node.inputs.flatMap { it.links })
            addAll(node.outputs.flatMap { it.links })
            outSlots.keys.forEach { addAll(it.links) }
        }
    }

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::oldLinks.isInitialized) init(app)

        node.bp.removeNode(node)

        if (node.domBuilder is DomRunBlueprint) {
            val bp = node.domBuilder.blueprint
            app.state.blueprintRegistry.removeUsage(bp, node)
        }

        else if (node.domBuilder is DomBlueprintOutSlot) {
            // Remove the added slot on all BPs:
            outSlots.forEach { (slot, refDomBuilder) ->
                slot.node.removeSlot(slot)
                refDomBuilder.outSlots.remove(slot)
            }
        }
    }

    override fun revert(app: EditorAppImpl) {
        node.bp.nodes.add(node)

        if (node.domBuilder is DomRunBlueprint) {
            val bp = node.domBuilder.blueprint
            app.state.blueprintRegistry.addUsage(bp, node)
        }

        else if (node.domBuilder is DomBlueprintOutSlot) {
            // Restore out slots on all BPs:
            outSlots.forEach { (slot, refDomBuilder) ->
                slot.node.addOutputSlot(slot)
                refDomBuilder.outSlots.add(slot)
            }
        }
        oldLinks.forEach { it.from.linkTo(it.to) }
        node.applyImNodesPos()
    }
}