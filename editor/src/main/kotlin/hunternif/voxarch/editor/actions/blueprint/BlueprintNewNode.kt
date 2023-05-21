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

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::node.isInitialized) {
            node = bp.addNode(name, domBuilder, x, y)
        }
        bp.nodes.add(node)
        node.applyImNodesPos()
        if (node.inputs.isNotEmpty())
            autoLinkFrom?.linkTo(node.inputs[0])

        if (domBuilder is DomBlueprintOutSlot) {
            // If node type is "out slot", we must add a slot on all other BPs
            // that could be referencing it:
            val usage = app.state.blueprintLibrary.usage(bp)
            usage.delegators.forEach { node ->
                val refDomBuilder = node.domBuilder as DomRunBlueprint
                val slot = node.addOutput(domBuilder.slotName, domBuilder)
                refDomBuilder.outSlots.add(slot)
            }
        }
    }

    override fun revert(app: EditorAppImpl) {
        bp.removeNode(node)
        if (domBuilder is DomBlueprintOutSlot) {
            // Remove the added slot on all BPs:
            val usage = app.state.blueprintLibrary.usage(bp)
            usage.delegators.forEach { node ->
                val refDomBuilder = node.domBuilder as DomRunBlueprint
                val slot = refDomBuilder.outSlots.first { it.domSlot === domBuilder }
                refDomBuilder.outSlots.remove(slot)
                node.removeSlot(slot)
            }
        }
    }
}