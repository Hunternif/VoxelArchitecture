package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.history.StackingAction
import hunternif.voxarch.editor.actions.logError
import hunternif.voxarch.editor.blueprint.*
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class RenameBlueprintOutSlot(
    private val node: BlueprintNode,
    newName: String,
) : HistoryAction(
    "Rename blueprint out slot",
    FontAwesomeIcons.Code
), StackingAction<RenameBlueprintOutSlot> {

    /** Invalid if the node is not DonRunBlueprint */
    private var isValid = false

    private lateinit var domBuilder: DomBlueprintOutSlot

    private lateinit var oldName: String
    private var newName: String = newName.trim()

    /**
     * Slots on "Blueprint" nodes that reference this slot.
     */
    private lateinit var slots: List<BlueprintSlot.Out>

    private fun init(app: EditorAppImpl) {
        if (node.domBuilder !is DomBlueprintOutSlot) {
            app.logError("Attempting to set delegate blueprint on invalid " +
                "blueprint node type: ${node.domBuilder::class.java.simpleName}")
            return
        }
        isValid = true
        domBuilder = node.domBuilder
        oldName = domBuilder.slotName

        val usage = app.state.blueprintLibrary.usage(node.bp)
        slots = mutableListOf<BlueprintSlot.Out>().apply {
            usage.delegators.forEach { refNode ->
                val slot = refNode.outputs.firstOrNull {
                    (it.domSlot as DomBlueprintOutSlotInstance).source === domBuilder
                }
                if (slot != null) add(slot)
            }
        }
    }

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::oldName.isInitialized) init(app)
        if (!isValid) return
        domBuilder.slotName = newName
        slots.forEach { it.name = newName }
    }

    override fun revert(app: EditorAppImpl) {
        if (!isValid) return
        domBuilder.slotName = oldName
        slots.forEach { it.name = oldName }
    }

    override fun update(nextAction: RenameBlueprintOutSlot) {
        this.newName = nextAction.newName
    }
}