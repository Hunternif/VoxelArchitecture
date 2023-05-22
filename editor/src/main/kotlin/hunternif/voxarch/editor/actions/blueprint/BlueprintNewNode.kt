package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.*
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintNewNode(
    private val bp: Blueprint,
    private val name: String,
    private val domBuilder: DomBuilder,
    private val x: Float,
    private val y: Float,
    private val autoLinkFrom: BlueprintSlot.Out?,
) : HistoryAction(
    "New blueprint node",
    FontAwesomeIcons.Code
) {
    lateinit var node: BlueprintNode
        private set

    /**
     * Map of slots to "Blueprint" nodes that reference this slot,
     * if [node] is an "out slot" node.
     */
    private lateinit var outSlots: Map<BlueprintSlot.Out, DomRunBlueprint>

    private fun init(app: EditorAppImpl) {
        node = bp.addNode(name, domBuilder, x, y)

        // If node type is "out slot", we must add a slot on all other BPs
        // that reference it:
        outSlots = mutableMapOf<BlueprintSlot.Out, DomRunBlueprint>().apply {
            if (domBuilder is DomBlueprintOutSlot) {
                val usage = app.state.blueprintLibrary.usage(bp)
                usage.delegators.forEach { node ->
                    val refDomBuilder = node.domBuilder as DomRunBlueprint
                    val domSlot = DomBlueprintOutSlotInstance(domBuilder)
                    val slot = node.createOutputSlot(domBuilder.slotName, domSlot)
                    put(slot, refDomBuilder)
                }
            }
        }
    }

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::node.isInitialized) init(app)
        bp.nodes.add(node)
        node.applyImNodesPos()
        if (node.inputs.isNotEmpty())
            autoLinkFrom?.linkTo(node.inputs[0])

        if (domBuilder is DomBlueprintOutSlot) {
            // Add new out slots on all BPs:
            outSlots.forEach { (slot, refDomBuilder) ->
                slot.node.addOutputSlot(slot)
                refDomBuilder.outSlots.add(slot)
            }
        }
    }

    override fun revert(app: EditorAppImpl) {
        bp.removeNode(node)

        if (domBuilder is DomBlueprintOutSlot) {
            // Remove the added slot on all BPs:
            outSlots.forEach { (slot, refDomBuilder) ->
                slot.node.removeSlot(slot)
                refDomBuilder.outSlots.remove(slot)
            }
        }
    }
}