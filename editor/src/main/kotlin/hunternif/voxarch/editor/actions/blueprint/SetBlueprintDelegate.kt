package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.logError
import hunternif.voxarch.editor.blueprint.*
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class SetBlueprintDelegate(
    private val node: BlueprintNode,
    delegateBp: Blueprint?,
) : HistoryAction(
    "Set blueprint delegate",
    FontAwesomeIcons.Code
) {
    /** Invalid if the node is not DonRunBlueprint */
    private var isValid = false
    private lateinit var refDomBuilder: DomRunBlueprint

    private val newDelegateBp: Blueprint = delegateBp ?: DomRunBlueprint.emptyBlueprint
    private lateinit var oldDelegateBp: Blueprint

    /** Need to remember the original slots, to be able to restore links */
    private lateinit var oldSlots: List<BlueprintSlot.Out>
    private lateinit var newSlots: List<BlueprintSlot.Out>
    private lateinit var oldLinks: List<BlueprintLink>

    private fun init(app: EditorAppImpl) {
        if (node.domBuilder !is DomRunBlueprint) {
            app.logError("Attempting to set delegate blueprint on invalid " +
                "blueprint node type: ${node.domBuilder::class.java.simpleName}")
            return
        }
        isValid = true
        refDomBuilder = node.domBuilder
        oldDelegateBp = refDomBuilder.blueprint

        // Must remove links from the output slots of the old BP:
        oldSlots = refDomBuilder.outSlots.toList()
        newSlots = newDelegateBp.outNodes.map {
            val slotSource = it.domBuilder as DomBlueprintOutSlot
            val slotInstance = DomBlueprintOutSlotInstance(slotSource)
            node.addOutput(slotSource.slotName, slotInstance)
        }
        oldLinks = mutableListOf<BlueprintLink>().apply {
            oldSlots.forEach { addAll(it.links) }
        }
    }

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::refDomBuilder.isInitialized) init(app)
        if (!isValid) return
        refDomBuilder.blueprint = newDelegateBp

        // Update slots:
        oldLinks.forEach { it.unlink() }
        setSlots(newSlots)

        // Update usages:
        app.state.blueprintRegistry.removeUsage(oldDelegateBp, node)
        app.state.blueprintRegistry.addUsage(newDelegateBp, node)
    }

    override fun revert(app: EditorAppImpl) {
        if (!isValid) return
        refDomBuilder.blueprint = oldDelegateBp

        // Update slots:
        setSlots(oldSlots)
        oldLinks.forEach { it.from.linkTo(it.to) }

        // Update usages:
        app.state.blueprintRegistry.removeUsage(newDelegateBp, node)
        app.state.blueprintRegistry.addUsage(oldDelegateBp, node)
    }

    private fun setSlots(slots: List<BlueprintSlot.Out>) {
        node.removeAllOutputs()
        refDomBuilder.outSlots.clear()
        slots.forEach { node.addOutputSlot(it) }
        refDomBuilder.outSlots.addAll(slots)
    }
}